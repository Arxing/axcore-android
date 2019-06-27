package org.arxing.axmvvm.extension;

import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class ImageViewExtension {

    @BindingAdapter(value = {"image_url", "image_placeHolder", "image_errorHolder", "image_defHolder"}, requireAll = false)
    public static void setImageUrl(ImageView view, String url, Drawable placeHolder, Drawable errorHolder, Drawable defHolder) {
        RequestOptions options = new RequestOptions().centerCrop();
        if (placeHolder != null)
            options = options.placeholder(placeHolder);
        if (errorHolder != null)
            options = options.error(errorHolder);

        if (url != null) {
            Glide.with(view).load(url).apply(options).into(view);
        } else {
            if (defHolder != null)
                Glide.with(view).load(defHolder).apply(options).into(view);
        }
    }

    @BindingAdapter(value = {"image_url", "image_placeHolder", "image_errorHolder", "image_defHolder"}, requireAll = false)
    public static void setImageUrl(ImageView view,
                                   String url,
                                   @DrawableRes int placeHolder,
                                   @DrawableRes int errorHolder,
                                   @DrawableRes int defHolder) {
        Drawable placeHolderDrawable = placeHolder == 0 ? null : ContextCompat.getDrawable(view.getContext(), placeHolder);
        Drawable errorHolderDrawable = errorHolder == 0 ? null : ContextCompat.getDrawable(view.getContext(), errorHolder);
        Drawable defHolderDrawable = defHolder == 0 ? null : ContextCompat.getDrawable(view.getContext(), defHolder);

        setImageUrl(view, url, placeHolderDrawable, errorHolderDrawable, defHolderDrawable);
    }

    @BindingAdapter({"android:src"}) public static void setImageResource(ImageView view, @DrawableRes int resId) {
        view.setImageResource(resId);
    }
}
