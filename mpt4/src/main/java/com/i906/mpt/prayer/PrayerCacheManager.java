package com.i906.mpt.prayer;

import android.location.Location;

import com.i906.mpt.api.prayer.PrayerData;
import com.i906.mpt.db.LocationCache;
import com.i906.mpt.db.LocationCacheMeta;
import com.i906.mpt.db.PrayerCache;
import com.i906.mpt.db.PrayerCacheMeta;
import com.i906.mpt.prefs.HiddenPreferences;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.queries.Query;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * @author Noorzaini Ilhami
 */
@Singleton
class PrayerCacheManager {

    private final PrayerDataFactory mPrayerDataFactory;
    private final StorIOSQLite mSqlite;
    private final HiddenPreferences mHiddenPreferences;

    @Inject
    PrayerCacheManager(StorIOSQLite sqlite, HiddenPreferences hpref) {
        mSqlite = sqlite;
        mHiddenPreferences = hpref;
        mPrayerDataFactory = new PrayerDataFactory();
    }

    boolean save(PrayerData data, Location location) {
        boolean l = saveLocation(data, location);
        boolean p = savePrayer(data);
        return l && p;
    }

    private boolean saveLocation(PrayerData data, Location location) {
        boolean saveCache = getClosestLocation(location)
                .isEmpty()
                .toBlocking()
                .first();

        if (!saveCache) {
            return true;
        }

        return mSqlite.put()
                .object(LocationCacheMeta.createCacheModel(data, location))
                .prepare()
                .executeAsBlocking()
                .wasInserted();
    }

    private boolean savePrayer(PrayerData data) {
        boolean saveCache = getPrayerData(data.getYear(), data.getMonth(), data.getCode())
                .isEmpty()
                .toBlocking()
                .first();

        if (!saveCache) {
            return true;
        }

        return mSqlite.put()
                .object(PrayerCacheMeta.createCacheModel(data))
                .prepare()
                .executeAsBlocking()
                .wasInserted();
    }

    Observable<PrayerData> get(final int year, final int month, final Location location) {
        return getClosestLocation(location)
                .flatMap(new Func1<LocationCache, Observable<PrayerData>>() {
                    @Override
                    public Observable<PrayerData> call(LocationCache cache) {
                        return getPrayerData(year, month, cache.getCode());
                    }
                });
    }

    private Observable<PrayerData> getPrayerData(int year, int month, String code) {
        Query query = Query.builder()
                .table(PrayerCacheMeta.TABLE)
                .where(PrayerCacheMeta.Columns.YEAR + " = ? AND " +
                        PrayerCacheMeta.Columns.MONTH + " = ? AND " +
                        PrayerCacheMeta.Columns.CODE + " = ?"
                )
                .whereArgs(year, month, code)
                .build();

        return mSqlite.get()
                .listOfObjects(PrayerCache.class)
                .withQuery(query)
                .prepare()
                .asRxObservable()
                .take(1)
                .flatMapIterable(new Func1<List<PrayerCache>, Iterable<PrayerCache>>() {
                    @Override
                    public Iterable<PrayerCache> call(List<PrayerCache> caches) {
                        return caches;
                    }
                })
                .map(mPrayerDataFactory);
    }

    private Observable<LocationCache> getClosestLocation(final Location location) {
        Query query = Query.builder()
                .table(LocationCacheMeta.TABLE)
                .build();

        return mSqlite.get()
                .listOfObjects(LocationCache.class)
                .withQuery(query)
                .prepare()
                .asRxObservable()
                .take(1)
                .flatMapIterable(new Func1<List<LocationCache>, Iterable<LocationCache>>() {
                    @Override
                    public Iterable<LocationCache> call(List<LocationCache> models) {
                        return models;
                    }
                })
                .scan(new CloserModel(location))
                .filter(new Func1<LocationCache, Boolean>() {
                    @Override
                    public Boolean call(LocationCache model) {
                        Location l = getLocation(model);
                        return location.distanceTo(l) < mHiddenPreferences.getLocationDistanceLimit();
                    }
                })
                .take(1);
    }

    private Location getLocation(LocationCache model) {
        Location l = new Location("l");
        l.setLatitude(model.getLatitude());
        l.setLongitude(model.getLongitude());
        return l;
    }

    private final class CloserModel implements Func2<LocationCache, LocationCache, LocationCache> {

        Location location;

        CloserModel(Location location) {
            this.location = location;
        }

        @Override
        public LocationCache call(LocationCache small, LocationCache current) {
            Location s = getLocation(small);
            Location c = getLocation(current);

            float sd = location.distanceTo(s);
            float cd = location.distanceTo(c);

            if (cd < sd) {
                return current;
            } else {
                return small;
            }
        }
    }

    private final class PrayerDataFactory implements Func1<PrayerCache, PrayerData> {

        @Override
        public PrayerData call(PrayerCache model) {
            return new PrayerData.Builder()
                    .setMonth(model.month)
                    .setYear(model.year)
                    .setLocation(model.place)
                    .setProvider(model.provider)
                    .setPrayerTimes(model.times)
                    .setCode(model.code)
                    .build();
        }
    }
}
