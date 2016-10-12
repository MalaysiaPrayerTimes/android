package com.i906.mpt.settings.locationpicker;

import com.i906.mpt.api.prayer.PrayerCode;
import com.i906.mpt.internal.PerActivity;

import java.util.List;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * @author Noorzaini Ilhami
 */
@PerActivity
class LocationPickerPresenter {

    private final CodeManager mCodeManager;
    private final CompositeSubscription mSubscription = new CompositeSubscription();

    private CodeView mView;

    @Inject
    LocationPickerPresenter(CodeManager code) {
        mCodeManager = code;
    }

    void getCodeList(boolean refresh) {
        Subscription s = mCodeManager.getSupportedCodes(refresh)
                .flatMapIterable(new Func1<List<PrayerCode>, Iterable<PrayerCode>>() {
                    @Override
                    public Iterable<PrayerCode> call(List<PrayerCode> list) {
                        return list;
                    }
                })
                .toSortedList(new Func2<PrayerCode, PrayerCode, Integer>() {
                    @Override
                    public Integer call(PrayerCode x, PrayerCode y) {
                        return x.getCity().compareTo(y.getCity());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        showLoading();
                    }
                })
                .subscribe(new Action1<List<PrayerCode>>() {
                    @Override
                    public void call(List<PrayerCode> codes) {
                        showCodeList(codes);
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

    private void showCodeList(List<PrayerCode> codeList) {
        if (mView == null) return;
        mView.showCodeList(codeList);
    }

    private void showError(Throwable error) {
        if (mView == null) return;
        mView.showError(error);
    }

    void setView(CodeView view) {
        mView = view;
        if (view == null) mSubscription.clear();
    }
}
