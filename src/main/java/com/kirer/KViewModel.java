package com.kirer;

import android.content.Context;
import android.databinding.ViewDataBinding;

import com.kirer.utils.LUtil;


/**
 * Created by xinwb on 2016/8/10.
 */
public abstract class KViewModel<T extends ViewDataBinding>  {

    protected T binding;
    protected Context mContext;

    public KViewModel(T binding) {
        LUtil.d("VM Construct from TUtil");
        this.binding = binding;
        this.mContext = binding.getRoot().getContext();
    }
}
