package org.arxing.axmvvm;

import android.content.Context;
import android.databinding.ViewDataBinding;

import org.arxing.axutils_android.Logger;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"unchecked", "WeakerAccess", "unused"})
public abstract class ViewModel<TBinding extends ViewDataBinding> implements ViewModelHelper {
    protected TBinding binding;
    protected Logger logger = new Logger(getClass().getSimpleName());
    private ViewModelHelper helper;
    private Map<String, ViewModel> children = new HashMap<>();
    private ViewModel parent = null;

    public ViewModel(TBinding binding, ViewModelHelper helper) {
        this.binding = binding;
        this.helper = helper;
    }

    @Override public Context getContext() {
        return helper.getContext();
    }

    public void refresh(Object... params) {
    }

    public void addChild(String tag, ViewModel viewModel) {
        viewModel.parent = this;
        this.children.put(tag, viewModel);
    }

    public void addChild(ViewModel viewModel) {
        addChild(viewModel.getClass().getName(), viewModel);
    }

    public <T extends ViewModel> T getChild(String tag) {
        return (T) children.get(tag);
    }

    public <T extends ViewModel> T getChild(Class type) {
        return getChild(type.getName());
    }

    public <T extends ViewModel> T getParent() {
        return (T) parent;
    }

    public TBinding getBinding() {
        return binding;
    }
}
