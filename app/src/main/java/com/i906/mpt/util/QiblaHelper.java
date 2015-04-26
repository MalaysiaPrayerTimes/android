package com.i906.mpt.util;

import android.content.Context;
import android.location.Location;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

@Singleton
public class QiblaHelper {

    protected Context mContext;
    protected LocationHelper mLocationHelper;

    @Inject
    public QiblaHelper(Context context, LocationHelper h1) {
        mContext = context;
        mLocationHelper = h1;
    }

    public Observable<SensorObservable.AngleInfo> requestQiblaAngles(int orientation) {
        return mLocationHelper.getLocation()
                .flatMap((Location location) ->
                        Observable.create(new SensorObservable(mContext, location, orientation)));
    }
}
