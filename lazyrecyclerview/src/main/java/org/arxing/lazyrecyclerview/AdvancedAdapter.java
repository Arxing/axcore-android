package org.arxing.lazyrecyclerview;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


abstract class AdvancedAdapter extends AdapterDecorator {
    private final static int ITEM_TYPE_LOAD_MORE = 0x10;

    AdvancedAdapter(RecyclerView.Adapter adapter) {
        super(adapter);
    }

    public abstract boolean loadMoreEnabled();

    @NonNull @Override public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        if (viewType == ITEM_TYPE_LOAD_MORE)
            holder = new LoadMoreHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_load_more, parent, false));
        else
            holder = super.onCreateViewHolder(parent, viewType);
        return holder;
    }

    @Override public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        if (type == ITEM_TYPE_LOAD_MORE) {

        } else {
            super.onBindViewHolder(holder, position);
        }
    }

    /**
     * 為加載更多擴展一個位置
     */
    @Override public int getItemCount() {
        int count = super.getItemCount();
        if (loadMoreEnabled()) {
            count += 1;
        }
        return count;
    }

    /**
     * 加載更多Type
     */
    @Override public int getItemViewType(int position) {
        int itemType = super.getItemViewType(position);
        if (loadMoreEnabled() && getItemCount() > 0 && position == getItemCount() - 1) {
            itemType = ITEM_TYPE_LOAD_MORE;
        }
        return itemType;
    }

    class LoadMoreHolder extends RecyclerView.ViewHolder {

        LoadMoreHolder(View itemView) {
            super(itemView);
        }
    }
}
