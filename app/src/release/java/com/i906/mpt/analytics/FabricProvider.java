package com.i906.mpt.analytics;

import android.app.Activity;
import android.app.Application;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Noorzaini Ilhami
 */
@Singleton
class FabricProvider implements AnalyticsProvider {

    @Inject
    FabricProvider() {
    }

    @Override
    public void initialize(Application application) {
    }

    @Override
    public void trackViewedScreen(String screen) {
        logContentView(screen);
    }

    @Override
    public void trackViewedPrayerTimes() {
        logContentView(SCREEN_PRAYER_TIMES);
    }

    @Override
    public void trackViewedQibla() {
        logContentView(SCREEN_QIBLA);
    }

    @Override
    public void trackViewedMosqueList() {
        logContentView(SCREEN_MOSQUE_LIST);
    }

    private void logContentView(String screen) {
        Answers.getInstance()
                .logContentView(new ContentViewEvent()
                        .putContentName(screen)
                );
    }

    @Override
    public void startActivity(Activity activity) {
    }

    @Override
    public void stopActivity(Activity activity) {
    }
}
