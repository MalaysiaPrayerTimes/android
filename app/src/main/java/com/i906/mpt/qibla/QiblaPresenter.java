package com.i906.mpt.qibla;

import android.location.Location;

import com.i906.mpt.internal.PerActivity;
import com.i906.mpt.location.LocationRepository;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * @author Noorzaini Ilhami
 */
@PerActivity
class QiblaPresenter {

    private final LocationRepository mLocationRepository;
    private final Location mKaabaLocation;
    private final CompositeSubscription mSubscription = new CompositeSubscription();

    private QiblaView mView;

    @Inject
    QiblaPresenter(LocationRepository location) {
        mLocationRepository = location;

        mKaabaLocation = new Location("kaaba");
        mKaabaLocation.setLatitude(21.42251);
        mKaabaLocation.setLongitude(39.82616);
    }

    public void getAzimuth() {
        Subscription s = mLocationRepository.getLocation()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        showLoading();
                    }
                })
                .subscribe(new Action1<Location>() {
                    @Override
                    public void call(Location location) {
                        showAzimuth(location);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        showError(throwable);
                    }
                });

        mSubscription.add(s);
    }

    private void showLoading() {
        if (mView == null) return;
        mView.showLoading();
    }

    private void showAzimuth(Location location) {
        if (mView == null) return;
        float azimuth = location.bearingTo(mKaabaLocation);
        azimuth = azimuth < 0 ? azimuth + 360 : azimuth;
        mView.showAzimuth(azimuth);
    }

    private void showError(Throwable error) {
        if (mView == null) return;
        mView.showError(error);
    }

    public void setView(QiblaView view) {
        mView = view;
        if (view == null) mSubscription.clear();
    }
}
