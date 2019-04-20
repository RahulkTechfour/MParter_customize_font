package com.luminous.cardslibrary.card.action;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.ImageView;

import com.luminous.cardslibrary.card.Action;
import com.luminous.cardslibrary.card.Card;
import com.luminous.cardslibrary.card.OnActionClickListener;

public class ImageViewAction extends Action {
    @ColorInt
    private int mActionTextColor;
    private String mActionText;
    @Nullable
    private OnActionClickListener mListener;

    public ImageViewAction(@NonNull Context context) {
        super(context);
    }

    public int getTextColor() {
        return mActionTextColor;
    }

    public ImageViewAction setTextColor(@ColorInt final int color) {
        this.mActionTextColor = color;
        notifyActionChanged();
        return this;
    }

    public ImageViewAction setTextResourceColor(@ColorRes final int color) {
        return setTextColor(getContext().getResources().getColor(color));
    }

    public String getText() {
        return mActionText;
    }

    public ImageViewAction setText(@Nullable final String text) {
        this.mActionText = text;
        notifyActionChanged();
        return this;
    }

    public ImageViewAction setText(@StringRes final int textId) {
        return setText(getContext().getString(textId));
    }

    @Nullable
    public OnActionClickListener getListener() {
        return mListener;
    }

    public ImageViewAction setListener(@Nullable final OnActionClickListener listener) {
        this.mListener = listener;
        notifyActionChanged();
        return this;
    }

    @Override
    protected void onRender(@NonNull final View view, @NonNull final Card card) {
        ImageView textView = (ImageView) view;
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null) {
                    mListener.onActionClicked(view, card);
                }
            }
        });
    }
}
