package com.i906.mpt.mosque.ui;

import com.i906.mpt.api.foursquare.Mosque;
import com.i906.mpt.internal.PerActivity;
import com.i906.mpt.mosque.MosqueManager;

import java.util.List;

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
class MosquePresenter {

    private final MosqueManager mMosqueManager;
    private final CompositeSubscription mSubscription = new CompositeSubscription();

    private MosqueView mView;

    @Inject
    MosquePresenter(MosqueManager manager) {
        mMosqueManager = manager;
    }

    public void getMosqueList(boolean refresh) {
        Subscription s = mMosqueManager.getMosqueList(refresh)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        showLoading();
                    }
                })
                .subscribe(new Action1<List<Mosque>>() {
                    @Override
                    public void call(List<Mosque> mosques) {
                        showMosqueList(mosques);
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

    private void showMosqueList(List<Mosque> mosqueList) {
        if (mView == null) return;
        mView.showMosqueList(mosqueList);
    }

    private void showError(Throwable error) {
        if (mView == null) return;
        mView.showError(error);
    }

    public void setView(MosqueView view) {
        mView = view;
        if (view == null) mSubscription.clear();
    }
}
