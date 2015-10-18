package com.i906.mpt.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.internal.text.AllCapsTransformationMethod;
import android.support.v7.internal.widget.TintInfo;
import android.util.AttributeSet;
import android.widget.TextView;

import com.i906.mpt.R;
import com.i906.mpt.ui.internal.TintManager;

class TextHelper {

    static TextHelper create(TextView textView) {
        if (Build.VERSION.SDK_INT >= 17) {
            return new TextHelperV17(textView);
        }
        return new TextHelper(textView);
    }

    private static final int[] VIEW_ATTRS = {android.R.attr.textAppearance,
            android.R.attr.drawableLeft, android.R.attr.drawableTop,
            android.R.attr.drawableRight, android.R.attr.drawableBottom };
    private static final int[] TEXT_APPEARANCE_ATTRS = {R.attr.textAllCaps};

    final TextView mView;

    private TintInfo mDrawableLeftTint;
    private TintInfo mDrawableTopTint;
    private TintInfo mDrawableRightTint;
    private TintInfo mDrawableBottomTint;

    TextHelper(TextView view) {
        mView = view;
    }

    void loadFromAttributes(AttributeSet attrs, int defStyleAttr) {
        final Context context = mView.getContext();
        final TintManager tintManager = TintManager.get(context);

        // First read the TextAppearance style id
        TypedArray a = context.obtainStyledAttributes(attrs, VIEW_ATTRS, defStyleAttr, 0);
        final int ap = a.getResourceId(0, -1);

        // Now read the compound drawable and grab any tints
        if (a.hasValue(1)) {
            mDrawableLeftTint = new TintInfo();
            mDrawableLeftTint.mHasTintList = true;
            mDrawableLeftTint.mTintList = tintManager.getTintList(a.getResourceId(1, 0));
        }
        if (a.hasValue(2)) {
            mDrawableTopTint = new TintInfo();
            mDrawableTopTint.mHasTintList = true;
            mDrawableTopTint.mTintList = tintManager.getTintList(a.getResourceId(2, 0));
        }
        if (a.hasValue(3)) {
            mDrawableRightTint = new TintInfo();
            mDrawableRightTint.mHasTintList = true;
            mDrawableRightTint.mTintList = tintManager.getTintList(a.getResourceId(3, 0));
        }
        if (a.hasValue(4)) {
            mDrawableBottomTint = new TintInfo();
            mDrawableBottomTint.mHasTintList = true;
            mDrawableBottomTint.mTintList = tintManager.getTintList(a.getResourceId(4, 0));
        }
        a.recycle();

        // Now check TextAppearance's textAllCaps value
        if (ap != -1) {
            TypedArray appearance = context.obtainStyledAttributes(ap, R.styleable.TextAppearance);
            if (appearance.hasValue(R.styleable.TextAppearance_textAllCaps)) {
                setAllCaps(appearance.getBoolean(R.styleable.TextAppearance_textAllCaps, false));
            }
            appearance.recycle();
        }

        // Now read the style's value
        a = context.obtainStyledAttributes(attrs, TEXT_APPEARANCE_ATTRS, defStyleAttr, 0);
        if (a.getBoolean(0, false)) {
            setAllCaps(true);
        }
        a.recycle();
    }

    void onSetTextAppearance(Context context, int resId) {
        TypedArray appearance = context.obtainStyledAttributes(resId, TEXT_APPEARANCE_ATTRS);
        if (appearance.hasValue(0)) {
            setAllCaps(appearance.getBoolean(0, false));
        }
        appearance.recycle();
    }

    void setAllCaps(boolean allCaps) {
        mView.setTransformationMethod(allCaps
                ? new AllCapsTransformationMethod(mView.getContext())
                : null);
    }

    void applyCompoundDrawablesTints() {
        if (mDrawableLeftTint != null || mDrawableTopTint != null ||
                mDrawableRightTint != null || mDrawableBottomTint != null) {
            final Drawable[] compoundDrawables = mView.getCompoundDrawables();
            applyCompoundDrawableTint(compoundDrawables[0], mDrawableLeftTint);
            applyCompoundDrawableTint(compoundDrawables[1], mDrawableTopTint);
            applyCompoundDrawableTint(compoundDrawables[2], mDrawableRightTint);
            applyCompoundDrawableTint(compoundDrawables[3], mDrawableBottomTint);
        }
    }

    final void applyCompoundDrawableTint(Drawable drawable, TintInfo info) {
        if (drawable != null && info != null) {
            TintManager.tintDrawable(drawable, info, mView.getDrawableState());
        }
    }
}
