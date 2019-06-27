package org.arxing.axmvvm.extension;

import android.databinding.BindingAdapter;
import android.text.InputType;
import android.widget.EditText;

public class EditTextExtension {

    @BindingAdapter({"editText_typePassword"}) public static void setInputType(EditText view, boolean isPassword) {
        view.setInputType(isPassword ? InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD : InputType.TYPE_CLASS_TEXT);
    }
}
