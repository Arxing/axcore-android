package org.arxing.axmvvm.extension;

import android.databinding.BindingAdapter;
import android.view.View;

import com.jakewharton.rxbinding2.view.RxView;

import org.arxing.axmvvm.FormatError;
import org.arxing.axmvvm.Rx;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;


public class ViewExtension {
    @BindingAdapter("view_show") public static void setShow(View view, boolean show) {
        view.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @BindingAdapter("view_testPrinting") public static void setTestPrinting(View view, Object data) {
        throw new FormatError("view=%s, data=%s", view.getClass().getSimpleName(), data.toString());
    }

    @BindingAdapter(value = {"view_onThrottleFirstClick", "view_throttleFirstClickDuration"}, requireAll = false)
    public static void onThrottleFirstClick(final View view, final View.OnClickListener click, long duration) {
        Rx.bind(RxView.clicks(view)
                      .throttleFirst(duration == 0 ? 500 : duration, TimeUnit.MILLISECONDS)
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(o -> click.onClick(view)));
    }
}
