package org.arxing.axmvvm.adapter;

import android.databinding.ObservableList;

import org.arxing.utils.Logger;


public abstract class OnListSimpleCallback<T> extends ObservableList.OnListChangedCallback<ObservableList<T>> implements OnListSimpleListener<T> {
    public final static int TYPE_CHANGED = 0x10;
    public final static int TYPE_RANGE_CHANGED = 0x11;
    public final static int TYPE_RANGE_INSERTED = 0x12;
    public final static int TYPE_RANGE_MOVED = 0x13;
    public final static int TYPE_RANGE_REMOVED = 0x14;
    private final static Logger logger = new Logger("OnListSimpleCallBack");

    {
        logger.setEnable(logEnabled());
    }

    protected boolean logEnabled() {
        return false;
    }

    @Override public void onChanged(ObservableList<T> sender) {
        logger.d("onChanged()");
        onAnyChanged(sender, TYPE_CHANGED);
    }

    @Override public void onItemRangeChanged(ObservableList<T> sender, int positionStart, int itemCount) {
        logger.d("onItemRangeChanged(), start=%d, count=%d", positionStart, itemCount);
        onAnyChanged(sender, TYPE_RANGE_CHANGED);
    }

    @Override public void onItemRangeInserted(ObservableList<T> sender, int positionStart, int itemCount) {
        logger.d("onItemRangeInserted(), start=%d, count=%d", positionStart, itemCount);
        onAnyChanged(sender, TYPE_RANGE_INSERTED);
    }

    @Override public void onItemRangeMoved(ObservableList<T> sender, int fromPosition, int toPosition, int itemCount) {
        logger.d("onItemRangeMoved(), from=%d, to=%d, count=%d", fromPosition, toPosition, itemCount);
        onAnyChanged(sender, TYPE_RANGE_MOVED);
    }

    @Override public void onItemRangeRemoved(ObservableList<T> sender, int positionStart, int itemCount) {
        logger.d("onItemRangeRemoved(), start=%d, count=%d", positionStart, itemCount);
        onAnyChanged(sender, TYPE_RANGE_REMOVED);
    }
}
