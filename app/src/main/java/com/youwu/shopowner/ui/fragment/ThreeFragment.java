package com.youwu.shopowner.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;


import com.youwu.shopowner.BR;
import com.youwu.shopowner.R;

import me.goldze.mvvmhabit.base.BaseFragment;

/**
 * 2022/09/12
 */

public class ThreeFragment extends BaseFragment {
    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_three;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

}
