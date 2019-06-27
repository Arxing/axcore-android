package org.arxing.axmvvm;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.arxing.axutils_android.Logger;

public abstract class MvvmDialogFragment<TBinding extends ViewDataBinding, TViewModel extends ViewModel<TBinding>> extends DialogFragment implements ViewModelHelper {
    protected TBinding binding;
    protected TViewModel viewModel;
    protected Logger logger = new Logger(getClass().getSimpleName());

    protected MvvmDialogFragment() {
    }

    protected abstract TViewModel createViewModel(TBinding binding);

    public abstract @LayoutRes int getLayoutRes();

    public abstract String getFragmentTag();

    public abstract Dialog getDialog(Bundle savedInstanceState);

    public abstract boolean useDialog();

    protected void onInit() {
    }

    @CallSuper protected void onConfigDialog() {
        if (useDialog()) {

        } else {
            setStyle(STYLE_NO_TITLE, getTheme());
        }
    }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (useDialog())
            return super.onCreateView(inflater, container, savedInstanceState);
        else {
            binding = DataBindingUtil.inflate(inflater, getLayoutRes(), container, false);
            viewModel = createViewModel(binding);
            onInit();
            return binding.getRoot();
        }
    }

    @NonNull @Override public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return useDialog() ? getDialog(savedInstanceState) : super.onCreateDialog(savedInstanceState);
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onConfigDialog();
    }
}
