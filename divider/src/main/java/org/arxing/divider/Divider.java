package org.arxing.divider;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import org.arxing.attrparser.AttrParser;
import org.arxing.axutils_android.UnitParser;


/**
 * Created by arxing on 2018/1/24.
 */

public class Divider extends View {
    private Properties properties;
    public final static int VERTICAL = 0;
    public final static int HORIZONTAL = 1;
    public final static int LEFT_RIGHT = 0x10;
    public final static int RIGHT_LEFT = 0x11;
    public final static int TOP_BOTTOM = 0x12;
    public final static int BOTTOM_TOP = 0x13;
    public final static int LT_RB = 0x14;
    public final static int RB_LT = 0x15;
    public final static int LB_RT = 0x16;
    public final static int RT_LB = 0x17;

    private class Properties {
        private int orientation;
        private int size;
        private int[] colors;
        private int colorsDirection;

        Properties() {
            orientation = HORIZONTAL;
            size = UnitParser.dp2px(getContext(), 1);
            colors = new int[]{
                    Color.WHITE
            };
            colorsDirection = LEFT_RIGHT;
        }
    }

    public Divider(Context context) {
        this(context, null);
    }

    public Divider(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttr(attrs);
        init();
    }

    private void initAttr(AttributeSet attrs) {
        properties = new Properties();
        if (attrs != null) {
            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.Divider);
            properties.orientation = ta.getInt(R.styleable.Divider_divider_orientation, properties.orientation);
            properties.size = ta.getDimensionPixelSize(R.styleable.Divider_divider_size, properties.size);
            if (ta.hasValue(R.styleable.Divider_divider_color)) {
                properties.colors = new int[]{
                        ta.getColor(R.styleable.Divider_divider_color, properties.colors[0])
                };
            } else if (ta.hasValue(R.styleable.Divider_divider_colors)) {
                properties.colors = AttrParser.parseToColorArray(getContext(), ta.getString(R.styleable.Divider_divider_colors));
            }
            properties.colorsDirection = ta.getInt(R.styleable.Divider_divider_colors_direction, properties.colorsDirection);
            ta.recycle();
        }
    }

    private GradientDrawable.Orientation parseOrientation(int direction) {
        GradientDrawable.Orientation orientation;
        switch (direction) {
            case LEFT_RIGHT:
                orientation = GradientDrawable.Orientation.LEFT_RIGHT;
                break;
            case RIGHT_LEFT:
                orientation = GradientDrawable.Orientation.RIGHT_LEFT;
                break;
            case TOP_BOTTOM:
                orientation = GradientDrawable.Orientation.TOP_BOTTOM;
                break;
            case BOTTOM_TOP:
                orientation = GradientDrawable.Orientation.BOTTOM_TOP;
                break;
            case LT_RB:
                orientation = GradientDrawable.Orientation.TL_BR;
                break;
            case RB_LT:
                orientation = GradientDrawable.Orientation.BR_TL;
                break;
            case LB_RT:
                orientation = GradientDrawable.Orientation.BL_TR;
                break;
            case RT_LB:
                orientation = GradientDrawable.Orientation.TR_BL;
                break;
            default:
                orientation = GradientDrawable.Orientation.LEFT_RIGHT;
                break;
        }
        return orientation;
    }


    private void init() {
        if (properties.colors.length > 1) {
            GradientDrawable gd = new GradientDrawable(parseOrientation(properties.colorsDirection), properties.colors);
            setBackground(gd);
        } else {
            setBackgroundColor(properties.colors[0]);
        }
    }

    public void setColor(int color) {
        properties.colors = new int[]{
                color
        };
        init();
    }

    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);

        if (properties.orientation == VERTICAL) {
            w = properties.size;

        } else if (properties.orientation == HORIZONTAL) {
            h = properties.size;
        }
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
