package com.i906.mpt.prayer.ui;

import com.i906.mpt.internal.PerActivity;
import com.i906.mpt.prayer.PrayerContext;
import com.i906.mpt.prayer.PrayerManager;

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
class PrayerPresenter {

    private final PrayerManager mPrayerManager;
    private final CompositeSubscription mSubscription = new CompositeSubscription();

    private PrayerView mView;

    @Inject
    PrayerPresenter(PrayerManager prayer) {
        mPrayerManager = prayer;
    }

    public void getPrayerContext(final boolean refresh) {
        Subscription s = mPrayerManager.getPrayerContext(refresh)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        showLoading();
                    }
                })
                .subscribe(new Action1<PrayerContext>() {
                    @Override
                    public void call(PrayerContext prayerContext) {
                        showPrayerContext(prayerContext);
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

    private void showPrayerContext(PrayerContext prayer) {
        if (mView == null) return;
        mView.showPrayerContext(prayer);
    }

    private void showError(Throwable error) {
        if (mView == null) return;
        mView.showError(error);
    }

    public void setView(PrayerView view) {
        mView = view;
        if (view == null) mSubscription.clear();
    }
}
