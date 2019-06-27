package org.arxing.lazyrecyclerview;

import android.support.annotation.IdRes;
import android.view.View;

public class LazyGhostHeaderViewHolder extends SectioningAdapter.GhostHeaderViewHolder {

    public LazyGhostHeaderViewHolder(View itemView) {
        super(itemView);
    }

    protected <T> T $(@IdRes int id) {
        return (T) itemView.findViewById(id);
    }

}
