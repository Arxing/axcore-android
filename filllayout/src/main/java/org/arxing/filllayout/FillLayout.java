package org.arxing.filllayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

public class FillLayout extends ViewGroup {
    public final static int MODE_NONE = 0;
    public final static int MODE_WIDTH = 1;
    public final static int MODE_HEIGHT = 2;

    private Properties properties;
    private OnItemClickListener itemClickListener;

    // rect
    private Rect childRect = new Rect();

    class Properties {
        int rowCount;
        int columnCount;
        float spaceVertical;
        float spaceHorizontal;
        int fillMode;

        {
            rowCount = 1;
            columnCount = 1;
            fillMode = MODE_NONE;
        }
    }

    public FillLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        properties = new Properties();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FillLayout);
        properties.rowCount = typedArray.getInt(R.styleable.FillLayout_fl_rowCount, properties.rowCount);
        properties.columnCount = typedArray.getInt(R.styleable.FillLayout_fl_columnCount, properties.columnCount);
        properties.spaceVertical = typedArray.getDimension(R.styleable.FillLayout_fl_space_vertical, properties.spaceVertical);
        properties.spaceHorizontal = typedArray.getDimension(R.styleable.FillLayout_fl_space_horizontal, properties.spaceHorizontal);
        properties.fillMode = typedArray.getInt(R.styleable.FillLayout_fl_fillMode, properties.fillMode);
        typedArray.recycle();
    }

    @Override public void addView(View child, int index, ViewGroup.LayoutParams params) {
        int totalChildCount = properties.rowCount * properties.columnCount;
        if (getChildCount() < totalChildCount) {
            super.addView(child, index, params);
            child.setOnClickListener(v -> {
                if (itemClickListener != null)
                    itemClickListener.onItemClick(child, indexOfChild(child), child.getTag());
            });
        }
    }

    @Override public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs, properties);
    }

    @Override protected void onLayout(boolean changed, int l, int t, int r, int b) {
        childRect.setEmpty();
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (lp.isFillWidth()) {
                childRect.left = lp.rContainer.left;
                childRect.right = lp.rContainer.right;
            } else {
                childRect.left = lp.rContainer.left;
                childRect.right = childRect.left + lp.realWidth;
            }
            if (lp.isFillHeight()) {
                childRect.top = lp.rContainer.top;
                childRect.bottom = lp.rContainer.bottom;
            } else {
                childRect.top = lp.rContainer.top;
                childRect.bottom = childRect.top + lp.realHeight;
            }

            if (lp.canUseGravity()) {
                Gravity.apply(lp.gravity, lp.displayWidth(), lp.displayHeight(), lp.rContainer, childRect);
            }
            child.layout(childRect.left, childRect.top, childRect.right, childRect.bottom);
        }
    }

    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int rows = properties.rowCount;
        int columns = properties.columnCount;
        float spaceHorizontal = properties.spaceHorizontal;
        float spaceVertical = properties.spaceVertical;

        int width;
        int height;

        measureChildren(widthMeasureSpec, heightMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            int[] widths = new int[rows];
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                int column = i % columns;
                int row = i / columns;
                int mWidth = child.getMeasuredWidth();
                if (mWidth > widths[row])
                    widths[row] = mWidth;
            }
            int maxWidth = 0;
            for (int each : widths) {
                if (each > maxWidth)
                    maxWidth = each;
            }
            width = maxWidth * columns;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            int[] heights = new int[columns];
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                int column = i % columns;
                int row = i / columns;
                int mHeight = child.getMeasuredHeight();
                if (mHeight > heights[column])
                    heights[column] = mHeight;
            }
            int maxHeight = 0;
            for (int each : heights) {
                if (each > maxHeight)
                    maxHeight = each;
            }
            height = maxHeight * rows;
        }

        //算間隔
        float totalSpaceHorizontal = (columns - 1) * spaceHorizontal;
        float totalSpaceVertical = (rows - 1) * spaceVertical;

        int childWidth = (int) ((width - totalSpaceHorizontal) / columns);
        int childHeight = (int) ((height - totalSpaceVertical) / rows);

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            int row = i / columns;
            int column = i % columns;
            int cL, cT, cR, cB;
            cL = (int) (column * childWidth + column * spaceHorizontal);
            cT = (int) (row * childHeight + row * spaceVertical);
            cR = cL + childWidth;
            cB = cT + childHeight;
            lp.rContainer.set(cL, cT, cR, cB);

            lp.realWidth = child.getMeasuredWidth();
            lp.realHeight = child.getMeasuredHeight();

            int childContainerWidthSpec = MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY);
            int childContainerHeightSpec = MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY);
            child.measure(childContainerWidthSpec, childContainerHeightSpec);
        }
        setMeasuredDimension(width, height);
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public static class LayoutParams extends ViewGroup.LayoutParams {
        Rect rContainer = new Rect();
        int fillMode;
        int realWidth;
        int realHeight;
        int gravity;
        Properties properties;

        public LayoutParams(Context c, AttributeSet attrs, Properties properties) {
            super(c, attrs);
            this.properties = properties;
            TypedArray typedArray = c.obtainStyledAttributes(attrs, R.styleable.FillLayout_Layout);
            if (typedArray.hasValue(R.styleable.FillLayout_Layout_fl_fillMode)) {
                fillMode = typedArray.getInt(R.styleable.FillLayout_Layout_fl_fillMode, MODE_NONE);
            } else {
                fillMode = properties.fillMode;
            }
            gravity = typedArray.getInt(R.styleable.FillLayout_Layout_android_gravity, Gravity.START | Gravity.TOP);
            typedArray.recycle();
        }

        boolean isFillWidth() {
            return (fillMode & MODE_WIDTH) > 0;
        }

        boolean isFillHeight() {
            return (fillMode & MODE_HEIGHT) > 0;
        }

        boolean canUseGravity() {
            return isFillWidth() || isFillHeight();
        }

        int displayWidth() {
            return isFillWidth() ? rContainer.width() : realWidth;
        }

        int displayHeight() {
            return isFillHeight() ? rContainer.height() : realHeight;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int index, Object tag);
    }
}
