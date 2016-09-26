package com.kirer;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.kirer.utils.TUtil;


/**
 * Created by xinwb on 2016/8/5.
 */
public abstract class KActivity<T extends ViewDataBinding, E extends KViewModel> extends AppCompatActivity {

    public T binding;
    public E vm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, getLayoutId());
        initView();
        vm = TUtil.getVM(this, binding);
        binding.setVariable(com.kirer.BR.VM, vm);
    }

    public abstract int getLayoutId();

    public abstract void initView();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (android.R.id.home == item.getItemId()) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
