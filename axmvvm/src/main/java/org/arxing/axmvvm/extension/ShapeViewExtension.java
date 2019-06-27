package org.arxing.axmvvm.extension;

import android.databinding.BindingAdapter;
import android.databinding.BindingMethod;
import android.databinding.BindingMethods;
import android.graphics.Color;

import org.arxing.attrparser.AttrParser;
import org.arxing.shapeofview.ShapeOfView;


@BindingMethods({
        @BindingMethod(type = ShapeOfView.class, attribute = "shape_bgColor", method = "setBgColor"),
        @BindingMethod(type = ShapeOfView.class, attribute = "shape_bgColors", method = "setBgColors")
})
public class ShapeViewExtension {

    @BindingAdapter({"shape_bgColor"}) public static void setBgColor(ShapeOfView view, int color) {
        view.setBgColor(color);
    }

    @BindingAdapter({"shape_bgColor"}) public static void setBgColor(ShapeOfView view, String color) {
        setBgColor(view, Color.parseColor(color));
    }

    @BindingAdapter({"shape_bgColors"}) public static void setBgColors(ShapeOfView view, String colors) {
        view.setBgColors(AttrParser.parseToColorArray(view.getContext(), colors));
    }
}
