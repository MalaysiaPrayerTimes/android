package com.i906.mpt.widget;

import com.i906.mpt.internal.PerService;
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
@PerService
class WidgetDelegate {

    private final PrayerManager mPrayerManager;
    private final CompositeSubscription mSubscription = new CompositeSubscription();

    private WidgetHandler mHandler;

    @Inject
    WidgetDelegate(PrayerManager prayer) {
        mPrayerManager = prayer;
    }

    public void refreshPrayerContext() {
        Subscription s = mPrayerManager.getPrayerContext(true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<PrayerContext>() {
                    @Override
                    public void call(PrayerContext prayerContext) {
                        handlePrayerContext(prayerContext);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        handleError(throwable);
                    }
                });

        mSubscription.add(s);
    }

    private void handlePrayerContext(PrayerContext prayerContext) {
        if (mHandler == null) return;
        mHandler.handlePrayerContext(prayerContext);
    }

    private void handleError(Throwable throwable) {
        if (mHandler == null) return;
        mHandler.handleError(throwable);
    }

    public void setHandler(WidgetHandler handler) {
        mHandler = handler;
        if (handler == null) mSubscription.clear();
    }
}
