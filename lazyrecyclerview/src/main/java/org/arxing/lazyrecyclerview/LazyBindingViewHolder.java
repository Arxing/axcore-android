package org.arxing.lazyrecyclerview;

import android.view.View;

public abstract class LazyBindingViewHolder<TData> extends LazyItemViewHolder {

    public LazyBindingViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void bindData(TData data);
}
