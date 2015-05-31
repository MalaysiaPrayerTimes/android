package com.i906.mpt.util;

import android.content.Context;

import com.i906.mpt.api.PrayerApi;
import com.i906.mpt.model.PrayerData;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.schedulers.Schedulers;

@Singleton
public class PrayerHelper {

    protected Context mContext;
    protected PrayerApi mApi;
    protected GeocoderHelper mGeocoderHelper;
    protected LocationHelper mLocationHelper;
    protected DateTimeHelper mDateTimeHelper;

    @Inject
    public PrayerHelper(Context context, PrayerApi api, GeocoderHelper h1, LocationHelper h2, DateTimeHelper h3) {
        mContext = context;
        mApi = api;
        mGeocoderHelper = h1;
        mLocationHelper = h2;
        mDateTimeHelper = h3;
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
                .flatMap(location -> mGeocoderHelper.getAddresses(location).subscribeOn(Schedulers.io()))
                .flatMap(this::getJakimCode)
                .flatMap(jakimCode -> mApi.getPrayerData(jakimCode, year, month + 1))
                .flatMap(prayerResponse -> Observable.just(prayerResponse.getPrayerData()));
    }

    protected Observable<String> getJakimCode(List<String> components) {
        return Observable.create(subscriber -> {
            // TODO
            subscriber.onNext("wlp-0");
        });
    }
}
