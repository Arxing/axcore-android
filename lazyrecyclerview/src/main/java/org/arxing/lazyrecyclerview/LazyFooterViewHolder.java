package org.arxing.lazyrecyclerview;

import android.support.annotation.IdRes;
import android.view.View;

public class LazyFooterViewHolder extends SectioningAdapter.FooterViewHolder {

    public LazyFooterViewHolder(View itemView) {
        super(itemView);
    }

    protected <T> T $(@IdRes int id) {
        return (T) itemView.findViewById(id);
    }

}
