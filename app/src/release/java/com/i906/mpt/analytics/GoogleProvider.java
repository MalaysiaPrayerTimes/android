package com.i906.mpt.analytics;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.i906.mpt.BuildConfig;
import com.i906.mpt.R;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Noorzaini Ilhami
 */
@Singleton
class GoogleProvider implements AnalyticsProvider {

    private Tracker mTracker;

    @Inject
    GoogleProvider(Context context) {
        GoogleAnalytics ga = GoogleAnalytics.getInstance(context);

        if (BuildConfig.DEBUG) {
            ga.setDryRun(true);
        }

        mTracker = ga.newTracker(context.getString(R.string.google_analytics_id));
        mTracker.enableAdvertisingIdCollection(true);
        mTracker.enableAutoActivityTracking(false);
    }

    @Override
    public void initialize(Application application) {
    }

    @Override
    public void trackViewedScreen(String screen) {
        sendScreenName(screen);
    }

    @Override
    public void trackViewedPrayerTimes() {
        sendScreenName(SCREEN_PRAYER_TIMES);
    }

    @Override
    public void trackViewedQibla() {
        sendScreenName(SCREEN_QIBLA);
    }

    @Override
    public void trackViewedMosqueList() {
        sendScreenName(SCREEN_MOSQUE_LIST);
    }

    private void sendScreenName(String screenName) {
        mTracker.setScreenName(screenName);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void startActivity(Activity activity) {
    }

    @Override
    public void stopActivity(Activity activity) {
    }
}
