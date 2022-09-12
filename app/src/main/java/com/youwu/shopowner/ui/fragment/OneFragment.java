package com.youwu.shopowner.ui.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;


import com.youwu.shopowner.BR;
import com.youwu.shopowner.R;
import com.youwu.shopowner.UpData;
import com.youwu.shopowner.app.AppViewModelFactory;
import com.youwu.shopowner.bean.UpDateBean;
import com.youwu.shopowner.databinding.FragmentOneBinding;

import me.goldze.mvvmhabit.base.BaseFragment;

/**
 * 2022/09/12
 */

public class OneFragment extends BaseFragment<FragmentOneBinding,OneViewModel> {


    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_one;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public OneViewModel initViewModel() {
        //使用自定义的ViewModelFactory来创建ViewModel，如果不重写该方法，则默认会调用NetWorkViewModel(@NonNull Application application)构造方法
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getActivity().getApplication());
        return ViewModelProviders.of(this, factory).get(OneViewModel.class);
    }

    @Override
    public void initData() {
        super.initData();
        /**
         * 检查更新
         */
        viewModel.getAppVersion();

        //获取收银员信息
        viewModel.getMe();

    }

    @Override
    public void initViewObservable() {
        viewModel.IntegerEvent.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                switch (integer){

                }
            }
        });
        viewModel.upDateEvent.observe(this, new Observer<UpDateBean>() {
            @Override
            public void onChanged(UpDateBean upDateBean) {
                UpData.UpData(upDateBean);
            }
        });

    }

}
