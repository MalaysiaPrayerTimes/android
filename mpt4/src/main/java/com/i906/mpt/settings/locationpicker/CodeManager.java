package com.i906.mpt.settings.locationpicker;

import com.i906.mpt.api.prayer.PrayerClient;
import com.i906.mpt.api.prayer.PrayerCode;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * @author Noorzaini Ilhami
 */
@Singleton
class CodeManager {

    private final PrayerClient mPrayerClient;

    private Observable<List<PrayerCode>> mCodeObservable;

    @Inject
    CodeManager(PrayerClient client) {
        mPrayerClient = client;
        mCodeObservable = mPrayerClient.getSupportedCodes();
    }

    public Observable<List<PrayerCode>> getSupportedCodes(boolean refresh) {
        return mCodeObservable.share();
    }
}
