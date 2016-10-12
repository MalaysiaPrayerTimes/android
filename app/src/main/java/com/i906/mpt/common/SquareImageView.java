package com.i906.mpt.common;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * @author Noorzaini Ilhami
 */
public class SquareImageView extends AppCompatImageView {
    public SquareImageView(Context context) {
        super(context);
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int receivedWidth = MeasureSpec.getSize(widthMeasureSpec);
        int receivedHeight = MeasureSpec.getSize(heightMeasureSpec);

        if (receivedWidth < receivedHeight) {
            super.onMeasure(widthMeasureSpec, widthMeasureSpec);
        } else {
            super.onMeasure(heightMeasureSpec, heightMeasureSpec);
        }
    }
}
