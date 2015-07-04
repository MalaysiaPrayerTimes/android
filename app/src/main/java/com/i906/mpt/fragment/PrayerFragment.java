package com.i906.mpt.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.i906.mpt.R;
import com.i906.mpt.extension.PrayerView;
import com.i906.mpt.provider.MptInterface;

import timber.log.Timber;

public class PrayerFragment extends BaseFragment implements MptInterface.MptListener {

    protected String mSelectedView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSelectedView = mPrefs.getSelectedPrayerView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FrameLayout v = (FrameLayout) inflater.inflate(R.layout.fragment_prayer, container, false);
        setPrayerView(v);
        return v;
    }

    private void setPrayerView(FrameLayout v) {
        Timber.v("Selected prayer view: %s", mSelectedView);
        PrayerView pv = mExtensionManager.getPrayerView(mSelectedView);

        if (pv == null) {
            mPrefs.resetSelectedPrayerView();
            mSelectedView = mPrefs.getSelectedPrayerView();
            pv = mExtensionManager.getPrayerView(mSelectedView);
        }

        if (pv != null) {
            pv.setInterface(mPrayerInterface);
            pv.setId(R.id.prayerview);

            if (v != null) {
                v.removeAllViews();
                v.addView(pv);
            }
        }
    }

    @Override
    public void onPrayerExtensionCrashed(Throwable t) {
        setPrayerView((FrameLayout) this.getView());
    }

    @Override
    public void onResume() {
        super.onResume();
        String sv = mPrefs.getSelectedPrayerView();

        if (!sv.equals(mSelectedView)) {
            mSelectedView = sv;
            Timber.v("Selected prayer view changed.");
            setPrayerView((FrameLayout) this.getView());
        }

        mPrayerInterface.refresh();
    }
}
