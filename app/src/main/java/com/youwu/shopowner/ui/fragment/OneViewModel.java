package com.youwu.shopowner.ui.fragment;

import android.app.Application;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.youwu.shopowner.app.AppApplication;
import com.youwu.shopowner.bean.UpDateBean;
import com.youwu.shopowner.data.DemoRepository;
import com.youwu.shopowner.toast.RxToast;
import com.youwu.shopowner.ui.fragment.bean.UserBean;


import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.binding.command.BindingAction;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.http.BaseBean;
import me.goldze.mvvmhabit.http.ResponseThrowable;
import me.goldze.mvvmhabit.utils.KLog;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;

import static com.youwu.shopowner.app.AppApplication.toPrettyFormat;


/**
 * 2022/09/12
 */

public class OneViewModel extends BaseViewModel<DemoRepository> {

//    public ObservableField<UserBean.RoleBean> entity = new ObservableField<>();
    public SingleLiveEvent<Integer> IntegerEvent = new SingleLiveEvent<>();

    //更新监听
    public SingleLiveEvent<UpDateBean> upDateEvent = new SingleLiveEvent<>();


    public OneViewModel(@NonNull Application application, DemoRepository repository) {
        super(application,repository);
    }




    /**
     * 检查更新
     **/
    public void getAppVersion() {

        model.GET_APP_VERSION()
                .compose(RxUtils.schedulersTransformer()) //线程调度
                .compose(RxUtils.exceptionTransformer())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        showDialog();
                    }
                })
                .subscribe(new DisposableObserver<BaseBean<Object>>() {
                    @Override
                    public void onNext(BaseBean response) {
                        String submitJson = new Gson().toJson(response.data);
                        if (response.isOk()){
                            UpDateBean upDateBean = JSON.parseObject(toPrettyFormat(submitJson), UpDateBean.class);
                            upDateEvent.setValue(upDateBean);
                        }else {
                            RxToast.normal(response.getMessage());
                        }
                    }
                    @Override
                    public void onError(Throwable throwable) {
                        //关闭对话框
                        dismissDialog();
                        if (throwable instanceof ResponseThrowable) {
                            ToastUtils.showShort(((ResponseThrowable) throwable).message);
                        }
                    }
                    @Override
                    public void onComplete() {
                        //关闭对话框
                        dismissDialog();
                    }
                });
    }

    /**
     * 获取个人信息
     */
    public void getMe() {
        model.GET_ME()
                .compose(RxUtils.schedulersTransformer()) //线程调度
                .compose(RxUtils.exceptionTransformer())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
//                        showDialog();
                    }
                })
                .subscribe(new DisposableObserver<BaseBean<Object>>() {
                    @Override
                    public void onNext(BaseBean<Object> response) {
                        if (response.isOk()){
                            String submitJsonData = new Gson().toJson(response.data);

                            UserBean userBean= JSON.parseObject(toPrettyFormat(submitJsonData), UserBean.class);

                            AppApplication.spUtils.put("StoreId", userBean.getStore_id()+"");
                            AppApplication.spUtils.put("StoreName", userBean.getStore_name());
                            AppApplication.spUtils.put("Id", userBean.getId()+"");
                            AppApplication.spUtils.put("Name", userBean.getName());
                            AppApplication.spUtils.put("topic", userBean.getTopic());
                            AppApplication.spUtils.put("tel", userBean.getTel());

                            AppApplication.spUtils.put("is_order", userBean.getIs_order());
                            AppApplication.spUtils.put("start_time", userBean.getStart());
                            AppApplication.spUtils.put("end_time", userBean.getEnd());


                        }else {
                            RxToast.normal(response.getMessage());
                        }
                    }
                    @Override
                    public void onError(Throwable throwable) {
                        //关闭对话框
//                        dismissDialog();
                        if (throwable instanceof ResponseThrowable) {
                            ToastUtils.showShort(((ResponseThrowable) throwable).message);
                        }
                    }
                    @Override
                    public void onComplete() {
                        //关闭对话框
//                        dismissDialog();
                    }
                });
    }


}
