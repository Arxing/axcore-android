package org.arxing.axutils_android;

import android.os.Handler;

import java.lang.ref.WeakReference;

public class WeakReferenceHandler<T> extends Handler {
    private final WeakReference<T> ref;

    public WeakReferenceHandler(T self) {
        ref = new WeakReference<>(self);
    }

    public final void send(int what, int arg1, int arg2, Object obj) {
        sendMessage(obtainMessage(what, arg1, arg2, obj));
    }

    public final void send(int what, int arg1, int arg2) {
        send(what, arg1, arg2, null);
    }

    public final void send(int what, Object obj) {
        send(what, 0, 0, obj);
    }

    public final void send(int what) {
        send(what, null);
    }

    public final T self() {
        return ref.get();
    }
}
