package com.i906.mpt.util;

import android.content.Context;
import android.location.Location;

import javax.inject.Inject;
import javax.inject.Singleton;

import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Observable;

@Singleton
public class LocationHelper {

    protected ReactiveLocationProvider mProvider;

    @Inject
    public LocationHelper(Context context) {
        mProvider = new ReactiveLocationProvider(context);
    }

    public Observable<Location> getLocation() {
        return mProvider.getLastKnownLocation();
    }
}
