package org.arxing.axutils_android;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * Created by arxing on 2017/6/20.
 */

public class UnitParser {

    private static DisplayMetrics getDm(Context context) {
        return context.getResources().getDisplayMetrics();
    }

    public static float dp2px(Context context, float dp) {
        return dp * getDm(context).density;
    }

    public static float px2dp(Context context, float px) {
        return px / getDm(context).density;
    }

    public static float sp2px(Context context, float sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getDm(context));
    }

    public static float px2sp(Context context, float px) {
        return px / getDm(context).scaledDensity;
    }

    public static float dp2sp(Context context, float dp) {
        return dp2px(context, dp) / sp2px(context, dp);
    }

    public static float sp2dp(Context context, float sp) {
        return px2dp(context, sp2px(context, sp));
    }


    public static int dp2px(Context context, int dp) {
        return (int) (dp * getDm(context).density);
    }

    public static int px2dp(Context context, int px) {
        return (int) (px / getDm(context).density);
    }

    public static int sp2px(Context context, int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getDm(context));
    }

    public static int px2sp(Context context, int px) {
        return (int) (px / getDm(context).scaledDensity);
    }

    public static int dp2sp(Context context, int dp) {
        return dp2px(context, dp) / sp2px(context, dp);
    }

    public static int sp2dp(Context context, int sp) {
        return px2dp(context, sp2px(context, sp));
    }

}
