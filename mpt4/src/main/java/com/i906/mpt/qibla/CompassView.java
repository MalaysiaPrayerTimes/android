package com.i906.mpt.qibla;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Display;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;

import com.i906.mpt.R;

/**
 * @author Noorzaini Ilhami
 */
public class CompassView extends View implements SensorEventListener {

    private static final float THICKNESS_SCALE = 0.02f;

    private int mDeviceOrientation = 0;

    private boolean mUseRotation = false;
    private boolean mUseAccelerometer = false;
    private boolean mUseMagnetic = false;

    private SensorManager mSensorManager;
    private Sensor mGravitySensor;
    private Sensor mMagneticSensor;
    private Sensor mRotationSensor;

    private float[] mGravity = new float[3];
    private float[] mGeomagnetic = new float[3];
    private float[] mRotationVector = new float[3];
    private float[] mRotation = new float[9];
    private float[] mRotationMapped = new float[9];
    private float[] mOrientation = new float[3];

    private float mAzimuth = 292;
    private float mDirection;

    private boolean isUpdateTaskRunning = false;
    private Handler mHandler = new Handler();
    private ElapsedUpdater mUpdater;

    private Bitmap mBitmap;
    private Canvas mCanvas;

    private Bitmap mOuterCircleBitmap;

    private Bitmap mArrowBitmap;
    private Matrix mArrowRotator;

    private Paint mOuterCirclePaint;
    private Paint mCirclePaint;
    private Paint mEraserPaint;

    private RectF mCircleOuterBounds;
    private RectF mCircleInnerBounds;

    private RectF mArcOuterBounds;
    private RectF mArcInnerBounds;

    private float smoothSweep;
    private float smoothDelta;
    private float mDirectionDelta;
    private float mLastDirection;

    private Display mDefaultDisplay;
    private OrientationEventListener mOrientationEventListener;

    public CompassView(Context context) {
        this(context, null);
    }

    public CompassView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CompassView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaints();
        initSensors();

        mOrientationEventListener = new OrientationEventListener(getContext()) {
            @Override
            public void onOrientationChanged(int i) {
                configureOrientation();
            }
        };
    }

    private void initPaints() {
        mOuterCirclePaint = new Paint();
        mOuterCirclePaint.setAntiAlias(true);
        mOuterCirclePaint.setColor(Color.WHITE);

        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setColor(ContextCompat.getColor(getContext(), R.color.colorAccent));

        mEraserPaint = new Paint();
        mEraserPaint.setAntiAlias(true);
        mEraserPaint.setColor(Color.TRANSPARENT);
        mEraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    private void initSensors() {
        mDefaultDisplay = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay();

        mSensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        mGravitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagneticSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mRotationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
    }

    private void initOuterCircle(int w, int h) {
        mOuterCircleBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mOuterCircleBitmap.eraseColor(Color.TRANSPARENT);
        Canvas outerCircleCanvas = new Canvas(mOuterCircleBitmap);

        outerCircleCanvas.drawOval(mCircleOuterBounds, mOuterCirclePaint);
        outerCircleCanvas.drawOval(mCircleInnerBounds, mEraserPaint);
    }

    private void initArrows(int w, int h) {
        mArrowBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mArrowBitmap.eraseColor(Color.TRANSPARENT);
        Canvas arrowCanvas = new Canvas(mArrowBitmap);
        mArrowRotator = new Matrix();

        int hw = w / 2;
        int hh = h / 2;

        int t1 = (int) (w * 0.35);
        int t2 = (int) (w * 0.05);

        Point d = new Point(hw - t2, hh);
        Point e = new Point(hw + t2, hh);
        Point f = new Point(hw, hh - t1);
        Point g = new Point(hw, hh + t1);

        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(d.x, d.y);
        path.lineTo(e.x, e.y);
        path.lineTo(f.x, f.y);
        path.close();

        Path path2 = new Path();
        path2.setFillType(Path.FillType.EVEN_ODD);
        path2.moveTo(d.x, d.y);
        path2.lineTo(e.x, e.y);
        path2.lineTo(g.x, g.y);
        path2.close();

        arrowCanvas.drawPath(path, mCirclePaint);
        arrowCanvas.drawPath(path2, mOuterCirclePaint);
        arrowCanvas.save();
    }

    private void configureOrientation() {
        mDeviceOrientation = mDefaultDisplay.getRotation();
    }

    private void startSensors() {
        boolean v1 = false;
        boolean v2 = false;

        boolean v3 = mSensorManager.registerListener(this,
                mRotationSensor, SensorManager.SENSOR_DELAY_GAME);

        if (v3) {
            mUseRotation = true;
        } else {
            v1 = mSensorManager.registerListener(this,
                    mGravitySensor, SensorManager.SENSOR_DELAY_GAME);

            v2 = mSensorManager.registerListener(this,
                    mMagneticSensor, SensorManager.SENSOR_DELAY_GAME);
        }

        if (v1 && v2) {
            mUseAccelerometer = true;
            mUseMagnetic = true;
        }

        mOrientationEventListener.enable();
    }

    private void stopSensors() {
        mSensorManager.unregisterListener(this);
        mOrientationEventListener.disable();
    }

    private void startUpdating() {
        if (mUpdater == null) mUpdater = new ElapsedUpdater();
        if (!isUpdateTaskRunning) {
            mHandler.post(mUpdater);
            isUpdateTaskRunning = true;
        }
    }

    private void stopUpdating() {
        if (isUpdateTaskRunning) {
            mHandler.removeCallbacks(mUpdater);
            isUpdateTaskRunning = false;
        }
    }

    private void configureDeviceAngle() {
        switch (mDeviceOrientation) {
            case Surface.ROTATION_0: // Portrait
                SensorManager.remapCoordinateSystem(mRotation, SensorManager.AXIS_X,
                        SensorManager.AXIS_Y, mRotationMapped);
                break;
            case Surface.ROTATION_90: // Landscape
                SensorManager.remapCoordinateSystem(mRotation, SensorManager.AXIS_Y,
                        SensorManager.AXIS_MINUS_Z, mRotationMapped);
                break;
            case Surface.ROTATION_180: // Portrait
                SensorManager.remapCoordinateSystem(mRotation, SensorManager.AXIS_MINUS_X,
                        SensorManager.AXIS_MINUS_Y, mRotationMapped);
                break;
            case Surface.ROTATION_270: // Landscape
                SensorManager.remapCoordinateSystem(mRotation, SensorManager.AXIS_MINUS_Y,
                        SensorManager.AXIS_Z, mRotationMapped);
                break;
        }
    }

    private void loadSensorData(SensorEvent event) {
        int sensorType = event.sensor.getType();
        if (sensorType == Sensor.TYPE_ACCELEROMETER) mGravity = event.values;
        if (sensorType == Sensor.TYPE_MAGNETIC_FIELD) mGeomagnetic = event.values;
        if (sensorType == Sensor.TYPE_ROTATION_VECTOR) mRotationVector = event.values;
    }

    @Override
    public void onSensorChanged(SensorEvent e) {
        loadSensorData(e);

        if (mUseRotation) {
            SensorManager.getRotationMatrixFromVector(mRotation, mRotationVector);
        } else if (mUseAccelerometer && mUseMagnetic) {
            SensorManager.getRotationMatrix(mRotation, null, mGravity, mGeomagnetic);
        }

        configureDeviceAngle();
        SensorManager.getOrientation(mRotationMapped, mOrientation);

        float bearing = (float) Math.toDegrees(mOrientation[0]);
        if (bearing < 0) bearing += 360;

        mDirection = bearing - mAzimuth;
        mDirection = mAzimuth - bearing;
        mDirection = (mDirection < 0) ? mDirection + 360 : mDirection;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void setAzimuth(float azimuth) {
        mAzimuth = azimuth;
    }

    private static float calculateAngleDifference(float angle1, float angle2) {
        float diff = (angle2 - angle1 + 180) % 360 - 180;
        return diff < -180 ? diff + 360 : diff;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        smoothDelta += (mDirectionDelta - smoothDelta) * 0.1;

        if (mDirection > 180) {
            smoothSweep += (-(360 - mDirection) - smoothSweep) * 0.2;
        } else {
            smoothSweep += (mDirection - smoothSweep) * 0.2;
        }

        mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        mCanvas.drawArc(mArcOuterBounds, 270, smoothSweep, true, mCirclePaint);
        mCanvas.drawOval(mArcInnerBounds, mEraserPaint);

        mDirectionDelta = calculateAngleDifference(mLastDirection, mDirection);
        mArrowRotator.postRotate(smoothDelta, getWidth() / 2, getHeight() / 2);
        mLastDirection = mDirection;

        canvas.drawBitmap(mArrowBitmap, mArrowRotator, null);
        canvas.drawBitmap(mOuterCircleBitmap, 0, 0, null);
        canvas.drawBitmap(mBitmap, 0, 0, null);
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

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (w != oldw || h != oldh) {
            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mBitmap.eraseColor(Color.TRANSPARENT);
            mCanvas = new Canvas(mBitmap);

            updateAzimuthArc();
            updateOuterCircle();
            initOuterCircle(w, h);
            initArrows(w, h);
        }

        super.onSizeChanged(w, h, oldw, oldh);
        invalidate();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startSensors();
        startUpdating();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopSensors();
        stopUpdating();
    }

    private void updateAzimuthArc() {
        final float thickness = getWidth() * THICKNESS_SCALE * 2;

        mArcOuterBounds = new RectF(0, 0, getWidth(), getHeight());
        mArcInnerBounds = new RectF(
                mArcOuterBounds.left + thickness,
                mArcOuterBounds.top + thickness,
                mArcOuterBounds.right - thickness,
                mArcOuterBounds.bottom - thickness
        );
    }

    private void updateOuterCircle() {
        final float thickness = getWidth() * THICKNESS_SCALE;

        mCircleOuterBounds = new RectF(
                mArcInnerBounds.left + thickness,
                mArcInnerBounds.top + thickness,
                mArcInnerBounds.right - thickness,
                mArcInnerBounds.bottom - thickness
        );

        mCircleInnerBounds = new RectF(
                mCircleOuterBounds.left + thickness,
                mCircleOuterBounds.top + thickness,
                mCircleOuterBounds.right - thickness,
                mCircleOuterBounds.bottom - thickness
        );
    }

    private class ElapsedUpdater implements Runnable {

        @Override
        public void run() {
            invalidate();
            mHandler.postDelayed(this, 16);
        }
    }
}
