package com.i906.mpt.view;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

import com.i906.mpt.R;

/**
 * Created by Noorzaini Ilhami on 18/10/2015.
 */
public class TintableButton extends AppCompatButton {

    private final TextHelper mTextHelper;

    public TintableButton(Context context) {
        this(context, null);
    }

    public TintableButton(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.tintableButtonStyle);
    }

    public TintableButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mTextHelper = TextHelper.create(this);
        mTextHelper.loadFromAttributes(attrs, defStyleAttr);
        mTextHelper.applyCompoundDrawablesTints();
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (mTextHelper != null) {
            mTextHelper.applyCompoundDrawablesTints();
        }
    }

    @Override
    public void setTextAppearance(Context context, int resId) {
        super.setTextAppearance(context, resId);
        if (mTextHelper != null) {
            mTextHelper.onSetTextAppearance(context, resId);
        }
    }
}
