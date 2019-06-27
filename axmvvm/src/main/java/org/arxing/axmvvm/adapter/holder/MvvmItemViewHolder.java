package org.arxing.axmvvm.adapter.holder;

import android.view.View;

import org.arxing.axmvvm.ViewModel;
import org.arxing.lazyrecyclerview.LazyItemViewHolder;

public class MvvmItemViewHolder extends LazyItemViewHolder {
    public ViewModel viewModel;

    MvvmItemViewHolder(View itemView) {
        super(itemView);
    }

    public MvvmItemViewHolder(ViewModel viewModel) {
        super(viewModel.getBinding().getRoot());
        this.viewModel = viewModel;
    }
}
