package org.arxing.lazyrecyclerview.protocol;

import android.view.View;

public interface PageLoadLayoutManager {
    int getItemCount();

    View getBottommostChildView();

    int getViewAdapterPosition(View lastVisibleItem);
}
