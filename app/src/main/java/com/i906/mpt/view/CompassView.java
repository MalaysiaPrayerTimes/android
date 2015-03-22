package com.i906.mpt.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CompassView extends ImageView {

    private static long FRAME_RATE = 1000 / 60;
    private static int DIVIDER = 8;

    private float mRotationX = 0f;
    private float mLastRotationX = 0f;

    private float mRotationZ = 0f;
    private float mLastRotationZ = 0f;

    public CompassView(Context context) {
        this(context, null);
    }

    public CompassView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CompassView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        updateAngleX();
        updateAngleZ();
        postInvalidateDelayed(FRAME_RATE);
    }

    private void updateAngleX() {
        float diff = (mRotationX - mLastRotationX) % 360;
        float change = diff / DIVIDER;

        if (Math.abs(diff) > 180) {
            mLastRotationX += change - 360 / DIVIDER;
        } else {
            mLastRotationX += change;
        }

        setRotationX(mLastRotationX);
    }

    private void updateAngleZ() {
        float diff = (mRotationZ - mLastRotationZ) % 360;
        float change = diff / DIVIDER;

        if (Math.abs(diff) > 180) {
            mLastRotationZ += change - 360 / DIVIDER;
        } else {
            mLastRotationZ += change;
        }
        setRotation(mLastRotationZ);
    }

    public void setAngleX(float rotation) {
        mRotationX = rotation;
    }

    public void setAngleZ(float rotation) {
        mRotationZ = rotation;
    }
}
