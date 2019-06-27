package org.arxing.lazyrecyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.arxing.lazyrecyclerview.protocol.LazyStatus;
import org.arxing.lazyrecyclerview.protocol.OnLoadMoreListener;
import org.arxing.utils.AssertUtils;
import org.arxing.utils.ThreadUtil;
import org.arxing.utils.UnitParser;


public class LazyRecyclerView extends BaseWidgetView implements LazyStatus {
    final static int NO_ID = 0;

    SwipeRefreshLayout refreshLayout;
    RecyclerView recyclerView;
    ViewGroup emptyPromptContainer;
    ViewGroup errorPromptContainer;
    View loadingViewLayout;
    LoadingView loadingView;

    Properties properties;
    LazyAdapter adapter;
    SwipeRefreshLayout.OnRefreshListener refreshListener;
    OnLoadMoreListener loadMoreListener;
    FrameLayout root;

    public LazyRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override protected int getWidgetLayout() {
        return R.layout.widget_simple_recyclerview;
    }

    @Override protected void initAttr(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.LazyRecyclerView);
        properties = new Properties();
        properties.refreshEnabled = ta.getBoolean(R.styleable.LazyRecyclerView_refreshEnabled, false);
        properties.loadMoreEnabled = ta.getBoolean(R.styleable.LazyRecyclerView_loadMoreEnabled, false);
        properties.emptyPromptShowingEnabled = ta.getBoolean(R.styleable.LazyRecyclerView_emptyPromptShowingEnabled, true);
        properties.errorPromptShowingEnabled = ta.getBoolean(R.styleable.LazyRecyclerView_errorPromptShowingEnabled, true);
        properties.nestedScrollingEnabled = ta.getBoolean(R.styleable.LazyRecyclerView_nestedScrollingEnabled, true);

        properties.emptyPromptText = getStringAttrOfDefault(ta, R.styleable.LazyRecyclerView_emptyPromptText, "No Data");
        properties.emptyPromptTextAppearance = ta.getResourceId(R.styleable.LazyRecyclerView_emptyPromptAppearance, NO_ID);
        properties.emptyPromptViewId = ta.getResourceId(R.styleable.LazyRecyclerView_emptyPromptView, NO_ID);
        properties.emptyPromptDrawableId = ta.getResourceId(R.styleable.LazyRecyclerView_emptyPromptDrawable, NO_ID);
        properties.emptyPromptMode = PromptMode.parse(ta.getInt(R.styleable.LazyRecyclerView_emptyPromptMode, PromptMode.TEXT.value));

        properties.errorPromptText = getStringAttrOfDefault(ta, R.styleable.LazyRecyclerView_errorPromptText, "Error");
        properties.errorPromptTextAppearance = ta.getResourceId(R.styleable.LazyRecyclerView_errorPromptAppearance, NO_ID);
        properties.errorPromptViewId = ta.getResourceId(R.styleable.LazyRecyclerView_errorPromptView, NO_ID);
        properties.errorPromptDrawableId = ta.getResourceId(R.styleable.LazyRecyclerView_errorPromptDrawable, NO_ID);
        properties.errorPromptMode = PromptMode.parse(ta.getInt(R.styleable.LazyRecyclerView_errorPromptMode, PromptMode.TEXT.value));

        properties.loadingMorePromptText = getStringAttrOfDefault(ta, R.styleable.LazyRecyclerView_loadingMorePromptText, "Loading More");
        properties.loadExhaustedPromptText = getStringAttrOfDefault(ta, R.styleable.LazyRecyclerView_loadExhaustedPromptText, "No More");
        properties.loadFailedPromptText = getStringAttrOfDefault(ta, R.styleable.LazyRecyclerView_loadFailedPromptText, "Load Failed");

        properties.loadingViewStartColor = ta.getColor(R.styleable.LazyRecyclerView_loadingViewStartColor, Color.DKGRAY);
        properties.loadingViewEndColor = ta.getColor(R.styleable.LazyRecyclerView_loadingViewEndColor, Color.TRANSPARENT);
        properties.loadingViewStrokeWidth = ta.getDimensionPixelSize(R.styleable.LazyRecyclerView_loadingViewStrokeWidth,
                                                                     UnitParser.dp2px(getContext(), 5));
        properties.loadingViewSize = ta.getDimensionPixelSize(R.styleable.LazyRecyclerView_loadingViewSize,
                                                              UnitParser.dp2px(getContext(), 30));
        ta.recycle();

        properties.isLoading = false;
        properties.status = STATUS_GENERAL;

        String sWidth = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "layout_width");
        String sHeight = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "layout_height");
        boolean widthWrapping = sWidth.equals(ViewGroup.LayoutParams.WRAP_CONTENT + "");
        boolean heightWrapping = sHeight.equals(ViewGroup.LayoutParams.WRAP_CONTENT + "");
        properties.isWrapContent = widthWrapping || heightWrapping;
    }

    @Override protected void init() {
        refreshLayout = findViewById(R.id.entryCore_refreshLayout);
        recyclerView = findViewById(R.id.entryCore_recyclerView);
        emptyPromptContainer = findViewById(R.id.entryCore_emptyPrompt_container);
        errorPromptContainer = findViewById(R.id.entryCore_errorPrompt_container);
        loadingViewLayout = findViewById(R.id.entryCore_loadingView_layout);
        loadingView = findViewById(R.id.entryCore_loadingView);
        root = findViewById(R.id.root);

        recyclerView.setTag(R.id.lazyTag_recyclerView, this);

        //加載失敗時的重新載入
        errorPromptContainer.setOnClickListener(v -> {
            setLoading(true);
            if (refreshListener != null)
                refreshListener.onRefresh();
        });

        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(false);
            setLoading(true);
            if (refreshListener != null)
                refreshListener.onRefresh();
        });

        // 判斷是否滑動到底部來判斷加載更多的時機
        recyclerView.addOnScrollListener(new LazyScrollListener() {
            @Override public void onScrollingUp(int distance) {

            }

            @Override public void onScrollingDown(int distance) {

            }

            @Override public void onScrolledToTop() {

            }

            @Override public void onScrolledToBottom() {
                if (loadMoreListener != null) {
                    if (!adapter.canLoadMore())
                        return;
                    postLoadMore();
                }
            }
        });

        if (properties.isWrapContent) {
            refreshLayout.removeView(recyclerView);
            refreshLayout.setVisibility(GONE);
            root.addView(recyclerView, 0);
        }
    }

    @Override protected void syncAttr() {
        refreshLayout.setEnabled(properties.refreshEnabled);
        emptyPromptContainer.setVisibility(properties.emptyPromptShowingEnabled ? VISIBLE : GONE);
        recyclerView.setNestedScrollingEnabled(properties.nestedScrollingEnabled);
        syncEmptyPrompt();
        syncErrorPrompt();
        syncLoadingShowing();
        syncLoadingView();
        syncAppearanceShowing();
    }

    // inner method

    private void syncLoadingView() {
        loadingView.setColor(properties.loadingViewStartColor, properties.loadingViewEndColor);
        loadingView.setStorkeWidth(properties.loadingViewStrokeWidth);
        loadingView.getLayoutParams().width = properties.loadingViewSize;
        loadingView.getLayoutParams().height = properties.loadingViewSize;
        loadingView.requestLayout();
    }

    private void syncEmptyPrompt() {
        switch (properties.emptyPromptMode) {
            case TEXT:
                TextView defaultEmptyPrompt = new TextView(getContext());
                LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,
                                                   LayoutParams.WRAP_CONTENT);
                lp.gravity = Gravity.CENTER;
                lp.topMargin = UnitParser.dp2px(getContext(), 5);
                lp.bottomMargin = UnitParser.dp2px(getContext(), 5);
                defaultEmptyPrompt.setLayoutParams(lp);
                defaultEmptyPrompt.setText(properties.emptyPromptText);
                if (properties.emptyPromptTextAppearance != NO_ID)
                    TextViewCompat.setTextAppearance(defaultEmptyPrompt, properties.emptyPromptTextAppearance);
                emptyPromptContainer.addView(defaultEmptyPrompt);
                break;
            case DRAWABLE:
                emptyPromptContainer.setBackgroundResource(properties.emptyPromptDrawableId);
                break;
            case VIEW:
                View view = inflate(getContext(), properties.emptyPromptViewId, emptyPromptContainer);
                emptyPromptContainer.addView(view);
                break;
        }
    }

    private void syncErrorPrompt() {
        switch (properties.errorPromptMode) {
            case TEXT:
                TextView defaultErrorPrompt = new TextView(getContext());
                LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,
                                                   LayoutParams.WRAP_CONTENT);
                lp.gravity = Gravity.CENTER;
                lp.topMargin = UnitParser.dp2px(getContext(), 5);
                lp.bottomMargin = UnitParser.dp2px(getContext(), 5);
                defaultErrorPrompt.setLayoutParams(lp);
                defaultErrorPrompt.setText(properties.errorPromptText);
                if (properties.errorPromptTextAppearance != NO_ID)
                    TextViewCompat.setTextAppearance(defaultErrorPrompt, properties.errorPromptTextAppearance);
                errorPromptContainer.addView(defaultErrorPrompt);
                break;
            case DRAWABLE:
                errorPromptContainer.setBackgroundResource(properties.errorPromptDrawableId);
                break;
            case VIEW:
                View view = inflate(getContext(), properties.errorPromptViewId, errorPromptContainer);
                errorPromptContainer.addView(view);
                break;
        }
    }

    private void syncLoadingShowing() {
        if (properties.isLoading) {
            loadingViewLayout.setVisibility(VISIBLE);
            loadingView.startAnim();
        } else {
            loadingViewLayout.setVisibility(GONE);
            loadingView.stopAnim();
        }
    }

    private void syncAppearanceShowing() {
        if (!ThreadUtil.isInMainThread()) {
            ThreadUtil.post(this::syncAppearanceShowing);
            return;
        }
        switch (properties.status) {
            case STATUS_GENERAL:
                recyclerView.setVisibility(VISIBLE);
                emptyPromptContainer.setVisibility(GONE);
                errorPromptContainer.setVisibility(GONE);
                break;
            case STATUS_EMPTY:
                recyclerView.setVisibility(GONE);
                emptyPromptContainer.setVisibility(VISIBLE);
                errorPromptContainer.setVisibility(GONE);
                break;
            case STATUS_ERROR:
                recyclerView.setVisibility(GONE);
                emptyPromptContainer.setVisibility(GONE);
                errorPromptContainer.setVisibility(VISIBLE);
                break;
        }
    }

    private void setSelfLayoutManager(RecyclerView.LayoutManager layoutManager) {
        layoutManager.setItemPrefetchEnabled(false);
        recyclerView.setLayoutManager(layoutManager);
    }

    // local method

    void postLoadMore() {
        post(() -> {
            adapter.notifier.notifyLoading();
            loadMoreListener.onLoadMore(adapter.pageTag, adapter.notifier);
        });
    }

    // user method

    public void addOnScrollListener(RecyclerView.OnScrollListener onScrollListener) {
        recyclerView.addOnScrollListener(onScrollListener);
    }

    public void addItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
        recyclerView.addItemDecoration(itemDecoration);
    }

    public void scrollToSection(int sectionIndex) {
        if (adapter != null) {
            int sectionAdapterPosition = adapter.getAdapterPositionForSectionHeader(sectionIndex);
            recyclerView.scrollToPosition(sectionAdapterPosition);
        }
    }

    public void smoothScrollToSection(int sectionIndex) {
        if (adapter != null) {
            int sectionAdapterPosition = adapter.getAdapterPositionForSectionHeader(sectionIndex);
            recyclerView.smoothScrollToPosition(sectionAdapterPosition);
        }
    }

    public void setOnLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }

    public void setAdapter(LazyAdapter adapter) {
        recyclerView.setAdapter((this.adapter = adapter));
    }

    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        recyclerView.setLayoutManager(layoutManager);
    }

    public void setRefreshEnabled(boolean enable) {
        properties.refreshEnabled = enable;
        refreshLayout.setEnabled(enable);
    }

    public void setEmptyPromptShowingEnabled(boolean enable) {
        properties.emptyPromptShowingEnabled = enable;
        emptyPromptContainer.setVisibility(enable ? VISIBLE : GONE);
    }

    public void setErrorPromptShowingEnabled(boolean enable) {
        properties.errorPromptShowingEnabled = enable;
        errorPromptContainer.setVisibility(enable ? VISIBLE : GONE);
    }

    public void setNestedScrollingEnabled(boolean enable) {
        properties.nestedScrollingEnabled = enable;
        recyclerView.setNestedScrollingEnabled(enable);
    }

    public void setEmptyPromptMode(PromptMode mode) {
        properties.emptyPromptMode = mode;
        syncEmptyPrompt();
    }

    public void setErrorPromptMode(PromptMode mode) {
        properties.errorPromptMode = mode;
        syncErrorPrompt();
    }

    public void setLoadingMorePromptText(@StringRes int id) {
        properties.loadingMorePromptText = getResources().getString(id);
    }

    public void setLoadingMorePromptText(String s) {
        properties.loadingMorePromptText = s;
    }

    public void setLoadExhaustedPromptText(@StringRes int id) {
        properties.loadExhaustedPromptText = getResources().getString(id);
    }

    public void setLoadExhaustedPromptText(String s) {
        properties.loadExhaustedPromptText = s;
    }

    public void setLoadFailedPromptText(@StringRes int id) {
        properties.loadFailedPromptText = getResources().getString(id);
    }

    public void setLoadFailedPromptText(String s) {
        properties.loadFailedPromptText = s;
    }

    public void setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener refreshListener) {
        this.refreshListener = refreshListener;
    }

    public void startLoading() {
        setLoading(true);
    }

    public void stopLoading() {
        setLoading(false);
    }

    public void setLoading(boolean loading) {
        properties.isLoading = loading;
        syncLoadingShowing();
    }

    public void notifyShowGeneral() {
        properties.status = STATUS_GENERAL;
        syncAppearanceShowing();
    }

    public void notifyShowError() {
        properties.status = STATUS_ERROR;
        syncAppearanceShowing();
    }

    public void notifyShowEmpty() {
        properties.status = STATUS_EMPTY;
        syncAppearanceShowing();
    }

    public void setVerticalLinearLayoutManager() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        setSelfLayoutManager(layoutManager);
    }

    public void setHorizontalLinearLayoutManager() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        setSelfLayoutManager(layoutManager);
    }

    public void setVerticalGridLayoutManager(int spanCount) {
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), spanCount, GridLayoutManager.VERTICAL, false);
        setSelfLayoutManager(layoutManager);
    }

    public void setHorizontalGridLayoutManager(int spanCount) {
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), spanCount, GridLayoutManager.HORIZONTAL, false);
        setSelfLayoutManager(layoutManager);
    }

    public void setStickyLayoutManager() {
        StickyHeaderLayoutManager layoutManager = new StickyHeaderLayoutManager();
        setSelfLayoutManager(layoutManager);
    }

    public void setVerticalStaggeredGridLayoutManager(int spanCount) {
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL);
        setSelfLayoutManager(layoutManager);
    }

    public void setHorizontalStaggeredGridLayoutManager(int spanCount) {
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.HORIZONTAL);
        setSelfLayoutManager(layoutManager);
    }

    public void setVerticalItemSpacing(int sizeDp) {
        HorizontalDividerItemDecoration.Builder builder = new HorizontalDividerItemDecoration.Builder(getContext());
        builder.color(Color.TRANSPARENT).size(UnitParser.dp2px(getContext(), sizeDp));
        recyclerView.addItemDecoration(builder.build());
    }

    public void setHorizontalItemSpacing(int sizeDp) {
        VerticalDividerItemDecoration.Builder builder = new VerticalDividerItemDecoration.Builder(getContext());
        builder.color(Color.TRANSPARENT).size(UnitParser.dp2px(getContext(), sizeDp));
        recyclerView.addItemDecoration(builder.build());
    }

    public void setVerticalItemDividerRes(@ColorRes int color, int sizeDp) {
        setVerticalItemDivider(getResources().getColor(color), sizeDp);
    }

    public void setVerticalItemDivider(int color, int sizeDp) {
        HorizontalDividerItemDecoration.Builder builder = new HorizontalDividerItemDecoration.Builder(getContext());
        builder.color(color).size(UnitParser.dp2px(getContext(), sizeDp));
        recyclerView.addItemDecoration(builder.build());
    }

    public void setHorizontalItemDividerRes(@ColorRes int color, int sizeDp) {
        setHorizontalItemDivider(getResources().getColor(color), sizeDp);
    }

    public void setHorizontalItemDivider(int color, int sizeDp) {
        VerticalDividerItemDecoration.Builder builder = new VerticalDividerItemDecoration.Builder(getContext());
        builder.color(color).size(UnitParser.dp2px(getContext(), sizeDp));
        recyclerView.addItemDecoration(builder.build());
    }

    static class Properties {
        boolean refreshEnabled;
        boolean loadMoreEnabled;
        boolean emptyPromptShowingEnabled;
        boolean errorPromptShowingEnabled;
        boolean nestedScrollingEnabled;
        String emptyPromptText;
        @StyleRes int emptyPromptTextAppearance;
        @LayoutRes int emptyPromptViewId;
        @DrawableRes int emptyPromptDrawableId;
        PromptMode emptyPromptMode;

        String errorPromptText;
        @StyleRes int errorPromptTextAppearance;
        @LayoutRes int errorPromptViewId;
        @DrawableRes int errorPromptDrawableId;
        PromptMode errorPromptMode;

        String loadingMorePromptText;
        String loadExhaustedPromptText;
        String loadFailedPromptText;

        boolean isLoading;
        int status;

        int loadingViewStartColor;
        int loadingViewEndColor;
        int loadingViewStrokeWidth;
        int loadingViewSize;

        boolean isWrapContent;
    }

    public enum PromptMode {
        TEXT(0),
        VIEW(1),
        DRAWABLE(2);

        final int value;

        PromptMode(int value) {
            this.value = value;
        }

        public static PromptMode parse(int value) {
            switch (value) {
                case 0:
                    return TEXT;
                case 1:
                    return VIEW;
                case 2:
                    return DRAWABLE;
                default:
                    AssertUtils.throwError("Unknown value %d for %s", value, PromptMode.class.getSimpleName());
            }
            return null;
        }
    }
}
