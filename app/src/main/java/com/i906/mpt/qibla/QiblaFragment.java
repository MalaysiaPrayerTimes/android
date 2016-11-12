package com.i906.mpt.qibla;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.i906.mpt.R;
import com.i906.mpt.analytics.AnalyticsProvider;
import com.i906.mpt.common.BaseFragment;
import com.i906.mpt.prefs.HiddenPreferences;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * @author Noorzaini Ilhami
 */
public class QiblaFragment extends BaseFragment implements QiblaView {

    @Inject
    QiblaPresenter mPresenter;

    @Inject
    AnalyticsProvider mAnalytics;

    @Inject
    HiddenPreferences mHiddenPreferences;

    @BindView(R.id.compass)
    CompassView mCompassView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityGraph().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_qibla, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!mHiddenPreferences.isCompassEnabled()) {
            mCompassView.setEnabled(false);
        }

        mPresenter.setView(this);
        mPresenter.getAzimuth();
    }

    @Override
    public void showAzimuth(float azimuth) {
        mCompassView.setAzimuth(azimuth);
    }

    @Override
    public void showError(Throwable error) {
    }

    @Override
    public void showLoading() {
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isAdded() && isVisibleToUser) {
            mAnalytics.trackViewedQibla();
        }
    }
}
