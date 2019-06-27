package org.arxing.axmvvm.adapter;

import android.databinding.ObservableList;

public interface OnListSimpleListener<T> {
    void onAnyChanged(ObservableList<T> sender, int type);
}
