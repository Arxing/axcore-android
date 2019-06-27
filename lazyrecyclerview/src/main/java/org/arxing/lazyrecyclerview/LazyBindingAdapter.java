package org.arxing.lazyrecyclerview;

public abstract class LazyBindingAdapter<TData, THolder extends LazyBindingViewHolder<TData>> extends LazyAdapter {

    protected abstract TData getData(int sectionIndex, int itemIndex);

    @Override public void bindItemViewHolder(LazyItemViewHolder viewHolder, int sectionIndex, int itemIndex, int viewType) {
        TData data = getData(sectionIndex, itemIndex);
        THolder holder = (THolder) viewHolder;
        holder.bindData(data);
    }

}
