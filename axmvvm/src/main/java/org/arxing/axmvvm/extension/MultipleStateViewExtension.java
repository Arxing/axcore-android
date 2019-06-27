package org.arxing.axmvvm.extension;

import android.databinding.BindingAdapter;

import org.arxing.multiplestateview.MultipleStateView;


public class MultipleStateViewExtension {

    @BindingAdapter("displayChild") public static void setSelect(MultipleStateView view, int select) {
        if (view.getDisplayedChild() != select) {
            view.setDisplayedChild(select);
        }
    }
}
