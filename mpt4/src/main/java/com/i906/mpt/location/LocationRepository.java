package com.i906.mpt.location;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.location.LocationRequest;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Observable;

/**
 * @author Noorzaini Ilhami
 */
@Singleton
public class LocationRepository {

    private static final int LOCATION_REQUEST_TIMEOUT = 15 * 1000;

    private ReactiveLocationProvider mProvider;

    @Inject
    public LocationRepository(Context context) {
        mProvider = new ReactiveLocationProvider(context);
    }

    public Observable<Location> getLocation() {
        LocationRequest req = LocationRequest.create();

        return mProvider.getUpdatedLocation(req)
                .timeout(LOCATION_REQUEST_TIMEOUT, TimeUnit.MILLISECONDS)
                .first();
    }

    public static float getDistance(Location a, Location b) {
        if (a == null || b == null) return Float.MAX_VALUE;
        return a.distanceTo(b);
    }
}
