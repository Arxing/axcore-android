package org.arxing.attrparser.getter;

import android.content.Context;

import com.google.gson.JsonElement;

public interface JsonGetter {
    JsonElement getJson(Context context);
}
