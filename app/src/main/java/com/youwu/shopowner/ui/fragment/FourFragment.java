package com.youwu.shopowner.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;


import com.youwu.shopowner.BR;
import com.youwu.shopowner.R;
import com.youwu.shopowner.app.AppApplication;
import com.youwu.shopowner.app.AppViewModelFactory;
import com.youwu.shopowner.app.UserUtils;
import com.youwu.shopowner.databinding.FragmentFourBinding;

import me.goldze.mvvmhabit.base.BaseFragment;

/**
 * 2022/09/12
 */

public class FourFragment extends BaseFragment<FragmentFourBinding,FourViewModel> {
    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_four;
    }

    @Override
    public FourViewModel initViewModel() {
        //使用自定义的ViewModelFactory来创建ViewModel，如果不重写该方法，则默认会调用NetWorkViewModel(@NonNull Application application)构造方法
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getActivity().getApplication());
        return ViewModelProviders.of(this, factory).get(FourViewModel.class);
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        viewModel.nameEvent.set(AppApplication.spUtils.getString("Name"));
        viewModel.telEvent.set("账号："+AppApplication.spUtils.getString("tel"));

    }
}
