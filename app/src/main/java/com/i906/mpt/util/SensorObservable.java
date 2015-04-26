package com.i906.mpt.util;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.view.Surface;

import rx.Observable;
import rx.Subscriber;
import rx.subscriptions.Subscriptions;

public class SensorObservable implements Observable.OnSubscribe<SensorObservable.AngleInfo>,
        SensorEventListener {

    protected Context mContext;
    protected int mDeviceOrientation = 0;

    protected boolean mUseRotation = false;
    protected boolean mUseAccelerometer = false;
    protected boolean mUseMagnetic = false;

    protected Location mKaabaLocation;
    protected Location mCurrentLocation;

    protected SensorManager mSensorManager;
    protected Sensor mGravitySensor;
    protected Sensor mMagneticSensor;
    protected Sensor mRotationSensor;

    private float[] mGravity = new float[3];
    private float[] mGeomagnetic = new float[3];
    private float[] mRotationVector = new float[3];
    private float[] mRotation = new float[9];
    private float[] mRotationMapped = new float[9];
    private float[] mOrientation = new float[3];

    protected float mAzimuth;
    protected float mBearing;
    protected float mDirection;
    protected float mHorizon;

    public SensorObservable(Context context, Location location, int orientation) {
        mContext = context;
        mCurrentLocation =  location;
        mDeviceOrientation = orientation;
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        mGravitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagneticSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mRotationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        mKaabaLocation = new Location("kaaba");
        mKaabaLocation.setLatitude(21.42251);
        mKaabaLocation.setLongitude(39.82616);
        start();
    }

    protected void start() {
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
    }

    protected void stop() {
        mSensorManager.unregisterListener(this);
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
        } else if (mUseAccelerometer && mUseMagnetic){
            SensorManager.getRotationMatrix(mRotation, null, mGravity, mGeomagnetic);
        }

        configureDeviceAngle();
        SensorManager.getOrientation(mRotationMapped, mOrientation);

        mHorizon = (float) Math.toDegrees(mOrientation[1]);
        mAzimuth = (float) Math.toDegrees(mOrientation[0]);
        if (mAzimuth < 0) mAzimuth += 360;

        mBearing = mCurrentLocation.bearingTo(mKaabaLocation);
        mBearing = (mBearing < 0) ? mBearing + 360 : mBearing;

        mDirection = mBearing - mAzimuth;
        mDirection = (mDirection < 0) ? mDirection + 360 : mDirection;

        if (mAngleInfo == null) mAngleInfo = new AngleInfo();
        mAngleInfo.x = -mHorizon;
        mAngleInfo.z = mDirection;
        mObserver.onNext(mAngleInfo);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    protected AngleInfo mAngleInfo;
    protected Subscriber<? super AngleInfo> mObserver;

    @Override
    public void call(Subscriber<? super AngleInfo> subscriber) {
        mObserver = subscriber;
        subscriber.add(Subscriptions.create(this::stop));
    }

    public static class AngleInfo {
        public float x;
        public float z;
    }
}