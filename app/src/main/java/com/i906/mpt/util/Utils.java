package com.i906.mpt.util;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@Singleton
public class Utils {

    protected Context mContext;

    @Inject
    public Utils(Context context) {
        mContext = context;
    }

    public boolean hasSufficientSensors() {
        SensorManager sm = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        Sensor a = sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        Sensor b = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        Sensor c = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        return a != null || (b != null && c != null);
    }

    public static <T> Observable.Transformer<T, T> applySchedulers() {
        return observable -> observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
