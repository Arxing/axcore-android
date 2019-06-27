package org.arxing.lazyrecyclerview;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class LazyScrollListener extends RecyclerView.OnScrollListener {
    final static int DRAGGING = RecyclerView.SCROLL_STATE_DRAGGING;
    final static int IDLE = RecyclerView.SCROLL_STATE_IDLE;
    final static int SETTING = RecyclerView.SCROLL_STATE_SETTLING;

    private int nowState;

    @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        nowState = newState;
    }

    @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        if (nowState == DRAGGING) {
            int distanceVertical = Math.abs(dy);

            if (dy > 0)
                onScrollingDown(distanceVertical);
            else if (dy < 0)
                onScrollingUp(distanceVertical);
        }

        if (nowState == DRAGGING || nowState == SETTING) {
            if (!recyclerView.canScrollVertically(-1))
                onScrolledToTop();
        }

        // 在這裡根據不同的layoutManager做適配
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            handleLinearLayoutScrolled(recyclerView, (LinearLayoutManager) layoutManager);
        } else if (layoutManager instanceof StickyHeaderLayoutManager) {
            handleStickyLayoutScrolled(recyclerView, (StickyHeaderLayoutManager) layoutManager);
        }
    }

    public abstract void onScrollingUp(int distance);

    public abstract void onScrollingDown(int distance);

    public abstract void onScrolledToTop();

    public abstract void onScrolledToBottom();

    private void handleLinearLayoutScrolled(RecyclerView recyclerView, LinearLayoutManager layoutManager) {
        int totalItemCount = layoutManager.getItemCount();
        if (totalItemCount > 0) {
            int visibleItemCount = layoutManager.getChildCount();
            int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
            if (visibleItemCount + firstVisibleItemPosition >= totalItemCount) {
                onScrolledToBottom();
            }
        }
    }

    private void handleStickyLayoutScrolled(RecyclerView recyclerView, StickyHeaderLayoutManager layoutManager) {
        int totalItemCount = layoutManager.getItemCount();
        if (totalItemCount > 0) {
            int visibleItemCount = layoutManager.getChildCount();
            int firstVisibleItemPosition = layoutManager.getViewAdapterPosition(layoutManager.getTopmostChildView());
            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount) {
                onScrolledToBottom();
            }
        }
    }
}
