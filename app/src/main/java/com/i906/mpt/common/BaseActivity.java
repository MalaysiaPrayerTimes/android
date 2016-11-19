package com.i906.mpt.common;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.i906.mpt.R;
import com.i906.mpt.api.prayer.PrayerProviderException;
import com.i906.mpt.api.prayer.UnsupportedCoordinatesException;
import com.i906.mpt.internal.ActivityGraph;
import com.i906.mpt.internal.ActivityModule;
import com.i906.mpt.internal.Dagger;
import com.i906.mpt.internal.Graph;
import com.i906.mpt.location.ConnectionException;
import com.i906.mpt.location.LocationDisabledException;
import com.i906.mpt.location.LocationTimeoutException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import butterknife.ButterKnife;
import timber.log.Timber;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applyTheme();
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }

    public void setViewVisibility(View view, boolean visible, boolean animate) {
        if (view.getVisibility() == View.VISIBLE && visible) return;
        if (view.getVisibility() == View.GONE && !visible) return;

        if (visible) {
            if (animate) {
                view.startAnimation(
                        AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
            } else {
                view.clearAnimation();
            }
            view.setVisibility(View.VISIBLE);
        } else {
            if (animate) {
                view.startAnimation(
                        AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
            } else {
                view.clearAnimation();
            }
            view.setVisibility(View.GONE);
        }
    }

    public int getErrorMessage(Throwable e, int defaultResId) {
        if (e instanceof ConnectException || e instanceof UnknownHostException
                || e instanceof SocketTimeoutException) {
            return R.string.error_no_network;
        } else if (e instanceof ConnectionException) {
            return R.string.error_play_service;
        } else if (e instanceof SecurityException) {
            return R.string.error_no_location_permission;
        } else if (e instanceof LocationDisabledException || e instanceof LocationTimeoutException) {
            return R.string.error_no_location;
        } else if (e instanceof UnsupportedCoordinatesException) {
            Timber.w(e);
            return R.string.error_unsupported_coordinates;
        } else if (e instanceof PrayerProviderException) {
            return R.string.error_prayer_provider;
        } else {
            Timber.w(e);
            return defaultResId;
        }
    }

    protected ActivityGraph activityGraph() {
        return graph().activityGraph(new ActivityModule(this));
    }

    protected Graph graph() {
        return Dagger.getGraph(this);
    }

    protected void applyTheme() {
        if (graph().getInterfacePreferences().isLightTheme()) {
            setTheme(R.style.MptTheme_Light);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Dagger.getGraph(this)
                .getAnalyticsProvider()
                .startActivity(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        Dagger.getGraph(this)
                .getAnalyticsProvider()
                .stopActivity(this);
    }
}
