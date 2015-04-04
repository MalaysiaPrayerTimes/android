package com.i906.mpt.util;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.subjects.PublishSubject;

@Singleton
public class LocationHelper {

    protected LocationManager mLocationManager;
    protected Criteria mCriteria;
    protected Observable<Location> mObservable;
    protected PublishSubject<Location> mPublisher;

    @Inject
    public LocationHelper(LocationManager manager) {
        mLocationManager = manager;
        mPublisher = PublishSubject.create();
    }

    public Observable<Location> getLocation() {

        if (mObservable == null) {
            mObservable = Observable.create(subscriber -> {
                LocationListener locationListener = new LocationListener() {
                    public void onLocationChanged(final Location location) {
                        subscriber.onNext(location);
                        Looper.myLooper().quit();
                    }

                    public void onStatusChanged(String provider, int status, Bundle extras) {
                    }

                    public void onProviderEnabled(String provider) {
                    }

                    public void onProviderDisabled(String provider) {
                    }
                };

                Looper.prepare();
                String provider = mLocationManager.getBestProvider(LocationHelper.this.getCriteria(), true);
                mLocationManager.requestSingleUpdate(provider, locationListener, Looper.myLooper());
                Looper.loop();
            });
        }

        return mObservable.mergeWith(mPublisher);
    }

    public void updateLocation(Location location) {
        mPublisher.onNext(location);
    }

    public Criteria getCriteria() {

        if (mCriteria == null) {
            mCriteria = new Criteria();
            mCriteria.setAccuracy(Criteria.ACCURACY_COARSE);
            mCriteria.setPowerRequirement(Criteria.POWER_LOW);
        }

        return mCriteria;
    }

    public void setCriteria(Criteria criteria) {
        mCriteria = criteria;
    }
}
