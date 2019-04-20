package com.luminous.mpartner.custom_text_view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class TextViewRegular extends android.support.v7.widget.AppCompatTextView {


public TextViewRegular(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
        }

public TextViewRegular(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        }

public TextViewRegular(Context context) {
        super(context);
        init();
        }


private void init() {

        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
        "Lato-Regular.ttf");
        setTypeface(tf);

        }
        }


