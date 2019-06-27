package org.arxing.axmvvm.adapter.holder;

import android.view.View;

import org.arxing.axmvvm.ViewModel;
import org.arxing.lazyrecyclerview.LazyFooterViewHolder;

public class MvvmFooterViewHolder extends LazyFooterViewHolder {
    public ViewModel viewModel;

    MvvmFooterViewHolder(View itemView) {
        super(itemView);
    }

    public MvvmFooterViewHolder(ViewModel viewModel) {
        super(viewModel.getBinding().getRoot());
        this.viewModel = viewModel;
    }
}
