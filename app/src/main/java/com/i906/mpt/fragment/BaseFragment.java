package com.i906.mpt.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.i906.mpt.MptApplication;
import com.i906.mpt.extension.ExtensionManager;
import com.i906.mpt.provider.MptInterface;
import com.i906.mpt.util.LocationHelper;
import com.i906.mpt.util.MosqueHelper;
import com.i906.mpt.util.QiblaHelper;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class BaseFragment extends Fragment {

    @Inject
    protected LocationHelper mLocationHelper;

    @Inject
    protected MosqueHelper mMosqueHelper;

    @Inject
    protected QiblaHelper mQiblaHelper;

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
        ButterKnife.inject(this, view);
    }
}
