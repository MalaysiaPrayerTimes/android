package com.i906.mpt.intro;

import com.i906.mpt.internal.PerActivity;
import com.i906.mpt.prayer.PrayerContext;
import com.i906.mpt.prayer.PrayerManager;

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
class IntroPresenter {

    private final PrayerManager mPrayerManager;
    private final CompositeSubscription mSubscription = new CompositeSubscription();

    private IntroView mView;

    @Inject
    IntroPresenter(PrayerManager prayer) {
        mPrayerManager = prayer;
    }

    public void refreshPrayerContext() {
        Subscription s = mPrayerManager.getPrayerContext(true)
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

    public void setView(IntroView view) {
        mView = view;
        if (view == null) mSubscription.clear();
    }
}
