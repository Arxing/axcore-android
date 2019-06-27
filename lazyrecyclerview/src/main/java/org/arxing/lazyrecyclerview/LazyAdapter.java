package org.arxing.lazyrecyclerview;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.arxing.lazyrecyclerview.protocol.LoadMoreNotifier;
import org.arxing.axutils_android.Logger;


public abstract class LazyAdapter extends GlobalHeaderFooterSectioningAdapter {
    private final static int LOAD_MODE_EXHAUSTED = 100;
    private final static int LOAD_MODE_LOADING = 101;
    private final static int LOAD_MODE_FAILED = 102;
    private final static int LOAD_MODE_NOTHING = 103;
    private int loadMode = LOAD_MODE_NOTHING;

    private Logger logger = new Logger("LazyAdapter");
    Object pageTag;
    LazyRecyclerView recyclerView;
    private boolean isLoading;
    private boolean isLoadExhausted;
    private boolean isLoadFailed;

    // 強制實作

    @Override public abstract int getSizeOfSections();

    @Override public abstract int getSizeOfItems(int sectionIndex);

    @Override public abstract LazyItemViewHolder createItemViewHolder(ViewGroup parent, int viewType);

    @Override public abstract void bindItemViewHolder(LazyItemViewHolder viewHolder, int sectionIndex, int itemIndex, int viewType);

    /**
     * 是否開啟加載更多功能
     */
    public boolean loadMoreEnabled() {
        return recyclerView != null && recyclerView.properties.loadMoreEnabled;
    }

    @Override public final boolean hasGlobalFooter() {
        return loadMoreEnabled();
    }

    @Override public LazyItemViewHolder createGlobalFooterViewHolder(ViewGroup parent) {
        return new LoadMoreHolder($(parent, R.layout.item_load_more));
    }

    @Override public void bindGlobalFooterViewHolder(LazyItemViewHolder viewHolder) {
        LoadMoreHolder holder = (LoadMoreHolder) viewHolder;
        holder.syncPrompt();
        switch (loadMode) {
            case LOAD_MODE_LOADING:
                holder.showLoading();
                break;
            case LOAD_MODE_FAILED:
                holder.showLoadFailed();
                break;
            case LOAD_MODE_EXHAUSTED:
                holder.showExhausted();
                break;
            case LOAD_MODE_NOTHING:
                holder.showNothing();
                break;
        }
    }

    @Override public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        Object o = recyclerView.getTag(R.id.lazyTag_recyclerView);
        if (o == null || !(o instanceof LazyRecyclerView))
            return;
        this.recyclerView = (LazyRecyclerView) o;
    }

    @Override public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.recyclerView = null;
    }

    public LoadMoreNotifier getNotifier() {
        return notifier;
    }

    public String getLoadingPrompt() {
        return recyclerView == null ? "Loading more." : recyclerView.properties.loadingMorePromptText;
    }

    public String getLoadExhaustedPrompt() {
        return recyclerView == null ? "No data." : recyclerView.properties.loadExhaustedPromptText;
    }

    public String getLoadFailedPrompt() {
        return recyclerView == null ? "Failed." : recyclerView.properties.loadFailedPromptText;
    }

    public boolean hasData() {
        return getSizeOfSections() > 0;
    }

    public void initPageTag(Object pageTag) {
        this.pageTag = pageTag;
    }

    // 加載中不能載入
    // 加載錯誤不能載入
    // 完全加載完畢不能載入
    boolean canLoadMore() {
        return loadMoreEnabled() && !isLoading && !isLoadFailed && !isLoadExhausted && hasData();
    }

    LoadMoreNotifier notifier = new LoadMoreNotifier() {

        // 由外部通知本次加載完畢 紀錄pageTag
        @Override public void notifyLoadCompleted(Object tag) {
            if (!loadMoreEnabled())
                return;
            pageTag = tag;
            loadMode = LOAD_MODE_NOTHING;
            isLoading = false;
            notifySectionDataSetChanged(getGlobalFooterSection());
        }

        @Override public void notifyLoadFailed() {
            if (!loadMoreEnabled())
                return;
            loadMode = LOAD_MODE_FAILED;
            isLoading = false;
            isLoadFailed = true;
            isLoadExhausted = false;
            notifySectionDataSetChanged(getGlobalFooterSection());
        }

        @Override public void notifyLoadExhausted() {
            if (!loadMoreEnabled())
                return;
            loadMode = LOAD_MODE_EXHAUSTED;
            isLoading = false;
            isLoadFailed = false;
            isLoadExhausted = true;
            notifySectionDataSetChanged(getGlobalFooterSection());
        }

        @Override public void notifyLoading() {
            if (!loadMoreEnabled())
                return;
            loadMode = LOAD_MODE_LOADING;
            isLoading = true;
            isLoadFailed = false;
            isLoadExhausted = false;
            notifySectionDataSetChanged(getGlobalFooterSection());
        }

        @Override public void notifyReset() {
            if (!loadMoreEnabled())
                return;
            loadMode = LOAD_MODE_NOTHING;
            isLoading = false;
            isLoadExhausted = false;
            isLoadFailed = false;
            notifySectionDataSetChanged(getGlobalFooterSection());
        }
    };

    class LoadMoreHolder extends LazyItemViewHolder {
        View loadingLayout;
        View loadFailedLayout;
        View exhaustedLayout;

        TextView loadingTextView;
        LoadingView loadingLoadingView;

        TextView loadFailedTextView;
        LoadingView loadFailedLoadingView;

        TextView loadExhaustedTextView;


        LoadMoreHolder(View itemView) {
            super(itemView);
            loadingLayout = $(R.id.entryCore_loadingLayout);
            loadFailedLayout = $(R.id.entryCore_loadFailedLayout);
            exhaustedLayout = $(R.id.entryCore_exhaustedLayout);

            loadingTextView = $(R.id.entryCore_loading_textView);
            loadingLoadingView = $(R.id.entryCore_loading_loadingView);
            loadFailedTextView = $(R.id.entryCore_loadFailed_textView);
            loadFailedLoadingView = $(R.id.entryCore_loadFailed_loadingView);
            loadExhaustedTextView = $(R.id.entryCore_exhausted_textView);

            loadFailedLayout.setOnClickListener(v -> {
                if (recyclerView == null) {
                    logger.e("If you want to use advanced function, please use LazyRecyclerView instead of RecyclerView.");
                    return;
                }
                showErrorReloading();
                recyclerView.postLoadMore();
            });
            showNothing();
        }

        void showErrorReloading() {
            loadingLayout.setVisibility(View.GONE);
            loadFailedLayout.setVisibility(View.VISIBLE);
            exhaustedLayout.setVisibility(View.GONE);
            loadFailedLoadingView.setVisibility(View.VISIBLE);
            loadFailedLoadingView.startAnim();
        }

        void showLoading() {
            loadingLayout.setVisibility(View.VISIBLE);
            loadFailedLayout.setVisibility(View.GONE);
            exhaustedLayout.setVisibility(View.GONE);
            loadingLoadingView.startAnim();
        }

        void showLoadFailed() {
            loadingLayout.setVisibility(View.GONE);
            loadFailedLayout.setVisibility(View.VISIBLE);
            exhaustedLayout.setVisibility(View.GONE);
            loadFailedLoadingView.setVisibility(View.INVISIBLE);
            loadFailedLoadingView.clearAnimation();
        }

        void showExhausted() {
            loadingLayout.setVisibility(View.GONE);
            loadFailedLayout.setVisibility(View.GONE);
            exhaustedLayout.setVisibility(View.VISIBLE);
        }

        void showNothing() {
            loadingLayout.setVisibility(View.GONE);
            loadFailedLayout.setVisibility(View.GONE);
            exhaustedLayout.setVisibility(View.GONE);
        }

        void syncPrompt() {
            loadingTextView.setText(getLoadingPrompt());
            loadFailedTextView.setText(getLoadFailedPrompt());
            loadExhaustedTextView.setText(getLoadExhaustedPrompt());
        }
    }
}
