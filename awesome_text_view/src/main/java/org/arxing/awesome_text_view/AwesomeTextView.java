package org.arxing.awesome_text_view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class AwesomeTextView extends android.support.v7.widget.AppCompatTextView {
    private final static String PATH_SOLID = "fonts/fontawesome5-solid-900.otf";
    private final static String PATH_REGULAR = "fonts/fontawesome5-regular-400.otf";
    private final static String PATH_BRANDS = "fonts/fontawesome5-brands-400.otf";
    public final static int AWESOME_SOLID = 1;
    public final static int AWESOME_REGULAR = 2;
    public final static int AWESOME_BRANDS = 3;

    private int awesomeType;

    public AwesomeTextView(Context context) {
        super(context);
        initAttr(null);
        init();
    }

    public AwesomeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttr(attrs);
        init();
    }

    public AwesomeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(attrs);
        init();
    }

    private void initAttr(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.AwesomeTextView);
            awesomeType = typedArray.getInt(R.styleable.AwesomeTextView_awesomeType, AWESOME_SOLID);
            typedArray.recycle();
        } else {
            awesomeType = AWESOME_SOLID;
        }
    }

    private String getFontPath(int type) {
        switch (type) {
            case AWESOME_SOLID:
                return PATH_SOLID;
            case AWESOME_REGULAR:
                return PATH_REGULAR;
            case AWESOME_BRANDS:
                return PATH_BRANDS;
            default:
                return PATH_SOLID;
        }
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getResources().getAssets(), getFontPath(awesomeType));
        setTypeface(tf);
    }

    public void setAwesomeType(int awesomeType) {
        this.awesomeType = awesomeType;
        init();
    }
}
