package com.i906.mpt.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.i906.mpt.R;
import com.i906.mpt.extension.ExtensionInfo;
import com.i906.mpt.extension.PrayerView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PrayerFragment extends BaseFragment {

    @InjectView(R.id.frame)
    protected FrameLayout mFrameView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_prayer, container, false);
        ButterKnife.inject(this, v);

        ExtensionInfo ei = mExtensionManager.getDefaultExtensions().get(0);
        PrayerView pv = mExtensionManager.getPrayerView(ei.getScreens().get(0));
        pv.setInterface(mPrayerInterface);

        mFrameView.addView(pv);

        return v;
    }
}
