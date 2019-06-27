package org.arxing.axmvvm.adapter.holder;

import android.view.View;

import org.arxing.axmvvm.ViewModel;
import org.arxing.lazyrecyclerview.LazyItemViewHolder;

public class MvvmGlobalFooterViewHolder extends LazyItemViewHolder {
    public ViewModel viewModel;

    MvvmGlobalFooterViewHolder(View itemView) {
        super(itemView);
    }

    public MvvmGlobalFooterViewHolder(ViewModel viewModel) {
        super(viewModel.getBinding().getRoot());
        this.viewModel = viewModel;
    }
}
