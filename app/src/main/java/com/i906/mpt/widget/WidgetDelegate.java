package com.i906.mpt.widget;

import android.content.Context;
import android.database.Cursor;

import com.i906.mpt.extension.Extension;
import com.i906.mpt.internal.PerService;
import com.i906.mpt.prayer.PrayerContext;
import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.impl.DefaultStorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.queries.Query;

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

    private final CompositeSubscription mSubscription = new CompositeSubscription();
    private final StorIOContentResolver mContentResolver;

    private WidgetHandler mHandler;

    @Inject
    WidgetDelegate(Context context) {
        mContentResolver = DefaultStorIOContentResolver.builder()
                .contentResolver(context.getContentResolver())
                .build();
    }

    public void refreshPrayerContext() {
        Query query = Query.builder()
                .uri(Extension.PRAYER_CONTEXT_URI)
                .build();

        Subscription s = mContentResolver.get()
                .cursor()
                .withQuery(query)
                .prepare()
                .asRxObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Cursor>() {
                    @Override
                    public void call(Cursor cursor) {
                        if (cursor != null && cursor.moveToFirst()) {
                            PrayerContext pc = PrayerContext.Mapper.fromCursor(cursor);
                            cursor.close();
                            handlePrayerContext(pc);
                        }
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
