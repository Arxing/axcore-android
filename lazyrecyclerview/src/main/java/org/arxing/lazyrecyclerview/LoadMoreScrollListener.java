package org.arxing.lazyrecyclerview;

import org.arxing.lazyrecyclerview.protocol.PageLoadLayoutManager;

public abstract class LoadMoreScrollListener extends LazyScrollListener {
    private static final int DEFAULT_VISIBLE_THRESHOLD = 5;
    private int visibleThreshold;
    private int currentPage = 0;
    private int previousTotalItemCount = 0;
    private boolean loading = false;
    private boolean loadExhausted = false;
    private PageLoadLayoutManager layoutManager;


    @Override public void onScrollingUp(int distance) {

    }

    @Override public void onScrollingDown(int distance) {

    }

    @Override public void onScrolledToTop() {

    }

    @Override public void onScrolledToBottom() {
        // no-op if we're loading, or exhausted
        if (loading || loadExhausted) {
            return;
        }

        currentPage++;
        loading = true;
        onLoadMore(currentPage, loadCompleteNotifier);
    }

    /**
     * Override this to handle loading of new data. Each time new data is pulled in, the page counter will increase by one.
     * When your load is complete, call the appropriate method on the loadComplete callback.
     *
     * @param page         the page counter. Increases by one each time onLoadMore is called.
     * @param loadComplete callback to invoke when your load has completed.
     */
    public abstract void onLoadMore(int page, LoadCompleteNotifier loadComplete);

    LoadMoreScrollListener.LoadCompleteNotifier loadCompleteNotifier = new LoadMoreScrollListener.LoadCompleteNotifier() {
        @Override public void notifyLoadComplete() {
            loading = false;
            previousTotalItemCount = layoutManager.getItemCount();
        }

        @Override public void notifyLoadExhausted() {
            loadExhausted = true;
        }
    };

    public interface LoadCompleteNotifier {
        /**
         * Call to notify that a load has completed, with new items present.
         */
        void notifyLoadComplete();

        /**
         * Call to notify that a load has completed, but no new items were present, and none will be forthcoming.
         */
        void notifyLoadExhausted();
    }
}
