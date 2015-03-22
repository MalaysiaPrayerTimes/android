package com.i906.mpt.fragment;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;

import com.i906.mpt.R;
import com.i906.mpt.view.CompassView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class QiblaFragment extends Fragment implements SensorEventListener {

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

    @InjectView(R.id.compass)
    protected CompassView mCompassView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_qibla, container, false);
        ButterKnife.inject(this, v);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mGravitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagneticSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mRotationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
    }

    private void loadSensorData(SensorEvent event) {
        int sensorType = event.sensor.getType();
        if (sensorType == Sensor.TYPE_ACCELEROMETER) mGravity = event.values;
        if (sensorType == Sensor.TYPE_MAGNETIC_FIELD) mGeomagnetic = event.values;
        if (sensorType == Sensor.TYPE_ROTATION_VECTOR) mRotationVector = event.values;
    }

    private void configureDeviceAngle() {
        switch (getActivity().getWindowManager().getDefaultDisplay().getRotation()) {
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

        Location c = getCurrentLocation();
        Location k = getKaabaLocation();

        mBearing = c.bearingTo(k);
        mBearing = (mBearing < 0) ? mBearing + 360 : mBearing;

        mDirection = mBearing - mAzimuth;
        mDirection = (mDirection < 0) ? mDirection + 360 : mDirection;

        mCompassView.setAngleZ(mDirection);
        mCompassView.setAngleX(-mHorizon);
    }

    // TODO
    protected Location getCurrentLocation() {
        if (mCurrentLocation == null) {
            mCurrentLocation = new Location("current");
            mCurrentLocation.setLatitude(3.1845389);
            mCurrentLocation.setLongitude(101.54654354);
        }

        return mCurrentLocation;
    }

    protected Location getKaabaLocation() {
        if (mKaabaLocation == null) {
            mKaabaLocation = new Location("kaaba");
            mKaabaLocation.setLatitude(21.42251);
            mKaabaLocation.setLongitude(39.82616);
        }

        return mKaabaLocation;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    @Override
    public void onResume() {
        super.onResume();

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

    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
}
