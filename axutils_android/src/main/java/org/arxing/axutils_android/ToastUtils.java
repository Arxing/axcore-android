package org.arxing.axutils_android;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class ToastUtils {

    private static ToastUtils instance = new ToastUtils();
    private Toast sToast;
    private boolean isJumpWhenMore = false;

    private ToastUtils() {
    }


    public static ToastUtils getInstance() {
        return instance;
    }

    public void recycleContext() {
        cancelToast();
    }

    /**
     * 顯示短時吐司
     *
     * @param context
     * @param resId   資源Id
     */
    public void showShortToast(Context context, int resId) {
        showToast(context, resId, Toast.LENGTH_SHORT);
    }

    /**
     * 顯示短時吐司
     *
     * @param resId 資源Id
     * @param args  參數
     */
    public void showShortToast(Context context, int resId, Object... args) {
        showToast(context, resId, Toast.LENGTH_SHORT, args);
    }

    /**
     * 顯示短時吐司
     *
     * @param context
     * @param format  格式
     * @param args    參數
     */
    public void showShortToast(Context context, String format, Object... args) {
        showToast(context, format, Toast.LENGTH_SHORT, args);
    }

    /**
     * 顯示長時吐司
     *
     * @param text 文本
     */
    public void showLongToast(Context context, CharSequence text) {
        showToast(context, text, Toast.LENGTH_LONG);
    }

    /**
     * 顯示長時吐司
     *
     * @param resId 資源Id
     */
    public void showLongToast(Context context, int resId) {
        showToast(context, resId, Toast.LENGTH_LONG);
    }

    /**
     * 顯示長時吐司
     *
     * @param resId 資源Id
     * @param args  參數
     */
    public void showLongToast(Context context, int resId, Object... args) {
        showToast(context, resId, Toast.LENGTH_LONG, args);
    }

    /**
     * 顯示長時吐司
     *
     * @param format 格式
     * @param args   參數
     */
    public void showLongToast(Context context, String format, Object... args) {
        showToast(context, format, Toast.LENGTH_LONG, args);
    }

    /**
     * 顯示吐司
     *
     * @param context
     * @param resId    資源Id
     * @param duration 顯示時長
     */
    private void showToast(Context context, int resId, int duration) {
        if (context == null) {
            return;
        }
        showToast(context, context.getResources().getText(resId).toString(), duration);
    }

    /**
     * 顯示吐司
     *
     * @param resId    資源Id
     * @param duration 顯示時長
     * @param args     參數
     */
    private void showToast(Context context, int resId, int duration, Object... args) {
        if (context == null) {
            return;
        }
        showToast(context, String.format(context.getResources().getString(resId), args), duration);
    }

    /**
     * 顯示吐司
     *
     * @param format   格式
     * @param duration 顯示時長
     * @param args     參數
     */
    private void showToast(Context context, String format, int duration, Object... args) {
        showToast(context, String.format(format, args), duration);
    }

    /**
     * 顯示吐司
     *
     * @param text     文本
     * @param duration 顯示時長
     */
    private void showToast(final Context context, final CharSequence text, final int duration) {
        if (context == null) {
            return;
        }
        if (!ThreadUtil.isInMainThread()) {

            new Handler(Looper.getMainLooper()).post(() -> showToast(context, text, duration));
            return;
        }
        if (isJumpWhenMore) {
            cancelToast();
        }
        if (sToast == null) {
            sToast = Toast.makeText(context, text, duration);
        } else {
            sToast.setText(text);
            sToast.setDuration(duration);
        }
        sToast.show();
    }

    /**
     * 取消吐司顯示
     */
    public void cancelToast() {
        if (sToast != null) {
            sToast.cancel();
            sToast = null;
        }
    }
}