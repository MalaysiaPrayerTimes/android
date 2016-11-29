package com.i906.mpt.location;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.location.LocationRequest;
import com.i906.mpt.prefs.HiddenPreferences;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * @author Noorzaini Ilhami
 */
@Singleton
public class LocationRepository {

    private final long mCacheDuration;
    private final long mRequestTimeout;

    private final RxFusedLocation mFusedLocation;
    private final HiddenPreferences mHiddenPreferences;
    private Location mLastLocation;

    @Inject
    public LocationRepository(Context context, HiddenPreferences prefs) {
        mFusedLocation = new RxFusedLocation(context);

        mCacheDuration = prefs.getLocationCacheDuration();
        mRequestTimeout = prefs.getLocationRequestTimeout();
        mHiddenPreferences = prefs;
    }

    public Observable<Location> getLocation() {
        return getLocation(false);
    }

    public Observable<Location> getLocation(boolean force) {
        if (shouldRequestNewLocation() || force) {
            LocationRequest request = LocationRequest.create()
                    .setNumUpdates(1);

            return getLocation(request)
                    .timeout(mRequestTimeout, TimeUnit.MILLISECONDS)
                    .onErrorResumeNext(new Func1<Throwable, Observable<? extends Location>>() {
                        @Override
                        public Observable<? extends Location> call(Throwable e) {
                            if (e instanceof TimeoutException && mLastLocation == null) {
                                return Observable.error(new LocationTimeoutException());
                            } else if (mLastLocation == null) {
                                return Observable.error(e);
                            } else {
                                return Observable.just(mLastLocation);
                            }
                        }
                    })
                    .first();
        } else {
            return Observable.just(mLastLocation);
        }
    }

    public Observable<Location> getLocation(LocationRequest req) {
        return mFusedLocation.getLocation(req)
                .doOnNext(new Action1<Location>() {
                    @Override
                    public void call(Location location) {
                        cacheLocation(location);
                    }
                });
    }

    private boolean shouldRequestNewLocation() {
        Location cache = mHiddenPreferences.getLocationCache();

        if (mLastLocation == null && cache == null) {
            return true;
        }

        if (mLastLocation == null) {
            mLastLocation = cache;
        }

        if (System.currentTimeMillis() - mLastLocation.getTime() > mCacheDuration) {
            return true;
        }

        return false;
    }

    private void cacheLocation(Location location) {
        mLastLocation = location;
        mHiddenPreferences.setLocationCache(location);
    }

    public static float getDistance(Location a, Location b) {
        if (a == null || b == null) return Float.MAX_VALUE;
        return a.distanceTo(b);
    }
}
