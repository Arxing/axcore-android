package org.arxing.axmvvm.extension;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.BindingMethod;
import android.databinding.BindingMethods;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;

import org.arxing.axmvvm.FormatError;
import org.arxing.lazyrecyclerview.HorizontalDividerItemDecoration;
import org.arxing.lazyrecyclerview.LazyAdapter;
import org.arxing.lazyrecyclerview.LazyRecyclerView;
import org.arxing.lazyrecyclerview.protocol.OnLoadMoreListener;
import org.arxing.axutils_android.UnitParser;

@BindingMethods({
        @BindingMethod(type = LazyRecyclerView.class, attribute = "onLoadMore", method = "setOnLoadMoreListener"),
        @BindingMethod(type = LazyRecyclerView.class, attribute = "onRefresh", method = "setOnRefreshListener")
})
public class LazyRecyclerViewExtension {

    @BindingAdapter(value = {
            "recyclerView_divider_horizontal_sizeDp", "recyclerView_divider_horizontal_color"
    }, requireAll = false) public static void setHorizontalDivider(LazyRecyclerView view, float sizeDp, int color) {
        Context context = view.getContext();
        RecyclerView.ItemDecoration itemDecoration;
        HorizontalDividerItemDecoration.Builder builder = new HorizontalDividerItemDecoration.Builder(context);
        builder.color(color).size((int) UnitParser.dp2px(context, sizeDp)).showLastDivider();
        itemDecoration = builder.build();
        view.addItemDecoration(itemDecoration);
    }

    @BindingAdapter(value = {
            "recyclerView_divider_horizontal_sizeDp", "recyclerView_divider_horizontal_color"
    }, requireAll = false) public static void setHorizontalDivider(LazyRecyclerView view, float sizeDp, String color) {
        setHorizontalDivider(view, sizeDp, Color.parseColor(color));
    }

    @BindingAdapter(value = {
            "recyclerView_divider_vertical_sizeDp", "recyclerView_divider_vertical_color"
    }, requireAll = false) public static void setVerticalDivider(LazyRecyclerView view, float sizeDp, int color) {
        Context context = view.getContext();
        RecyclerView.ItemDecoration itemDecoration;
        HorizontalDividerItemDecoration.Builder builder = new HorizontalDividerItemDecoration.Builder(context);
        builder.color(color).size((int) UnitParser.dp2px(context, sizeDp)).showLastDivider();
        itemDecoration = builder.build();
        view.addItemDecoration(itemDecoration);
    }

    @BindingAdapter(value = {
            "recyclerView_divider_vertical_sizeDp", "recyclerView_divider_vertical_color"
    }, requireAll = false) public static void setVerticalDivider(LazyRecyclerView view, float sizeDp, String color) {
        setVerticalDivider(view, sizeDp, Color.parseColor(color));
    }

    @BindingAdapter("recyclerView_divider_horizontal_spacing_size")
    public static void setHorizontalSpacing(LazyRecyclerView view, float sizeDp) {
        setHorizontalDivider(view, sizeDp, Color.TRANSPARENT);
    }

    @BindingAdapter("recyclerView_divider_vertical_spacing_size")
    public static void setVerticalSpacing(LazyRecyclerView view, float sizeDp) {
        setVerticalDivider(view, sizeDp, Color.TRANSPARENT);
    }

    @BindingAdapter("recyclerView_loading") public static void setLoading(LazyRecyclerView view, boolean loading) {
        view.setLoading(loading);
    }

    @BindingAdapter({"recyclerView_layoutManager_linear"})
    public static void setLinearLayoutManager(LazyRecyclerView view, String orientation) {
        orientation = orientation.toLowerCase();
        switch (orientation) {
            case "horizontal":
                view.setHorizontalLinearLayoutManager();
                break;
            case "vertical":
                view.setVerticalLinearLayoutManager();
                break;
            default:
                throw new FormatError("Invalid linear layout manager orientation: %s", orientation);
        }
    }

    @BindingAdapter({"recyclerView_layoutManager_grid", "recyclerView_layoutManager_grid_spanCount"})
    public static void setGridLayoutManager(LazyRecyclerView view, String orientation, int spanCount) {
        orientation = orientation.toLowerCase();
        switch (orientation) {
            case "horizontal":
                view.setHorizontalGridLayoutManager(spanCount);
                break;
            case "vertical":
                view.setVerticalGridLayoutManager(spanCount);
                break;
            default:
                throw new FormatError("Invalid grid layout manager orientation: %s", orientation);
        }
    }

    @BindingAdapter({"recyclerView_layoutManager_staggeredGrid", "recyclerView_layoutManager_staggeredGrid_spanCount"})
    public static void setStaggeredGridLayoutManager(LazyRecyclerView view, String orientation, int spanCount) {
        orientation = orientation.toLowerCase();
        switch (orientation) {
            case "horizontal":
                view.setHorizontalStaggeredGridLayoutManager(spanCount);
                break;
            case "vertical":
                view.setVerticalStaggeredGridLayoutManager(spanCount);
                break;
            default:
                throw new FormatError("Invalid staggered grid layout manager orientation: %s", orientation);
        }
    }

    @BindingAdapter({"recyclerView_layoutManager_sticky"})
    public static void setStickyLayoutManager(LazyRecyclerView view, String orientation) {
        orientation = orientation.toLowerCase();
        switch (orientation) {
            default:
                view.setStickyLayoutManager();
                break;
        }
    }

    @BindingAdapter({"recyclerView_adapter"}) public static void setAdapter(LazyRecyclerView view, LazyAdapter adapter) {
        view.setAdapter(adapter);
    }

    @BindingAdapter({"recyclerView_status"}) public static void set(LazyRecyclerView view, int status) {
        switch (status) {
            case LazyRecyclerView.STATUS_EMPTY:
                view.notifyShowEmpty();
                break;
            case LazyRecyclerView.STATUS_ERROR:
                view.notifyShowError();
                break;
            case LazyRecyclerView.STATUS_GENERAL:
                view.notifyShowGeneral();
                break;
        }
    }

    @BindingAdapter({"onRefresh"}) public static void setOnRefreshListener(LazyRecyclerView view, SwipeRefreshLayout.OnRefreshListener l) {
        view.setOnRefreshListener(l);
    }

    @BindingAdapter({"onLoadMore"}) public static void setOnLoadMoreListener(LazyRecyclerView view, OnLoadMoreListener l) {
        view.setOnLoadMoreListener(l);
    }

    @BindingAdapter("nestedScrollingEnabled") public static void setNestedScrollingEnabled(LazyRecyclerView view, boolean enable) {
        view.setNestedScrollingEnabled(enable);
    }
}
