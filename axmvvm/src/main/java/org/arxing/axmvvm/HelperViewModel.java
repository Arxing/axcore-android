package org.arxing.axmvvm;

import android.databinding.ViewDataBinding;

public class HelperViewModel<TBinding extends ViewDataBinding, THelper extends ViewModelHelper> extends ViewModel<TBinding> {
    protected THelper helper;

    public HelperViewModel(TBinding binding, THelper helper) {
        super(binding, helper);
        this.helper = helper;
    }

    public THelper getHelper() {
        return helper;
    }
}
