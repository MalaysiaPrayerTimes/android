package com.i906.mpt.settings;

import com.i906.mpt.internal.PerActivity;
import com.i906.mpt.prayer.PrayerCacheManager;
import com.i906.mpt.prayer.PrayerManager;
import com.pushtorefresh.storio.sqlite.operations.delete.DeleteResult;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * @author Noorzaini Ilhami
 */
@PerActivity
class MorePresenter {

    private final PrayerManager mPrayerManager;
    private final PrayerCacheManager mCacheManager;
    private final CompositeSubscription mSubscription = new CompositeSubscription();

    private MoreView mView;

    @Inject
    MorePresenter(PrayerManager prayerManager, PrayerCacheManager cacheManager) {
        mPrayerManager = prayerManager;
        mCacheManager = cacheManager;
    }

    public void clearLocationCache() {
        Subscription s = mCacheManager.clearLocationCache()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<DeleteResult>() {
                    @Override
                    public void call(DeleteResult deleteResult) {
                        showLocationCacheCleared();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        showLocationCacheErrored();
                    }
                });

        mSubscription.add(s);
    }

    public void clearPrayerDataCache() {
        Subscription s = mCacheManager.clearPrayerDataCache()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<DeleteResult>() {
                    @Override
                    public void call(DeleteResult deleteResult) {
                        showPrayerDataCacheCleared();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        showPrayerDataCacheErrored();
                    }
                });

        mSubscription.add(s);
    }

    private void showLocationCacheCleared() {
        mPrayerManager.notifyPreferenceChanged();

        if (mView == null) return;
        mView.showLocationCacheCleared();
    }

    private void showLocationCacheErrored() {
        if (mView == null) return;
        mView.showLocationCacheErrored();
    }

    private void showPrayerDataCacheCleared() {
        mPrayerManager.notifyPreferenceChanged();

        if (mView == null) return;
        mView.showPrayerDataCacheCleared();
    }

    private void showPrayerDataCacheErrored() {
        if (mView == null) return;
        mView.showPrayerDataCacheErrored();
    }

    public void setView(MoreView view) {
        mView = view;
        if (view == null) mSubscription.clear();
    }
}
