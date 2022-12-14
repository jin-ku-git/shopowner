package me.goldze.mvvmhabit.base;


import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.mingle.widget.ShapeLoadingDialog;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;


import java.io.ByteArrayOutputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;

import me.goldze.mvvmhabit.R;
import me.goldze.mvvmhabit.base.BaseViewModel.ParameterField;
import me.goldze.mvvmhabit.bus.Messenger;
import me.goldze.mvvmhabit.utils.KLog;
import me.goldze.mvvmhabit.utils.MaterialShapeDialogUtils;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.Utils;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityBase;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityHelper;


/**
 * Created by goldze on 2017/6/15. * ????????????DataBinding????????????Activity
 * ??????????????????????????????????????????????????????BaseActivity, ??????????????????RxAppCompatActivity,??????LifecycleProvider??????????????????
 */
public abstract class BaseActivity<V extends ViewDataBinding, VM extends BaseViewModel> extends RxAppCompatActivity implements IBaseView, SwipeBackActivityBase {
    protected V binding;
    protected VM viewModel;
    private int viewModelId;
    private ShapeLoadingDialog dialog;

    private SwipeBackActivityHelper mHelper;

    private SwipeBackLayout mSwipeBackLayout;

    protected BaseActivity mActivity;

    public static ArrayList<Activity> mActivityList = new ArrayList<Activity>();

   public int widths;//?????????
    public int height;//?????????

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        mHelper = new SwipeBackActivityHelper(this);
        mHelper.onActivityCreate();
        //???????????????????????????
        initParam();
        //??????????????????Databinding???ViewModel??????
        initViewDataBinding(savedInstanceState);
        //?????????ViewModel???View???????????????????????????
        registorUIChangeLiveDataCallBack();
        //???????????????????????????
        initData();
        //??????????????????????????????????????????ViewModel?????????View??????????????????
        initViewObservable();
        //??????RxBus
        viewModel.registerRxBus();


        //????????????
        /***********************************************/


        // ??????????????????????????????????????????????????????
//        setSwipeBackEnable(true);
        mSwipeBackLayout = getSwipeBackLayout();
        // ??????????????????????????????EDGE_LEFT, EDGE_RIGHT, EDGE_ALL, EDGE_BOTTOM
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);

        // ????????????????????????????????????????????????????????????????????????touch????????????????????????????????????
        mSwipeBackLayout.setEdgeSize(100);

        /***********************************************/


        addActivity(mActivity);

        getScreenSize();
    }

    /**
     * ?????????????????????
     */
    private void getScreenSize() {
        //??????????????????
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        widths = size.x;
        height = size.y;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //??????Messenger??????
        Messenger.getDefault().unregister(viewModel);
        if (viewModel != null) {
            viewModel.removeRxBus();
        }
        if (binding != null) {
            binding.unbind();
        }
    }
    /**
     * onCreate()?????????
     *
     * @param activity
     */
    public static void addActivity(Activity activity){
        //???????????????????????????????????????????????????????????????
        if (!mActivityList.contains(activity)){
            mActivityList.add(activity);
        }
    }
    /**
     * onDestroy()?????????
     * @param activity
     */
    public static void removeActivity(Activity activity){
        mActivityList.remove(activity);
    }

    /**
     * ????????????Activity
     */
    public static void finishAllActivity(){
        for (Activity activity : mActivityList){
            if (!activity.isFinishing()){
                activity.finish();
            }
        }
    }


    /**
     * ????????????
     */
    private void initViewDataBinding(Bundle savedInstanceState) {
        //DataBindingUtil????????????project???build????????? dataBinding {enabled true }, ????????????????????????android.databinding???
        binding = DataBindingUtil.setContentView(this, initContentView(savedInstanceState));
        viewModelId = initVariableId();
        viewModel = initViewModel();
        if (viewModel == null) {
            Class modelClass;
            Type type = getClass().getGenericSuperclass();
            if (type instanceof ParameterizedType) {
                modelClass = (Class) ((ParameterizedType) type).getActualTypeArguments()[1];
            } else {
                //????????????????????????????????????????????????BaseViewModel
                modelClass = BaseViewModel.class;
            }
            viewModel = (VM) createViewModel(this, modelClass);
        }
        //??????ViewModel
        binding.setVariable(viewModelId, viewModel);
        //??????LiveData??????xml??????????????????UI???????????????
        binding.setLifecycleOwner(this);
        //???ViewModel??????View?????????????????????
        getLifecycle().addObserver(viewModel);
        //??????RxLifecycle????????????
        viewModel.injectLifecycleProvider(this);
    }

    //????????????
    public void refreshLayout() {
        if (viewModel != null) {
            binding.setVariable(viewModelId, viewModel);
        }
    }


    /**
     * =====================================================================
     **/
    //??????ViewModel???View?????????UI????????????
    protected void registorUIChangeLiveDataCallBack() {
        //?????????????????????
        viewModel.getUC().getShowDialogEvent().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String title) {
                showDialog(title);
            }
        });
        //?????????????????????
        viewModel.getUC().getDismissDialogEvent().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(@Nullable Void v) {
                dismissDialog();
            }
        });
        //???????????????
        viewModel.getUC().getStartActivityEvent().observe(this, new Observer<Map<String, Object>>() {
            @Override
            public void onChanged(@Nullable Map<String, Object> params) {

                Class<?> clz = (Class<?>) params.get(ParameterField.CLASS);
                Bundle bundle = (Bundle) params.get(ParameterField.BUNDLE);
                startActivity(clz, bundle);

            }
        });
        //??????ContainerActivity
        viewModel.getUC().getStartContainerActivityEvent().observe(this, new Observer<Map<String, Object>>() {
            @Override
            public void onChanged(@Nullable Map<String, Object> params) {
                String canonicalName = (String) params.get(ParameterField.CANONICAL_NAME);
                Bundle bundle = (Bundle) params.get(ParameterField.BUNDLE);
                startContainerActivity(canonicalName, bundle);
            }
        });
        //????????????
        viewModel.getUC().getFinishEvent().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(@Nullable Void v) {

                finish();

            }
        });
        //???????????????
        viewModel.getUC().getOnBackPressedEvent().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(@Nullable Void v) {
                onBackPressed();
            }
        });

    }

    public void showDialog(String title) {
        if (dialog != null) {
            dialog = new ShapeLoadingDialog.Builder(this)
                    .loadText(title)
                    .build();
            dialog.show();
        } else {
            ShapeLoadingDialog.Builder builder = MaterialShapeDialogUtils.showIndeterminateProgressDialog(this, title);
            dialog = builder.show();
        }
    }

    public void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

//    @Override
//    public void finish() {
//        super.finish();
//        this.overridePendingTransition(R.anim.activity_down_in, R.anim.activity_down_out);
//    }
//
//    @Override
//    public void startActivity(Intent intent) {
//        super.startActivity(intent);
//        overridePendingTransition(R.anim.activity_down_in, R.anim.activity_down_out);
//    }

    /**
     * ????????????
     *
     * @param clz ??????????????????Activity???
     */
    public void startActivity(Class<?> clz) {
        startActivity(new Intent(this, clz));
    }

    /**
     * ????????????
     *
     * @param clz    ??????????????????Activity???
     * @param bundle ????????????????????????
     */
    public void startActivity(Class<?> clz, Bundle bundle) {
        Intent intent = new Intent(this, clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * ??????????????????
     *
     * @param canonicalName ????????? : Fragment.class.getCanonicalName()
     */
    public void startContainerActivity(String canonicalName) {
        startContainerActivity(canonicalName, null);
    }

    /**
     * ??????????????????
     *
     * @param canonicalName ????????? : Fragment.class.getCanonicalName()
     * @param bundle        ????????????????????????
     */
    public void startContainerActivity(String canonicalName, Bundle bundle) {
        Intent intent = new Intent(this, ContainerActivity.class);
        intent.putExtra(ContainerActivity.FRAGMENT, canonicalName);
        if (bundle != null) {
            intent.putExtra(ContainerActivity.BUNDLE, bundle);
        }
        startActivity(intent);
    }

    /**
     * =====================================================================
     **/
    @Override
    public void initParam() {

    }

    /**
     * ??????????????????
     *
     * @return ??????layout???id
     */
    public abstract int initContentView(Bundle savedInstanceState);

    /**
     * ?????????ViewModel???id
     *
     * @return BR???id
     */
    public abstract int initVariableId();

    /**
     * ?????????ViewModel
     *
     * @return ??????BaseViewModel???ViewModel
     */
    public VM initViewModel() {
        return null;
    }

    @Override
    public void initData() {

    }

    @Override
    public void initViewObservable() {

    }

    /**
     * ??????ViewModel
     *
     * @param cls
     * @param <T>
     * @return
     */
    public <T extends ViewModel> T createViewModel(FragmentActivity activity, Class<T> cls) {
        return ViewModelProviders.of(activity).get(cls);
    }


    /**
     * ???????????????????????????0
     *
     * @param s
     * @return
     */
    public static String subZeroAndDot(String s) {
        if (s.indexOf(".") > 0) {
            s = s.replaceAll("0+?$", "");//???????????????0
            s = s.replaceAll("[.]$", "");//??????????????????.?????????
        }
        return s;
    }

    public String bitmapToString(Bitmap bitmap) {
        //???Bitmap??????????????????
        String string = null;
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bStream);
        byte[] bytes = bStream.toByteArray();
        string = Base64.encodeToString(bytes, Base64.DEFAULT);
        return string;
    }


    /**
     * ????????????
     */
    public void showDatePickerDialog(final TextView tv) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, 0, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                showTimePickerDialog(tv, year + "." + (monthOfYear + 1) + "." + dayOfMonth);
            }
        }
                , calendar.get(Calendar.YEAR)
                , calendar.get(Calendar.MONTH)
                , calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    /**
     * ????????????
     *
     * @param tv
     * @param date
     */
    public void showTimePickerDialog(final TextView tv, final String date) {
        Calendar calendar = Calendar.getInstance();
        new TimePickerDialog(this, 0,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        tv.setText(date + " " + hourOfDay + ":" + minute);
                    }
                }
                , calendar.get(Calendar.HOUR_OF_DAY)
                , calendar.get(Calendar.MINUTE)
                , true).show();
    }

    public Long getTimestamp(String time) {
        Long timestamp = null;
        try {
            timestamp = new SimpleDateFormat("yyyy.MM.dd HH:mm").parse(time).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timestamp / 1000;
    }

    /*
     * ???????????????????????????
     */
    public static String dateToStamp(String s) throws ParseException {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        Date date = null;
        try {
            date = simpleDateFormat.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long ts = date.getTime();
        res = String.valueOf(ts);
        String b = res.substring(0, 10);  //???????????????
        return b;
    }

    public static String date2TimeStamp(String date, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return String.valueOf(sdf.parse(date).getTime() / 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

//    //????????????EditText???  ??????????????????
//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        View v = getCurrentFocus();
//
//        if (v != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
//                v instanceof EditText &&
//                !v.getClass().getName().startsWith("android.webkit.")) {
//            int[] sourceCoordinates = new int[2];
//            v.getLocationOnScreen(sourceCoordinates);
//            float x = ev.getRawX() + v.getLeft() - sourceCoordinates[0];
//            float y = ev.getRawY() + v.getTop() - sourceCoordinates[1];
//
//            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom()) {
//                hideKeyboard(this);
//            }
//
//        }
//        return super.dispatchTouchEvent(ev);
//    }
//
//    private void hideKeyboard(Activity activity) {
//        if (activity != null && activity.getWindow() != null) {
//            activity.getWindow().getDecorView();
//            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
//            if (imm != null) {
//                imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
//            }
//        }
//    }

    /**
     * ???????????????JSON?????????
     *
     * @return ???????????????JSON?????????
     */
    public static String toPrettyFormat(String json) {
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(json).getAsJsonObject();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(jsonObject);
    }

    /**
     * ???????????????
     *
     * @return ????????????????????????
     */
    public String getVersion() {
        try {
            PackageManager manager = getPackageManager();
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "????????????????????????";
        }
    }

    /**
     * ????????????????????????????????????
     *
     * @param phoneNum ????????????
     */

    public void callPhone(String phoneNum) {

        //android6????????????????????????
        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {Manifest.permission.CALL_PHONE};
            //????????????????????????
            for (String str : permissions) {
                if (checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //????????????
                    requestPermissions(permissions, REQUEST_CODE_CONTACT);
                    return;
                }
            }
        }
        //???????????????????????????Intent.ACTION_CALL??????Intent.ACTION_DIAL??????????????????????????????????????????????????????
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        startActivity(intent);
    }

    /**
     * ?????????????????????????????????
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static int getTimeCompareSize(String startTime, String endTime) {
        int i = 0;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//???-???-??? ???-???
        try {
            Date date1 = dateFormat.parse(startTime);//????????????
            Date date2 = dateFormat.parse(endTime);//????????????
            // 1 ?????????????????????????????? 2 ????????????????????????????????? 3 ??????????????????????????????
            if (date2.getTime() < date1.getTime()) {
                i = 1;
            } else if (date2.getTime() == date1.getTime()) {
                i = 2;
            } else if (date2.getTime() > date1.getTime()) {
                //??????????????????????????????.
                i = 3;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return i;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mHelper.onPostCreate();
    }

    @Override
    public View findViewById(int id) {
        View v = super.findViewById(id);
        if (v == null && mHelper != null)
            return mHelper.findViewById(id);
        return v;
    }

    @Override
    public SwipeBackLayout getSwipeBackLayout() {
        return mHelper.getSwipeBackLayout();
    }

    @Override
    public void setSwipeBackEnable(boolean enable) {
        getSwipeBackLayout().setEnableGesture(enable);
    }

    @Override
    public void scrollToFinishActivity() {
        Utils.convertActivityToTranslucent(this);
        getSwipeBackLayout().scrollToFinishActivity();
    }

    /**
     * ???????????? dimens (dp)
     *
     * @param context
     * @param id      ??????id
     * @return
     */
    public float getDimens(Context context, int id) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        float px = context.getResources().getDimension(id);
        return px / dm.density;
    }

    /**
     * dp???px
     *
     * @param context
     * @param dp
     * @return
     */
    public int dpToPx(Context context, float dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int) ((dp * displayMetrics.density) + 0.5f);
    }



    /**
     * ??????????????????
     * @return
     */
    public String getDate(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy???MM???dd??? HH:mm:ss");// HH:mm:ss
        //??????????????????
        Date date = new Date(System.currentTimeMillis());

        return simpleDateFormat.format(date);
    }


    /**
     ??????dispatchTouchEvent
     * ?????????????????????????????????????????????
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            // ???????????????????????????View???
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                //???????????????????????????
                InputMethodManager imm = (InputMethodManager)getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * ?????????????????????????????????????????????
     *
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = { 0, 0 };
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // ??????EditText????????????????????????
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * ????????????
     *
     * @param et ????????????
     */
    public void showInput(final EditText et) {
        et.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
    }

    /**
     * ????????????
     */
    protected void hideInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View v = getWindow().peekDecorView();
        if (null != v) {
            imm.showSoftInput(v, InputMethodManager.SHOW_FORCED);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }


    /**
     * ???????????????
     *
     *
     * @param context ?????????
     * @return ??????????????????true???????????????false
     */
    public static boolean isPad(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        double x = Math.pow(dm.widthPixels / dm.xdpi, 2);
        double y = Math.pow(dm.heightPixels / dm.ydpi, 2);
        double screenInches = Math.sqrt(x + y); // ????????????
        return screenInches >= 7.0;
    }

    protected void hideBottomUIMenu() {
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                setHideVirtualKey(getWindow());
            }
        });
        //?????????????????????????????????
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }
    public void setHideVirtualKey(Window window){
        //??????????????????
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE|
                //???????????????????????????
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|
                //??????
                View.SYSTEM_UI_FLAG_FULLSCREEN|
                //???????????????
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        if (Build.VERSION.SDK_INT>=19){
            uiOptions |= 0x00001000;
        }else{
            uiOptions |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
        }
        window.getDecorView().setSystemUiVisibility(uiOptions);
    }


    /**
     * ??????????????????????????????
     */
    public static boolean isNumber(String value) {
        return isInteger(value) || isDouble(value);
    }
    /**
     * ??????????????????????????????
     */
    public static boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * ?????????????????????????????????
     */
    public static boolean isDouble(String value) {
        try {
            Double.parseDouble(value);
            if (value.contains("."))
                return true;
            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * ??????????????????????????????/??????????????????
     *
     * @param isShow true:?????????false?????????
     */
    public void controlBottomNavigation(boolean isShow) {
        //??????????????????
        if (isShow) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        } else {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE);
        }
    }

    /** ?????????????????????????????????*/
    public String getSerialNumber() {
        String serial = null;
        try {
            serial = android.os.Build.SERIAL;
        } catch (Exception e) { }
        return serial;
    }



}
