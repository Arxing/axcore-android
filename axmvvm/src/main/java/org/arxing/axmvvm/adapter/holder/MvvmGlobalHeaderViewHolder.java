package org.arxing.axmvvm.adapter.holder;

import android.view.View;

import org.arxing.axmvvm.ViewModel;
import org.arxing.lazyrecyclerview.LazyItemViewHolder;

public class MvvmGlobalHeaderViewHolder extends LazyItemViewHolder {
    public ViewModel viewModel;

    MvvmGlobalHeaderViewHolder(View itemView) {
        super(itemView);
    }

    public MvvmGlobalHeaderViewHolder(ViewModel viewModel) {
        super(viewModel.getBinding().getRoot());
        this.viewModel = viewModel;
    }
}
