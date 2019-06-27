package org.arxing.axmvvm;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.CallSuper;

import org.arxing.pagemanager.PageFragment;


public abstract class MvvmFragment<TBinding extends ViewDataBinding, TViewModel extends ViewModel<TBinding>> extends PageFragment implements ViewModelHelper {
    protected TBinding binding;
    protected TViewModel viewModel;

    protected abstract TViewModel createViewModel(TBinding binding);

    @CallSuper @Override protected void onInit() {
        binding = DataBindingUtil.bind(getView());
        viewModel = createViewModel(binding);
    }
}
