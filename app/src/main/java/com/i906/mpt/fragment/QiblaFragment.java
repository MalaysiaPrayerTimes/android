package com.i906.mpt.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.i906.mpt.R;
import com.i906.mpt.util.SensorObservable;
import com.i906.mpt.view.CompassView;

import butterknife.InjectView;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class QiblaFragment extends BaseFragment {

    private Subscription mSubscription;

    @InjectView(R.id.compass)
    protected CompassView mCompassView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_qibla, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        int orientation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
        mSubscription = mQiblaHelper.requestQiblaAngles(orientation)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<SensorObservable.AngleInfo>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(SensorObservable.AngleInfo angleInfo) {
                        mCompassView.setAngleX(angleInfo.x);
                        mCompassView.setAngleZ(angleInfo.z);
                    }
                });
    }

    @Override
    public void onStop() {
        super.onStop();
        mSubscription.unsubscribe();
    }
}
