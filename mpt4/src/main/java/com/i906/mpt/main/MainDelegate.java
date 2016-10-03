package com.i906.mpt.main;

import android.location.Location;

import com.google.android.gms.location.LocationRequest;
import com.i906.mpt.location.LocationRepository;
import com.i906.mpt.prayer.PrayerContext;
import com.i906.mpt.prayer.PrayerManager;
import com.i906.mpt.prefs.HiddenPreferences;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * @author Noorzaini Ilhami
 */
@Singleton
class MainDelegate {

    private final long mLocationFastestInterval;
    private final long mLocationInterval;

    private final CompositeSubscription mSubscription = new CompositeSubscription();
    private final LocationRepository mLocationRepository;
    private final PrayerManager mPrayerManager;

    private MainHandler mView;

    @Inject
    MainDelegate(LocationRepository location, PrayerManager prayer, HiddenPreferences hpref) {
        mLocationRepository = location;
        mPrayerManager = prayer;

        mLocationFastestInterval = hpref.getLocationFastestInterval();
        mLocationInterval = hpref.getLocationInterval();
    }

    public void startLocationListener() {
        if (mSubscription.hasSubscriptions()) {
            return;
        }

        LocationRequest request = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setFastestInterval(mLocationFastestInterval)
                .setInterval(mLocationInterval);

        Subscription s = mLocationRepository.getLocation(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Location>() {
                    @Override
                    public void call(Location location) {
                        handleLocation(location);
                        updatePrayerContext(location);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        handleError(throwable);
                    }
                });

        mSubscription.add(s);
    }

    private void updatePrayerContext(Location location) {
        if (mPrayerManager.shouldUpdatePrayerContext(location)) {
            Subscription s = mPrayerManager.updatePrayerContext(location)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<PrayerContext>() {
                        @Override
                        public void call(PrayerContext prayerContext) {
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                        }
                    });

            mSubscription.add(s);
        }
    }

    private void handleLocation(Location location) {
        if (mView == null) return;
        mView.handleLocation(location);
    }

    private void handleError(Throwable throwable) {
        if (mView == null) return;
        mView.handleError(throwable);
    }

    public void setHandler(MainHandler view) {
        mView = view;
        if (view == null) mSubscription.clear();
    }
}
