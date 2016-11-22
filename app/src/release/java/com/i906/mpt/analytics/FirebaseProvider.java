package com.i906.mpt.analytics;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.i906.mpt.prefs.CommonPreferences;

import java.lang.ref.WeakReference;

import javax.inject.Inject;

/**
 * @author Noorzaini Ilhami
 */
class FirebaseProvider implements AnalyticsProvider {

    private FirebaseAnalytics mFirebaseAnalytics;
    private WeakReference<Activity> mActivity;

    @Inject
    FirebaseProvider(Context context, CommonPreferences prefs) {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);

        mFirebaseAnalytics.setUserProperty("beta_user",
                Boolean.toString(prefs.isBetaUser()));
    }

    @Override
    public void initialize(Application application) {
    }

    @Override
    public void trackViewedScreen(String screen) {
        setCurrentScreen(screen);
    }

    @Override
    public void trackViewedPrayerTimes() {
        setCurrentScreen(SCREEN_PRAYER_TIMES);
    }

    @Override
    public void trackViewedQibla() {
        setCurrentScreen(SCREEN_QIBLA);
    }

    @Override
    public void trackViewedMosqueList() {
        setCurrentScreen(SCREEN_MOSQUE_LIST);
    }

    private void setCurrentScreen(String screen) {
        if (mActivity == null) return;

        Activity activity = mActivity.get();
        if (activity == null) return;

        mFirebaseAnalytics.setCurrentScreen(activity, screen, null);
    }

    @Override
    public void startActivity(Activity activity) {
        mActivity = new WeakReference<>(activity);
    }

    @Override
    public void stopActivity(Activity activity) {
        mActivity.clear();
    }
}
