package com.i906.mpt.location;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.location.LocationRequest;
import com.i906.mpt.prefs.HiddenPreferences;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * @author Noorzaini Ilhami
 */
@Singleton
public class LocationRepository {

    private final long LOCATION_CACHE_DURATION;
    private final long LOCATION_REQUEST_TIMEOUT;

    private ReactiveLocationProvider mProvider;
    private Location mLastLocation;

    @Inject
    public LocationRepository(Context context, HiddenPreferences prefs) {
        mProvider = new ReactiveLocationProvider(context);

        LOCATION_CACHE_DURATION = prefs.getLocationCacheDuration();
        LOCATION_REQUEST_TIMEOUT = prefs.getLocationRequestTimeout();
    }

    public Observable<Location> getLocation() {
        return getLocation(false);
    }

    public Observable<Location> getLocation(boolean force) {
        if (shouldRequestNewLocation() || force) {
            return getLocation(LocationRequest.create())
                    .first();
        } else {
            return Observable.just(mLastLocation);
        }
    }

    public Observable<Location> getLocation(LocationRequest req) {
        return mProvider.getUpdatedLocation(req)
                .timeout(LOCATION_REQUEST_TIMEOUT, TimeUnit.MILLISECONDS)
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends Location>>() {
                    @Override
                    public Observable<? extends Location> call(Throwable e) {
                        if (mLastLocation == null) {
                            return Observable.error(e);
                        } else {
                            return Observable.just(mLastLocation);
                        }
                    }
                })
                .doOnNext(new Action1<Location>() {
                    @Override
                    public void call(Location location) {
                        mLastLocation = location;
                    }
                });
    }

    private boolean shouldRequestNewLocation() {
        if (mLastLocation == null) {
            return true;
        }

        if (System.currentTimeMillis() - mLastLocation.getTime() > LOCATION_CACHE_DURATION) {
            return true;
        }

        return false;
    }

    public static float getDistance(Location a, Location b) {
        if (a == null || b == null) return Float.MAX_VALUE;
        return a.distanceTo(b);
    }
}
