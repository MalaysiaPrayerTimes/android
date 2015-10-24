package com.i906.mpt.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v7.internal.widget.TintInfo;
import android.support.v7.internal.widget.TintManager;
import android.util.AttributeSet;
import android.widget.TextView;

class TextHelperV17 extends TextHelper {

    private static final int[] VIEW_ATTRS_v17 = {
            android.R.attr.drawableStart, android.R.attr.drawableEnd };

    private TintInfo mDrawableStartTint;
    private TintInfo mDrawableEndTint;

    TextHelperV17(TextView view) {
        super(view);
    }

    void loadFromAttributes(AttributeSet attrs, int defStyleAttr) {
        super.loadFromAttributes(attrs, defStyleAttr);

        final Context context = mView.getContext();
        final TintManager tintManager = TintManager.get(context);

        // First read the TextAppearance style id
        TypedArray a = context.obtainStyledAttributes(attrs, VIEW_ATTRS_v17, defStyleAttr, 0);
        if (a.hasValue(0)) {
            mDrawableStartTint = new TintInfo();
            mDrawableStartTint.mHasTintList = true;
            mDrawableStartTint.mTintList = tintManager.getTintList(a.getResourceId(0, 0));
        }
        if (a.hasValue(1)) {
            mDrawableEndTint = new TintInfo();
            mDrawableEndTint.mHasTintList = true;
            mDrawableEndTint.mTintList = tintManager.getTintList(a.getResourceId(1, 0));
        }
        a.recycle();
    }

    @Override
    void applyCompoundDrawablesTints() {
        super.applyCompoundDrawablesTints();

        if (mDrawableStartTint != null || mDrawableEndTint != null) {
            final Drawable[] compoundDrawables = mView.getCompoundDrawablesRelative();
            applyCompoundDrawableTint(compoundDrawables[0], mDrawableStartTint);
            applyCompoundDrawableTint(compoundDrawables[2], mDrawableEndTint);
        }
    }
}
