package org.arxing.shapeofview.shapes;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.annimon.stream.Stream;

import org.arxing.attrparser.AttrParser;
import org.arxing.shapeofview.GradientMode;
import org.arxing.shapeofview.R;
import org.arxing.shapeofview.ShapeOfView;
import org.arxing.shapeofview.manager.ClipPathManager;

public class RoundRectView extends ShapeOfView {

    private final RectF rectF = new RectF();
    //region border
    private final Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF borderRectF = new RectF();
    private final Path borderPath = new Path();
    private float topLeftRadius = 0f;
    private float topRightRadius = 0f;
    private float bottomRightRadius = 0f;
    private float bottomLeftRadius = 0f;
    private int[] borderColors;
    private float[] positions;
    private int borderDegree;
    private Shader.TileMode borderMode;
    private GradientMode gradientMode;
    private float borderRadius;

    private float borderWidthPx = 0f;
    //endregion

    public RoundRectView(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public RoundRectView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RoundRectView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            final TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.RoundRectView);
            if (attributes.hasValue(R.styleable.RoundRectView_shape_roundRect_radius)) {
                topLeftRadius = topRightRadius = bottomLeftRadius = bottomRightRadius = attributes.getDimensionPixelSize(R.styleable
                                                                                                                                 .RoundRectView_shape_roundRect_radius,
                                                                                                                         0);
            } else {
                topLeftRadius = attributes.getDimensionPixelSize(R.styleable.RoundRectView_shape_roundRect_topLeftRadius,
                                                                 (int) topLeftRadius);
                topRightRadius = attributes.getDimensionPixelSize(R.styleable.RoundRectView_shape_roundRect_topRightRadius,
                                                                  (int) topRightRadius);
                bottomLeftRadius = attributes.getDimensionPixelSize(R.styleable.RoundRectView_shape_roundRect_bottomLeftRadius,
                                                                    (int) bottomLeftRadius);
                bottomRightRadius = attributes.getDimensionPixelSize(R.styleable.RoundRectView_shape_roundRect_bottomRightRadius,
                                                                     (int) bottomRightRadius);
            }

            borderWidthPx = attributes.getDimensionPixelSize(R.styleable.RoundRectView_shape_roundRect_borderWidth, (int) borderWidthPx);

            if (attributes.hasValue(R.styleable.RoundRectView_shape_roundRect_borderColors)) {
                String input = attributes.getString(R.styleable.RoundRectView_shape_roundRect_borderColors);
                borderColors = AttrParser.parseToColorArray(getContext(), input);
                borderDegree = attributes.getInt(R.styleable.RoundRectView_shape_roundRect_borderColors_degree, 0);
                int ordinal = attributes.getInt(R.styleable.RoundRectView_shape_roundRect_borderColors_tileMode,
                                                Shader.TileMode.REPEAT.ordinal());
                borderMode = Stream.of(Shader.TileMode.values())
                                   .filter(m -> m.ordinal() == ordinal)
                                   .findSingle()
                                   .orElse(Shader.TileMode.REPEAT);
            } else {
                int singleColor = attributes.getColor(R.styleable.RoundRectView_shape_roundRect_borderColor, Color.WHITE);
                borderColors = new int[]{singleColor, singleColor};
                borderDegree = 0;
                borderMode = Shader.TileMode.REPEAT;
            }

            int gradientModeOrdinal = attributes.getInt(R.styleable.RoundRectView_shape_roundRect_borderColors_gradientMode,
                                                        GradientMode.LINEAR.ordinal());
            gradientMode = Stream.of(GradientMode.values()).filter(gm -> gm.ordinal() == gradientModeOrdinal).single();
            borderRadius = attributes.getDimensionPixelSize(R.styleable.RoundRectView_shape_roundRect_borderColors_radius, 1);

            if (attributes.hasValue(R.styleable.RoundRectView_shape_roundRect_positions)) {
                String input = attributes.getString(R.styleable.RoundRectView_shape_roundRect_positions);
                positions = AttrParser.parseToFloatArray(getContext(), input);
            }
            attributes.recycle();
        }
        borderPaint.setStyle(Paint.Style.STROKE);
        super.setClipPathCreator(new ClipPathManager.ClipPathCreator() {
            @Override public Path createClipPath(int width, int height) {
                rectF.set(0, 0, width, height);
                return generatePath(rectF,
                                    limitSize(topLeftRadius, width, height),
                                    limitSize(topRightRadius, width, height),
                                    limitSize(bottomRightRadius, width, height),
                                    limitSize(bottomLeftRadius, width, height));
            }

            @Override public boolean requiresBitmap() {
                return false;
            }
        });
    }

    protected float limitSize(float from, final float width, final float height) {
        return Math.min(from, Math.min(width, height));
    }

    @Override public void requiresShapeUpdate() {
        borderRectF.set(borderWidthPx / 2f, borderWidthPx / 2f, getWidth() - borderWidthPx / 2f, getHeight() - borderWidthPx / 2f);

        borderPath.set(generatePath(borderRectF, topLeftRadius, topRightRadius, bottomRightRadius, bottomLeftRadius));
        super.requiresShapeUpdate();
    }

    @Override protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        if (borderWidthPx > 0) {
            borderPaint.setStrokeWidth(borderWidthPx);
            Shader shader = buildShader();

            borderPaint.setShader(shader);
            canvas.drawPath(borderPath, borderPaint);
        }
    }

    private Shader buildShader() {
        Shader shader = null;
        switch (gradientMode) {
            case LINEAR:
                shader = buildLinear();
                break;
            case RADIAL:
                shader = buildRadial();
                break;
            case SWEEP:
                shader = buildSweep();
                break;
        }
        return shader;
    }

    private LinearGradient buildLinear() {
        float x0 = 0;
        float y0 = 0;
        float x1 = 0;
        float y1 = getHeight();
        LinearGradient lg = new LinearGradient(x0, y0, x1, y1, borderColors, positions, borderMode);
        Matrix matrix = new Matrix();
        matrix.setRotate(borderDegree);
        lg.setLocalMatrix(matrix);
        return lg;
    }

    private SweepGradient buildSweep() {
        float cx = getWidth() / 2f;
        float cy = getHeight() / 2f;
        return new SweepGradient(cx, cy, borderColors, positions);
    }

    private RadialGradient buildRadial() {
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;
        float radius = borderRadius;
        return new RadialGradient(centerX, centerY, radius, borderColors, positions, borderMode);
    }

    private Path generatePath(RectF rect, float topLeftRadius, float topRightRadius, float bottomRightRadius, float bottomLeftRadius) {
        return generatePath(false, rect, topLeftRadius, topRightRadius, bottomRightRadius, bottomLeftRadius);
    }

    private Path generatePath(boolean useBezier,
                              RectF rect,
                              float topLeftRadius,
                              float topRightRadius,
                              float bottomRightRadius,
                              float bottomLeftRadius) {
        final Path path = new Path();

        final float left = rect.left;
        final float top = rect.top;
        final float bottom = rect.bottom;
        final float right = rect.right;

        final float maxSize = Math.min(rect.width() / 2f, rect.height() / 2f);

        float topLeftRadiusAbs = Math.abs(topLeftRadius);
        float topRightRadiusAbs = Math.abs(topRightRadius);
        float bottomLeftRadiusAbs = Math.abs(bottomLeftRadius);
        float bottomRightRadiusAbs = Math.abs(bottomRightRadius);

        if (topLeftRadiusAbs > maxSize) {
            topLeftRadiusAbs = maxSize;
        }
        if (topRightRadiusAbs > maxSize) {
            topRightRadiusAbs = maxSize;
        }
        if (bottomLeftRadiusAbs > maxSize) {
            bottomLeftRadiusAbs = maxSize;
        }
        if (bottomRightRadiusAbs > maxSize) {
            bottomRightRadiusAbs = maxSize;
        }

        path.moveTo(left + topLeftRadiusAbs, top);
        path.lineTo(right - topRightRadiusAbs, top);

        //float left, float top, float right, float bottom, float startAngle, float sweepAngle, boolean forceMoveTo
        if (useBezier) {
            path.quadTo(right, top, right, top + topRightRadiusAbs);
        } else {
            final float arc = topRightRadius > 0 ? 90 : -270;
            path.arcTo(new RectF(right - topRightRadiusAbs * 2f, top, right, top + topRightRadiusAbs * 2f), -90, arc);
        }
        path.lineTo(right, bottom - bottomRightRadiusAbs);
        if (useBezier) {
            path.quadTo(right, bottom, right - bottomRightRadiusAbs, bottom);
        } else {
            final float arc = bottomRightRadiusAbs > 0 ? 90 : -270;
            path.arcTo(new RectF(right - bottomRightRadiusAbs * 2f, bottom - bottomRightRadiusAbs * 2f, right, bottom), 0, arc);
        }
        path.lineTo(left + bottomLeftRadiusAbs, bottom);
        if (useBezier) {
            path.quadTo(left, bottom, left, bottom - bottomLeftRadiusAbs);
        } else {
            final float arc = bottomLeftRadiusAbs > 0 ? 90 : -270;
            path.arcTo(new RectF(left, bottom - bottomLeftRadiusAbs * 2f, left + bottomLeftRadiusAbs * 2f, bottom), 90, arc);
        }
        path.lineTo(left, top + topLeftRadiusAbs);
        if (useBezier) {
            path.quadTo(left, top, left + topLeftRadiusAbs, top);
        } else {
            final float arc = topLeftRadiusAbs > 0 ? 90 : -270;
            path.arcTo(new RectF(left, top, left + topLeftRadiusAbs * 2f, top + topLeftRadiusAbs * 2f), 180, arc);
        }
        path.close();

        return path;
    }

    public float getTopLeftRadius() {
        return topLeftRadius;
    }

    public void setTopLeftRadius(float topLeftRadius) {
        this.topLeftRadius = topLeftRadius;
        requiresShapeUpdate();
    }

    public float getTopLeftRadiusDp() {
        return pxToDp(getTopLeftRadius());
    }

    public void setTopLeftRadiusDp(float topLeftRadius) {
        setTopLeftRadius(dpToPx(topLeftRadius));
    }

    public float getTopRightRadius() {
        return topRightRadius;
    }

    public void setTopRightRadius(float topRightRadius) {
        this.topRightRadius = topRightRadius;
        requiresShapeUpdate();
    }

    public float getTopRightRadiusDp() {
        return pxToDp(getTopRightRadius());
    }

    public void setTopRightRadiusDp(float topRightRadius) {
        setTopRightRadius(dpToPx(topRightRadius));
    }

    public float getBottomRightRadius() {
        return bottomRightRadius;
    }

    public void setBottomRightRadius(float bottomRightRadius) {
        this.bottomRightRadius = bottomRightRadius;
        requiresShapeUpdate();
    }

    public float getBottomRightRadiusDp() {
        return pxToDp(getBottomRightRadius());
    }

    public void setBottomRightRadiusDp(float bottomRightRadius) {
        setBottomRightRadius(dpToPx(bottomRightRadius));
    }

    public float getBottomLeftRadius() {
        return bottomLeftRadius;
    }

    public void setBottomLeftRadius(float bottomLeftRadius) {
        this.bottomLeftRadius = bottomLeftRadius;
        requiresShapeUpdate();
    }

    public float getBottomLeftRadiusDp() {
        return pxToDp(getBottomLeftRadius());
    }

    public void setBottomLeftRadiusDp(float bottomLeftRadius) {
        setBottomLeftRadius(dpToPx(bottomLeftRadius));
    }

    public int[] getBorderColors() {
        return borderColors;
    }

    public int getSingleBorderColor() {
        return getBorderColors()[0];
    }

    public void setBorderColor(int borderColor) {
        this.borderColors = new int[]{borderColor, borderColor};
        requiresShapeUpdate();
    }

    public void setBorderColors(int[] borderColors) {
        this.borderColors = borderColors;
        requiresShapeUpdate();
    }

    public float getBorderWidth() {
        return borderWidthPx;
    }

    public void setBorderWidth(float borderWidth) {
        this.borderWidthPx = borderWidth;
        requiresShapeUpdate();
    }

    public float getBorderWidthDp() {
        return pxToDp(getBorderWidth());
    }

    public void setBorderWidthDp(float borderWidth) {
        setBorderWidth(dpToPx(borderWidth));
    }
}