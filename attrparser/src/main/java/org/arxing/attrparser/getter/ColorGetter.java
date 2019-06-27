package org.arxing.attrparser.getter;

import android.content.Context;
import android.support.annotation.ColorInt;

public interface ColorGetter {
    @ColorInt int getColor(Context context);
}
