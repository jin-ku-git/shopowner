package com.youwu.shopowner.ui.main;


import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;


import com.youwu.shopowner.BR;
import com.youwu.shopowner.R;
import com.youwu.shopowner.app.AppViewModelFactory;
import com.youwu.shopowner.databinding.ActivityMainBinding;
import com.youwu.shopowner.ui.fragment.FourFragment;
import com.youwu.shopowner.ui.fragment.OneFragment;
import com.youwu.shopowner.ui.fragment.ThreeFragment;
import com.youwu.shopowner.ui.fragment.TwoFragment;
import com.youwu.shopowner.utils_view.StatusBarUtil;

import me.goldze.mvvmhabit.base.BaseActivity;

/**
 * 2021/12/12
 * 首页
 * 金库
 */

public class MainActivity extends BaseActivity<ActivityMainBinding, MainViewModel> {


    private OneFragment mOneFragment;
    private TwoFragment mTowFragment;
    private ThreeFragment mThreeFragment;
    private FourFragment mFourFragment;

    private FragmentManager manager;
    private FragmentTransaction transaction;

//    @Override
//    public void initParam() {
//        super.initParam();
//
//    }

    @Override
    public MainViewModel initViewModel() {
        //使用自定义的ViewModelFactory来创建ViewModel，如果不重写该方法，则默认会调用LoginViewModel(@NonNull Application application)构造方法
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getApplication());
        return ViewModelProviders.of(this, factory).get(MainViewModel.class);
    }

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_main;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }
    @Override
    public void initParam() {
        super.initParam();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void initData() {
        StatusBarUtil.setRootViewFitsSystemWindows(this, true);
        //修改状态栏是状态栏透明
        StatusBarUtil.setTransparentForWindow(this);
        StatusBarUtil.setDarkMode(this);//使状态栏字体变为黑色
        // 可以调用该方法，设置不允许滑动退出
        setSwipeBackEnable(false);
        setSwPage(1);
    }

    @Override
    public void initViewObservable() {

        //注册文件下载的监听
        viewModel.IntegerEvent.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                switch (integer){
                    case 1:
                        binding.oneHome.setonSelected(true);
                        binding.twoHome.setonSelected(false);
                        binding.threeHome.setonSelected(false);
                        binding.fourHome.setonSelected(false);
                        setSwPage(1);
                        break;
                    case 2:
                        binding.twoHome.setonSelected(true);
                        binding.oneHome.setonSelected(false);
                        binding.threeHome.setonSelected(false);
                        binding.fourHome.setonSelected(false);
                        setSwPage(2);
                        break;
                    case 3:
                        binding.threeHome.setonSelected(true);
                        binding.oneHome.setonSelected(false);
                        binding.twoHome.setonSelected(false);
                        binding.fourHome.setonSelected(false);
                        setSwPage(3);
                        break;
                    case 4:
                        binding.fourHome.setonSelected(true);
                        binding.oneHome.setonSelected(false);
                        binding.twoHome.setonSelected(false);
                        binding.threeHome.setonSelected(false);
                        setSwPage(4);
                        break;

                }
            }
        });
    }

    @SuppressLint("ResourceAsColor")
    public void setSwPage(int i) {

        //获取FragmentManager对象
        manager = getSupportFragmentManager();
        //获取FragmentTransaction对象
        transaction = manager.beginTransaction();
        //先隐藏所有的Fragment
        hideFragments(transaction);
        switch (i) {
            case 1:
                if (mOneFragment == null) {
                    mOneFragment = new OneFragment();
                    transaction.add(R.id.frame, mOneFragment);
                } else {
                    //对应的Fragment已经实例化，则直接显示出来
                    transaction.show(mOneFragment);
                }
                break;
            case 2:
                if (mTowFragment == null) {
                    mTowFragment = new TwoFragment();
                    transaction.add(R.id.frame, mTowFragment);
                } else {
                    //对应的Fragment已经实例化，则直接显示出来
                    transaction.show(mTowFragment);
                }
                break;
            case 3:
                if (mThreeFragment == null) {
                    mThreeFragment = new ThreeFragment();
                    transaction.add(R.id.frame, mThreeFragment);
                } else {
                    //对应的Fragment已经实例化，则直接显示出来
                    transaction.show(mThreeFragment);
                }
                break;
            case 4:
                if (mFourFragment == null) {
                    mFourFragment = new FourFragment();
                    transaction.add(R.id.frame, mFourFragment);
                } else {
                    //对应的Fragment已经实例化，则直接显示出来
                    transaction.show(mFourFragment);
                }
                break;
        }
        transaction.commit();
    }

    //将全部Fragment隐藏
    private void hideFragments(FragmentTransaction transaction) {
        if (mOneFragment != null) {
            transaction.hide(mOneFragment);
        }
        if (mTowFragment != null) {
            transaction.hide(mTowFragment);
        }
        if (mThreeFragment != null) {
            transaction.hide(mThreeFragment);
        }
        if (mFourFragment != null) {
            transaction.hide(mFourFragment);
        }
    }
    //声明一个long类型变量：用于存放上一点击“返回键”的时刻
    private long mExitTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //判断用户是否点击了“返回键”
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            //与上次点击返回键时刻作差
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                //大于2000ms则认为是误操作，使用Toast进行提示

                View toastRoot = getLayoutInflater().inflate(R.layout.my_toast, null);
                Toast toast = new Toast(this);
                toast.setView(toastRoot);
                TextView tv = (TextView) toastRoot.findViewById(R.id.TextViewInfo);
                tv.setText("再按一次退出程序");
                toast.setGravity(Gravity.BOTTOM, 0, 150);
                toast.show();
                //并记录下本次点击“返回键”的时刻，以便下次进行判断
                mExitTime = System.currentTimeMillis();
            } else {
                //小于2000ms则认为是用户确实希望退出程序-调用System.exit()方法进行退出
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }
}
