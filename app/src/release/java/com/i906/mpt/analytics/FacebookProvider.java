package com.i906.mpt.analytics;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsLogger;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Noorzaini Ilhami
 */
@Singleton
class FacebookProvider implements AnalyticsProvider {

    private AppEventsLogger mLogger;

    @Inject
    FacebookProvider() {
    }

    @Override
    public void initialize(Application application) {
        FacebookSdk.sdkInitialize(application.getApplicationContext());
        AppEventsLogger.activateApp(application);
    }

    @Override
    public void trackViewedScreen(String screen) {
        logEvent(screen);
    }

    @Override
    public void trackViewedPrayerTimes() {
        logEvent(SCREEN_PRAYER_TIMES);
    }

    @Override
    public void trackViewedQibla() {
        logEvent(SCREEN_QIBLA);
    }

    @Override
    public void trackViewedMosqueList() {
        logEvent(SCREEN_MOSQUE_LIST);
    }

    private void logEvent(String screen) {
        if (mLogger == null) return;

        Bundle parameters = new Bundle();
        parameters.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, screen);
        mLogger.logEvent(AppEventsConstants.EVENT_NAME_VIEWED_CONTENT, parameters);
    }

    @Override
    public void startActivity(Activity activity) {
        mLogger = AppEventsLogger.newLogger(activity);
    }

    @Override
    public void stopActivity(Activity activity) {
        mLogger = null;
    }
}
