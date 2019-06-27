package org.arxing.lazyrecyclerview;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

public class AdapterDecorator extends RecyclerView.Adapter {
    private RecyclerView.Adapter adapter;

    public AdapterDecorator(RecyclerView.Adapter adapter) {
        this.adapter = adapter;
    }

    @NonNull @Override public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return adapter.onCreateViewHolder(parent, viewType);
    }

    @Override public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        adapter.onBindViewHolder(holder, position);
    }

    @Override public int getItemCount() {
        return adapter.getItemCount();
    }

    @Override public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List payloads) {
        adapter.onBindViewHolder(holder, position, payloads);
    }

    @Override public int getItemViewType(int position) {
        return adapter.getItemViewType(position);
    }

    @Override public void setHasStableIds(boolean hasStableIds) {
        adapter.setHasStableIds(hasStableIds);
    }

    @Override public long getItemId(int position) {
        return adapter.getItemId(position);
    }

    @Override public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        adapter.onViewRecycled(holder);
    }

    @Override public boolean onFailedToRecycleView(@NonNull RecyclerView.ViewHolder holder) {
        return adapter.onFailedToRecycleView(holder);
    }

    @Override public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        adapter.onViewAttachedToWindow(holder);
    }

    @Override public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        adapter.onViewDetachedFromWindow(holder);
    }

    @Override public void registerAdapterDataObserver(@NonNull RecyclerView.AdapterDataObserver observer) {
        adapter.registerAdapterDataObserver(observer);
    }

    @Override public void unregisterAdapterDataObserver(@NonNull RecyclerView.AdapterDataObserver observer) {
        adapter.unregisterAdapterDataObserver(observer);
    }

    @Override public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        adapter.onAttachedToRecyclerView(recyclerView);
    }

    @Override public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        adapter.onDetachedFromRecyclerView(recyclerView);
    }

    public RecyclerView.Adapter getRealAdapter() {
        return adapter;
    }
}
