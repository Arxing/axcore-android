package org.arxing.axmvvm;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public abstract class MvvmActivity<TBinding extends ViewDataBinding, TViewModel extends ViewModel<TBinding>> extends AppCompatActivity
        implements ViewModelHelper {

    protected TBinding binding;
    protected TViewModel viewModel;

    public abstract @LayoutRes int getLayout();

    public abstract TViewModel createViewModel(TBinding binding);

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, getLayout());
        viewModel = createViewModel(binding);
    }

    @Override public Context getContext() {
        return this;
    }

    public TViewModel getViewModel() {
        return viewModel;
    }
}
