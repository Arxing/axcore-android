package org.arxing.axutils_android;

import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.util.Size;
import android.util.SizeF;
import android.util.SparseArray;

import java.io.Serializable;
import java.util.ArrayList;

public class BundleHelper {
    private Bundle bundle;

    private BundleHelper() {
        bundle = new Bundle();
    }

    private BundleHelper(Bundle from) {
        bundle = new Bundle(from);
    }

    public static BundleHelper newHelper() {
        return new BundleHelper();
    }

    public static BundleHelper newHelper(Bundle from) {
        return new BundleHelper(from);
    }

    public Bundle bundle() {
        return bundle;
    }

    /* put */

    public BundleHelper putDataAll(Bundle bundle) {
        bundle.putAll(bundle);
        return this;
    }

    public BundleHelper putData(String key, IBinder v) {
        bundle.putBinder(key, v);
        return this;
    }

    public BundleHelper putData(String key, Bundle v) {
        bundle.putBundle(key, v);
        return this;
    }

    public BundleHelper putData(String key, byte v) {
        bundle.putByte(key, v);
        return this;
    }

    public BundleHelper putData(String key, byte[] v) {
        bundle.putByteArray(key, v);
        return this;
    }

    public BundleHelper putData(String key, char v) {
        bundle.putChar(key, v);
        return this;
    }

    public BundleHelper putData(String key, char[] v) {
        bundle.putCharArray(key, v);
        return this;
    }

    public BundleHelper putData(String key, CharSequence v) {
        bundle.putCharSequence(key, v);
        return this;
    }

    public BundleHelper putData(String key, CharSequence[] v) {
        bundle.putCharSequenceArray(key, v);
        return this;
    }

    public BundleHelper putDataCharArrayList(String key, ArrayList<CharSequence> v) {
        bundle.putCharSequenceArrayList(key, v);
        return this;
    }

    public BundleHelper putData(String key, float v) {
        bundle.putFloat(key, v);
        return this;
    }

    public BundleHelper putData(String key, float[] v) {
        bundle.putFloatArray(key, v);
        return this;
    }

    public BundleHelper putDataIntArrayList(String key, ArrayList<Integer> v) {
        bundle.putIntegerArrayList(key, v);
        return this;
    }

    public BundleHelper putData(String key, Parcelable v) {
        bundle.putParcelable(key, v);
        return this;
    }

    public BundleHelper putData(String key, Parcelable[] v) {
        bundle.putParcelableArray(key, v);
        return this;
    }

    public BundleHelper putDataParcelableArrayList(String key, ArrayList<? extends Parcelable> v) {
        bundle.putParcelableArrayList(key, v);
        return this;
    }

    public BundleHelper putData(String key, Serializable v) {
        bundle.putSerializable(key, v);
        return this;
    }

    public BundleHelper putData(String key, short v) {
        bundle.putShort(key, v);
        return this;
    }

    public BundleHelper putData(String key, short[] v) {
        bundle.putShortArray(key, v);
        return this;
    }

    public BundleHelper putData(String key, Size v) {
        bundle.putSize(key, v);
        return this;
    }

    public BundleHelper putData(String key, SizeF v) {
        bundle.putSizeF(key, v);
        return this;
    }

    public BundleHelper putData(String key, SparseArray<? extends Parcelable> v) {
        bundle.putSparseParcelableArray(key, v);
        return this;
    }

    public BundleHelper putDataStringArrayList(String key, ArrayList<String> v) {
        bundle.putStringArrayList(key, v);
        return this;
    }

    public BundleHelper putDataAll(PersistableBundle v) {
        bundle.putAll(v);
        return this;
    }

    public BundleHelper putData(String key, boolean v) {
        bundle.putBoolean(key, v);
        return this;
    }

    public BundleHelper putData(String key, boolean[] v) {
        bundle.putBooleanArray(key, v);
        return this;
    }

    public BundleHelper putData(String key, double v) {
        bundle.putDouble(key, v);
        return this;
    }

    public BundleHelper putData(String key, double[] v) {
        bundle.putDoubleArray(key, v);
        return this;
    }

    public BundleHelper putData(String key, int v) {
        bundle.putInt(key, v);
        return this;
    }

    public BundleHelper putData(String key, int[] v) {
        bundle.putIntArray(key, v);
        return this;
    }

    public BundleHelper putData(String key, long v) {
        bundle.putLong(key, v);
        return this;
    }

    public BundleHelper putData(String key, long[] v) {
        bundle.putLongArray(key, v);
        return this;
    }

    public BundleHelper putData(String key, String v) {
        bundle.putString(key, v);
        return this;
    }

    public BundleHelper putData(String key, String[] v) {
        bundle.putStringArray(key, v);
        return this;
    }
}
