package com.i906.mpt.util;

import android.content.Context;
import android.location.Location;

import com.i906.mpt.api.PrayerApi;
import com.i906.mpt.model.PrayerCode;
import com.i906.mpt.model.PrayerData;
import com.i906.mpt.model.database.PrayerCodesTableMeta;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.queries.Query;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.schedulers.Schedulers;
import timber.log.Timber;

@Singleton
public class PrayerHelper {

    protected Context mContext;
    protected PrayerApi mApi;
    protected GeocoderHelper mGeocoderHelper;
    protected LocationHelper mLocationHelper;
    protected DateTimeHelper mDateTimeHelper;
    protected StorIOSQLite mDatabase;

    @Inject
    public PrayerHelper(Context context, PrayerApi api, GeocoderHelper h1, LocationHelper h2,
                        DateTimeHelper h3, StorIOSQLite db) {
        mContext = context;
        mApi = api;
        mGeocoderHelper = h1;
        mLocationHelper = h2;
        mDateTimeHelper = h3;
        mDatabase = db;
    }

    public Observable<PrayerData> getPrayerData() {
        return getPrayerData(mDateTimeHelper.getCurrentYear(), mDateTimeHelper.getCurrentMonth());
    }

    public Observable<PrayerData> getNextPrayerData() {
        int m = mDateTimeHelper.getNextMonth();
        int y = mDateTimeHelper.getCurrentYear();

        if (mDateTimeHelper.isNextMonthNewYear()) {
            y = mDateTimeHelper.getNextYear();
        }

        return getPrayerData(y, m);
    }

    public Observable<PrayerData> getPrayerData(int year, int month) {
        return mLocationHelper.getLocation()
                .flatMap(location -> this.getPrayerCode(location).subscribeOn(Schedulers.io()))
                .flatMap(prayerCode -> mApi.getPrayerData(prayerCode.getCode(), year, month + 1))
                .flatMap(prayerResponse -> Observable.just(prayerResponse.getPrayerData()));
    }

    private Observable<PrayerCode> getPrayerCode(Location location) {
        Observable<PrayerCode> cache = mLocationHelper.getNearestCachedLocationObservable(location)
                .flatMap(locationCache -> {
                    Timber.v("Getting prayer code using cache.");
                    return getPrayerCodeFromDatabase(Query.builder()
                            .table(PrayerCodesTableMeta.TABLE)
                            .where(PrayerCodesTableMeta.Columns.JAKIM + " = ?")
                            .whereArgs(locationCache.getJakimCode())
                            .build());
                });

        Observable<PrayerCode> fresh = mGeocoderHelper.getAddresses(location)
                .flatMap(components -> getPrayerCodeFromDatabase(Query.builder()
                        .table(PrayerCodesTableMeta.TABLE)
                        .where(PrayerCodesTableMeta.Columns.PLACE +
                                " IN (" + makePlaceholders(components.size()) + ")")
                        .whereArgs(components)
                        .build()))
                .doOnNext(prayerCode -> mLocationHelper.saveLocationIntoCache(prayerCode, location));

        return Observable.concat(cache, fresh)
                .first();
    }

    private Observable<PrayerCode> getPrayerCodeFromDatabase(Query query) {
        return mDatabase.get()
                .listOfObjects(PrayerCode.class)
                .withQuery(query)
                .prepare()
                .createObservable()
                .take(1)
                .flatMap(prayerCodes -> {
                    if (!prayerCodes.isEmpty()) {
                        Timber.v("Found prayer code in database: %s", prayerCodes.get(0));
                        return Observable.just(prayerCodes.get(0));
                    } else {
                        Timber.v("No prayer codes found.");
                        return Observable.error(new PrayerCodeNotFound("Locations : " +
                                query.whereArgs().toString()));
                    }
                });
    }

    private static String makePlaceholders(int len) {
        if (len < 1) {
            throw new RuntimeException("No placeholders");
        } else {
            StringBuilder sb = new StringBuilder(len * 2 - 1);
            sb.append("?");
            for (int i = 1; i < len; i++) {
                sb.append(",?");
            }
            return sb.toString();
        }
    }

    public static class PrayerError extends RuntimeException {

        public PrayerError(String message) {
            super(message);
        }
    }

    public static class PrayerCodeNotFound extends PrayerError {

        public PrayerCodeNotFound(String message) {
            super(message);
        }
    }
}
