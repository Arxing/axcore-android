package org.arxing.lazyrecyclerview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.SweepGradient;
import android.support.annotation.NonNull;
import android.support.annotation.Px;
import android.util.AttributeSet;
import android.view.View;

import org.arxing.utils.UnitParser;


class LoadingView extends LVBase {
    private Paint mPaint;
    private float mWidth;
    private @Px int mStrokeWidth;
    private float cx, cy;
    private int startColor;
    private int endColor;

    public LoadingView(Context context) {
        super(context);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override protected void initAttr(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.LoadingView);
        startColor = ta.getColor(R.styleable.LoadingView_startColor, Color.GRAY);
        endColor = ta.getColor(R.styleable.LoadingView_endColor, Color.TRANSPARENT);
        mStrokeWidth = ta.getDimensionPixelSize(R.styleable.LoadingView_strokeWidth, UnitParser.dp2px(getContext(), 15));
        ta.recycle();
    }

    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (getMeasuredWidth() > getHeight())
            mWidth = getMeasuredHeight();
        else
            mWidth = getMeasuredWidth();
        cx = mWidth / 2;
        cy = mWidth / 2;
        mPaint.setShader(new SweepGradient(cx, cy, startColor, endColor));
    }

    @Override protected void onDraw(Canvas canvas) {
        float radius = mWidth / 2 - mStrokeWidth / 2;
        canvas.drawCircle(cx, cy, radius, mPaint);
    }

    @Override protected void InitPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mStrokeWidth);
    }

    @Override protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE) {
            startAnim();
        } else {
            stopAnim();
        }
    }

    @Override protected void OnAnimationUpdate(ValueAnimator valueAnimator) {
        float value = (float) valueAnimator.getAnimatedValue();
        float angle = 360 * value;
        setRotation(angle);
        invalidate();
    }

    @Override protected void OnAnimationRepeat(Animator animation) {
    }

    @Override protected int OnStopAnim() {
        return 0;
    }

    @Override protected int SetAnimRepeatMode() {
        return ValueAnimator.RESTART;
    }

    @Override protected void AnimIsRunning() {
    }

    @Override protected int SetAnimRepeatCount() {
        return ValueAnimator.INFINITE;
    }

    public void setColor(int start, int end) {
        startColor = start;
        endColor = end;
        mPaint.setShader(new SweepGradient(cx, cy, start, end));
    }

    public void setStorkeWidth(@Px int width) {
        mStrokeWidth = width;
        mPaint.setStrokeWidth(mStrokeWidth);
    }
}
