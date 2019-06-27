package org.arxing.shapeofview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.content.res.AppCompatResources;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;

import org.arxing.attrparser.AttrParser;
import org.arxing.shapeofview.manager.ClipManager;
import org.arxing.shapeofview.manager.ClipPathManager;

public class ShapeOfView extends FrameLayout {

    private final Paint clipPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path clipPath = new Path();

    protected PorterDuffXfermode pdMode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);

    @Nullable protected Drawable drawable = null;
    private ClipManager clipManager = new ClipPathManager();
    private boolean requiersShapeUpdate = true;
    private Bitmap clipBitmap;
    private int[] bgColors;
    private GradientOrientation orientation;

    final Path rectView = new Path();

    public ShapeOfView(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public ShapeOfView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ShapeOfView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override public void setBackground(Drawable background) {
        //disabled here, please set a background to to this view child
        //        super.setBackground(background);
    }

    @Override public void setBackgroundResource(int resid) {
        //disabled here, please set a background to to this view child
        //        super.setBackgroundResource(resid);
    }

    @Override public void setBackgroundColor(int color) {
        //disabled here, please set a background to to this view child
        //        super.setBackgroundColor(color);
    }

    private void init(Context context, AttributeSet attrs) {
        clipPaint.setAntiAlias(true);

        setDrawingCacheEnabled(true);

        setWillNotDraw(false);

        clipPaint.setColor(Color.BLUE);
        clipPaint.setStyle(Paint.Style.FILL);
        clipPaint.setStrokeWidth(1);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
            clipPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            setLayerType(LAYER_TYPE_SOFTWARE, clipPaint); //Only works for software layers
        } else {
            clipPaint.setXfermode(pdMode);
            setLayerType(LAYER_TYPE_SOFTWARE, null); //Only works for software layers
        }

        if (attrs != null) {
            final TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.ShapeOfView);

            if (attributes.hasValue(R.styleable.ShapeOfView_shape_clip_drawable)) {
                final int resourceId = attributes.getResourceId(R.styleable.ShapeOfView_shape_clip_drawable, -1);
                if (-1 != resourceId) {
                    setDrawable(resourceId);
                }
            }

            if (attributes.hasValue(R.styleable.ShapeOfView_shape_bgColors)) {
                String input = attributes.getString(R.styleable.ShapeOfView_shape_bgColors);
                bgColors = AttrParser.parseToColorArray(getContext(), input);
                int val = attributes.getInt(R.styleable.ShapeOfView_shape_bgColors_orientation, GradientOrientation.left_right.val);
                orientation = GradientOrientation.fromVal(val);
            } else {
                int singleColor = attributes.getColor(R.styleable.ShapeOfView_shape_bgColor, Color.TRANSPARENT);
                bgColors = new int[]{singleColor, singleColor};
                orientation = GradientOrientation.left_right;
            }

            attributes.recycle();
        }
    }

    protected float dpToPx(float dp) {
        return dp * this.getContext().getResources().getDisplayMetrics().density;
    }

    protected float pxToDp(float px) {
        return px / this.getContext().getResources().getDisplayMetrics().density;
    }

    @Override protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            requiresShapeUpdate();
        }
    }

    private boolean requiresBitmap() {
        return isInEditMode() || (clipManager != null && clipManager.requiresBitmap()) || drawable != null;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
        requiresShapeUpdate();
    }

    public void setDrawable(int redId) {
        setDrawable(AppCompatResources.getDrawable(getContext(), redId));
    }

    public void setBgColor(int bgColor) {
        setBgColors(new int[]{bgColor, bgColor});
    }

    public void setBgColors(int[] bgColors) {
        this.bgColors = bgColors;
    }

    @Override protected void dispatchDraw(Canvas canvas) {
        //再畫背景
        Bitmap bgBitmap = createBgBitmap();
        canvas.drawBitmap(bgBitmap, 0, 0, null);

        //再畫本身
        super.dispatchDraw(canvas);

        //最後畫子類實作的裁切
        if (requiersShapeUpdate) {
            calculateLayout(canvas.getWidth(), canvas.getHeight());
            requiersShapeUpdate = false;
        }
        if (requiresBitmap()) {
            clipPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            canvas.drawBitmap(clipBitmap, 0, 0, clipPaint);
        } else {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
                canvas.drawPath(clipPath, clipPaint);
            } else {
                canvas.drawPath(rectView, clipPaint);
            }
        }

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
            setLayerType(LAYER_TYPE_HARDWARE, null);
        }
    }

    private Bitmap createBgBitmap() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        drawable.setOrientation(orientation.parse());
        drawable.setColors(bgColors);

        int w = getWidth();
        int h = getHeight();
        Bitmap.Config config = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        //注意，下面三行代码要用到，否则在View或者SurfaceView里的canvas.drawBitmap会看不到图
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }

    private void calculateLayout(int width, int height) {
        rectView.reset();
        rectView.addRect(0f, 0f, 1f * getWidth(), 1f * getHeight(), Path.Direction.CW);

        if (clipManager != null) {
            if (width > 0 && height > 0) {
                clipManager.setupClipLayout(width, height);
                clipPath.reset();
                clipPath.set(clipManager.createMask(width, height));

                if (requiresBitmap()) {
                    if (clipBitmap != null) {
                        clipBitmap.recycle();
                    }
                    clipBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                    final Canvas canvas = new Canvas(clipBitmap);

                    if (drawable != null) {
                        drawable.setBounds(0, 0, width, height);
                        drawable.draw(canvas);
                    } else {
                        canvas.drawPath(clipPath, clipManager.getPaint());
                    }
                }

                //invert the path for android P
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                    final boolean success = rectView.op(clipPath, Path.Op.DIFFERENCE);
                }

                //this needs to be fixed for 25.4.0
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && ViewCompat.getElevation(this) > 0f) {
                    try {
                        setOutlineProvider(getOutlineProvider());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        postInvalidate();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP) @Override public ViewOutlineProvider getOutlineProvider() {
        return new ViewOutlineProvider() {
            @Override public void getOutline(View view, Outline outline) {
                if (clipManager != null && !isInEditMode()) {
                    final Path shadowConvexPath = clipManager.getShadowConvexPath();
                    if (shadowConvexPath != null) {
                        try {
                            outline.setConvexPath(shadowConvexPath);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
    }

    public void setClipPathCreator(ClipPathManager.ClipPathCreator createClipPath) {
        ((ClipPathManager) clipManager).setClipPathCreator(createClipPath);
        requiresShapeUpdate();
    }

    public void requiresShapeUpdate() {
        this.requiersShapeUpdate = true;
        postInvalidate();
    }

}
