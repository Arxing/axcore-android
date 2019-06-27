package org.arxing.shapeofview.shapes;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.arxing.shapeofview.R;
import org.arxing.axutils_android.UnitParser;

public class RoundTextView extends RoundRectView {
    private TextView textView;

    private Properties properties;

    class Properties {
        String text;
        int gravity;
        @ColorInt int textColor;
        float textSize;

        {
            gravity = Gravity.START | Gravity.TOP;
            textColor = Color.BLACK;
            textSize = UnitParser.sp2px(getContext(), 12);
        }
    }

    public RoundTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        properties = new Properties();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundTextView);
        properties.text = typedArray.getString(R.styleable.RoundTextView_android_text);
        properties.gravity = typedArray.getInt(R.styleable.RoundTextView_android_gravity, properties.gravity);
        properties.textColor = typedArray.getColor(R.styleable.RoundTextView_android_textColor, properties.textColor);
        properties.textSize = typedArray.getDimension(R.styleable.RoundTextView_android_textSize, properties.textSize);
        typedArray.recycle();

        textView = new TextView(context);
        addView(textView);
        syncAttr();
    }

    @Override public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (child instanceof TextView && getChildCount() == 0) {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) params;
            lp.width = FrameLayout.LayoutParams.WRAP_CONTENT;
            lp.height = FrameLayout.LayoutParams.WRAP_CONTENT;
            lp.gravity = properties.gravity;
            super.addView(child, index, params);
        }
    }

    private void syncAttr() {
        textView.setText(properties.text);
        textView.setTextColor(properties.textColor);
        textView.setTextSize(UnitParser.px2sp(getContext(), properties.textSize));
    }

    public void setText(@StringRes int text) {
        setText(getResources().getString(text));
    }

    public void setText(String text) {
        properties.text = text;
        syncAttr();
    }

    public void setTextColor(@ColorInt int color) {
        properties.textColor = color;
        syncAttr();
    }

    public void setTextSizeSp(float sizeSp) {
        properties.textSize = UnitParser.sp2px(getContext(), sizeSp);
        syncAttr();
    }
}
