package com.i906.mpt.util;

import android.content.Context;
import android.location.Location;
import android.support.annotation.Nullable;

import com.google.android.gms.location.LocationRequest;
import com.i906.mpt.model.LocationCache;
import com.i906.mpt.model.PrayerCode;
import com.i906.mpt.model.database.LocationCacheTableMeta;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.operations.get.PreparedGetListOfObjects;
import com.pushtorefresh.storio.sqlite.queries.Query;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Observable;
import timber.log.Timber;

@Singleton
public class LocationHelper {

    private static final int LOCATION_CACHE_DISTANCE_LIMIT = 5000;
    private static final int LOCATION_CACHE_AGE_LIMIT = 60 * 60 * 1000;
    private static final int LOCATION_REQUEST_TIMEOUT = 5 * 60 * 1000;
    private static final Object DATABASE_ACCESS = new Object();

    protected ReactiveLocationProvider mProvider;
    protected StorIOSQLite mDatabase;

    @Inject
    public LocationHelper(Context context, StorIOSQLite database) {
        mProvider = new ReactiveLocationProvider(context);
        mDatabase = database;
    }

    public Observable<Location> getLocation() {
        LocationRequest req = LocationRequest.create();
        return mProvider.getUpdatedLocation(req)
                .filter(location -> {
                    long now = System.currentTimeMillis();
                    return now - location.getTime() < LOCATION_CACHE_AGE_LIMIT;
                })
                .timeout(LOCATION_REQUEST_TIMEOUT, TimeUnit.MILLISECONDS,
                        mProvider.getLastKnownLocation())
                .first();
    }

    public Observable<LocationCache> getNearestCachedLocationObservable(Location location) {
        synchronized (DATABASE_ACCESS) {
            return getLocationCacheQuery()
                    .createObservable()
                    .take(1)
                    .flatMap(locationCaches -> {
                        Timber.d("Finding nearest location cache for: %s", location);
                        LocationCache cache = findNearestLocationCache(locationCaches, location);

                        if (cache != null) {
                            Timber.v("Found location cache: %s", cache);
                            return Observable.just(cache);
                        } else {
                            Timber.v("No location cache found.");
                            return Observable.empty();
                        }
                    });
        }
    }

    private PreparedGetListOfObjects<LocationCache> getLocationCacheQuery() {
        Query query = Query.builder()
                .table(LocationCacheTableMeta.TABLE)
                .build();

        return mDatabase.get()
                .listOfObjects(LocationCache.class)
                .withQuery(query)
                .prepare();
    }

    @Nullable
    private LocationCache findNearestLocationCache(List<LocationCache> list, Location location) {
        if (list == null || list.isEmpty()) return null;

        LocationCache nearestCache = list.get(0);
        Location temp = new Location("temp");
        Location nearest = new Location("cache");

        double distance;
        double nearestDistance = Double.MAX_VALUE;

        for (LocationCache lc : list) {
            temp.setLatitude(lc.getLatitude());
            temp.setLongitude(lc.getLongitude());
            distance = temp.distanceTo(location);

            if (distance < nearestDistance) {
                nearest.set(temp);
                nearestDistance = distance;
                nearestCache = lc;
            }
        }

        if (nearestDistance < LOCATION_CACHE_DISTANCE_LIMIT) {
            return nearestCache;
        } else {
            return null;
        }
    }

    public void saveLocationIntoCache(PrayerCode code, Location location) {
        long age = System.currentTimeMillis() - location.getTime();

        synchronized (DATABASE_ACCESS) {
            if (age < LOCATION_CACHE_AGE_LIMIT && getNearestCachedLocation(location) == null) {
                LocationCache lc = new LocationCache.Builder()
                        .setCode(code.getCode())
                        .setJakimCode(code.getJakimCode())
                        .setLatitude(location.getLatitude())
                        .setLongitude(location.getLongitude())
                        .build();

                Timber.d("Saving location into cache: %s", lc);

                mDatabase.put()
                        .object(lc)
                        .prepare()
                        .executeAsBlocking()
                        .wasInserted();
            }
        }
    }

    @Nullable
    private LocationCache getNearestCachedLocation(Location location) {
        synchronized (DATABASE_ACCESS) {
            List<LocationCache> cacheList = getLocationCacheQuery()
                    .executeAsBlocking();

            return findNearestLocationCache(cacheList, location);
        }
    }
}
