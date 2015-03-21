package com.i906.mpt.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.i906.mpt.MptApplication;
import com.i906.mpt.extension.ExtensionManager;
import com.i906.mpt.provider.MptInterface;

import javax.inject.Inject;

public class BaseFragment extends Fragment {

    @Inject
    protected ExtensionManager mExtensionManager;

    @Inject
    protected MptInterface mPrayerInterface;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MptApplication.component(getActivity()).inject(this);
    }
}
