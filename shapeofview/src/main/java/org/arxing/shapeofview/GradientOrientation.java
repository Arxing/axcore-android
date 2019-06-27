package org.arxing.shapeofview;

import android.graphics.drawable.GradientDrawable;

import com.annimon.stream.Stream;

enum GradientOrientation {
    top_bottom(1),
    bottom_top(2),
    left_right(3),
    right_left(4),
    topLeft_bottomRight(5),
    topRight_bottomLeft(6),
    bottomLeft_topRight(7),
    bottomRight_topLeft(8);

    public final int val;

    GradientOrientation(int val) {
        this.val = val;
    }

    public static GradientOrientation fromVal(int val) {
        return Stream.of(GradientOrientation.values()).filter(e -> e.val == val).findSingle().orElse(null);
    }

    public GradientDrawable.Orientation parse() {
        switch (this) {
            case top_bottom:
                return GradientDrawable.Orientation.TOP_BOTTOM;
            case bottom_top:
                return GradientDrawable.Orientation.BOTTOM_TOP;
            case left_right:
                return GradientDrawable.Orientation.LEFT_RIGHT;
            case right_left:
                return GradientDrawable.Orientation.RIGHT_LEFT;
            case topLeft_bottomRight:
                return GradientDrawable.Orientation.TL_BR;
            case topRight_bottomLeft:
                return GradientDrawable.Orientation.TR_BL;
            case bottomLeft_topRight:
                return GradientDrawable.Orientation.BL_TR;
            case bottomRight_topLeft:
                return GradientDrawable.Orientation.BR_TL;
            default:
                return GradientDrawable.Orientation.TOP_BOTTOM;
        }
    }
}
