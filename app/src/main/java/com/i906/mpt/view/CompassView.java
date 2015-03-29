package com.i906.mpt.view;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CompassView extends ImageView {

    private static String KEY_VIEW_STATE = "view_state";
    private static String KEY_COMPASS_X_ROTATION = "compass_x_rotation";
    private static String KEY_COMPASS_Z_ROTATION = "compass_z_rotation";
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

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle b = (Bundle) state;
        Parcelable viewState = b.getParcelable(KEY_VIEW_STATE);
        mLastRotationX = b.getFloat(KEY_COMPASS_X_ROTATION, 0);
        mLastRotationZ = b.getFloat(KEY_COMPASS_Z_ROTATION, 0);
        super.onRestoreInstanceState(viewState);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable state = super.onSaveInstanceState();
        Bundle b = new Bundle();
        b.putParcelable(KEY_VIEW_STATE, state);
        b.putFloat(KEY_COMPASS_X_ROTATION, mLastRotationX);
        b.putFloat(KEY_COMPASS_Z_ROTATION, mLastRotationZ);
        return b;
    }
}
