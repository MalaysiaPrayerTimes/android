package com.i906.mpt.util;

import com.i906.mpt.api.PrayerApi;
import com.i906.mpt.model.PrayerData;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

@Singleton
public class PrayerHelper {

    protected PrayerApi mApi;
    protected GeocoderHelper mGeocoderHelper;
    protected LocationHelper mLocationHelper;

    @Inject
    public PrayerHelper(PrayerApi api, GeocoderHelper h1, LocationHelper h2) {
        mApi = api;
        mGeocoderHelper = h1;
        mLocationHelper = h2;
    }

    public Observable<PrayerData> getPrayerData(int year, int month) {
        return mLocationHelper.getLocation()
                .flatMap(mGeocoderHelper::getAddresses)
                .flatMap(this::getJakimCode)
                .flatMap(jakimCode -> mApi.getPrayerData(jakimCode, year, month))
                .flatMap(prayerResponse -> Observable.just(prayerResponse.getPrayerData()));
    }

    protected Observable<String> getJakimCode(List<String> components) {
        return Observable.create(subscriber -> {
            // TODO
            subscriber.onNext("wlp-0");
        });
    }
}
