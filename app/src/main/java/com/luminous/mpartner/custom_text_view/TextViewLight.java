package com.luminous.mpartner.custom_text_view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class TextViewLight extends android.support.v7.widget.AppCompatTextView {


    public TextViewLight(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public TextViewLight(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextViewLight(Context context) {
        super(context);
        init();
    }


    private void init() {

        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                "Lato-Light.ttf");
        setTypeface(tf);
    }
}
