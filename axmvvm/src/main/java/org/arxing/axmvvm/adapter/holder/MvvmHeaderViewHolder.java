package org.arxing.axmvvm.adapter.holder;

import android.view.View;

import org.arxing.axmvvm.ViewModel;
import org.arxing.lazyrecyclerview.LazyHeaderViewHolder;

public class MvvmHeaderViewHolder extends LazyHeaderViewHolder {
    public ViewModel viewModel;

    MvvmHeaderViewHolder(View itemView) {
        super(itemView);
    }

    public MvvmHeaderViewHolder(ViewModel viewModel) {
        super(viewModel.getBinding().getRoot());
        this.viewModel = viewModel;
    }
}
