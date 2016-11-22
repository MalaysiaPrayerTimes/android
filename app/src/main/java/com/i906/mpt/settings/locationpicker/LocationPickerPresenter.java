package com.i906.mpt.settings.locationpicker;

import com.i906.mpt.api.prayer.PrayerCode;
import com.i906.mpt.internal.PerActivity;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
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
    private final CodeFilter mCodeFilter = new CodeFilter();
    private final CodeSorter mCodeSorter = new CodeSorter();

    private CodeView mView;
    private List<PrayerCode> mPrayerCodes;

    @Inject
    LocationPickerPresenter(CodeManager code) {
        mCodeManager = code;
    }

    private Observable<List<PrayerCode>> getSupportedCodes(boolean refresh) {
        if (!refresh && mPrayerCodes != null) {
            return Observable.just(mPrayerCodes);
        }

        return mCodeManager.getSupportedCodes(refresh)
                .flatMapIterable(new Func1<List<PrayerCode>, Iterable<PrayerCode>>() {
                    @Override
                    public Iterable<PrayerCode> call(List<PrayerCode> list) {
                        return list;
                    }
                })
                .filter(mCodeFilter)
                .toSortedList(mCodeSorter)
                .doOnNext(new Action1<List<PrayerCode>>() {
                    @Override
                    public void call(List<PrayerCode> codeList) {
                        mPrayerCodes = codeList;
                    }
                });
    }

    void getCodeList(boolean refresh) {
        Subscription s = getSupportedCodes(refresh)
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

    void filter(final String filter) {
        Subscription s = getSupportedCodes(false)
                .flatMapIterable(new Func1<List<PrayerCode>, Iterable<PrayerCode>>() {
                    @Override
                    public Iterable<PrayerCode> call(List<PrayerCode> l) {
                        return l;
                    }
                })
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mCodeFilter.setFilter(filter);
                    }
                })
                .filter(mCodeFilter)
                .toSortedList(mCodeSorter)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<PrayerCode>>() {
                    @Override
                    public void call(List<PrayerCode> codeList) {
                        showCodeList(codeList);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
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

    private static class CodeSorter implements Func2<PrayerCode, PrayerCode, Integer> {
        @Override
        public Integer call(PrayerCode x, PrayerCode y) {
            return x.getCity().compareTo(y.getCity());
        }
    }

    private static class CodeFilter implements Func1<PrayerCode, Boolean> {

        String filter;

        CodeFilter() {
        }

        @Override
        public Boolean call(PrayerCode c) {
            if (filter == null) return true;
            String name = c.getCity().toLowerCase();
            String f = filter.toLowerCase();

            if (name.startsWith(f)) {
                return true;
            }

            String[] words = name.split(" ");

            for (String word : words) {
                if (word.startsWith(f)) {
                    return true;
                }
            }

            return false;
        }

        void setFilter(String filter) {
            this.filter = filter;
        }
    }
}
