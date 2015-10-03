package com.i906.mpt.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.i906.mpt.MptApplication;
import com.i906.mpt.extension.ExtensionManager;
import com.i906.mpt.provider.MptInterface;
import com.i906.mpt.util.LocationHelper;
import com.i906.mpt.util.MosqueHelper;
import com.i906.mpt.util.PrayerHelper;
import com.i906.mpt.util.QiblaHelper;
import com.i906.mpt.util.preference.GeneralPrefs;
import com.squareup.leakcanary.RefWatcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import rx.Subscription;

public abstract class BaseFragment extends Fragment {

    protected List<Subscription> mSubscriptions;

    @Inject
    protected GeneralPrefs mPrefs;

    @Inject
    protected LocationHelper mLocationHelper;

    @Inject
    protected MosqueHelper mMosqueHelper;

    @Inject
    protected QiblaHelper mQiblaHelper;

    @Inject
    protected PrayerHelper mPrayerHelper;

    @Inject
    protected ExtensionManager mExtensionManager;

    @Inject
    protected MptInterface mPrayerInterface;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MptApplication.component(getActivity()).inject(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    protected void addSubscription(Subscription... s) {
        if (mSubscriptions == null) mSubscriptions = new ArrayList<>();
        if (s != null) Collections.addAll(mSubscriptions, s);
    }

    protected void unsubscribeAll() {
        if (mSubscriptions == null) return;
        for (Subscription s : mSubscriptions) {
            s.unsubscribe();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        unsubscribeAll();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        RefWatcher refWatcher = MptApplication.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }
}
