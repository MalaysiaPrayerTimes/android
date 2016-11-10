package com.i906.mpt.analytics;

import android.app.Activity;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import timber.log.Timber;

/**
 * @author Noorzaini Ilhami
 */
@Module
public class AnalyticsModule {

    @Provides
    @Singleton
    AnalyticsProvider provideAnalyticsProvider() {
        return new AnalyticsProvider() {
            @Override
            public void trackViewedPrayerTimes() {
                Timber.v("Viewing prayer times");
            }

            @Override
            public void trackViewedQibla() {
                Timber.v("Viewing prayer qibla");
            }

            @Override
            public void trackViewedMosqueList() {
                Timber.v("Viewing mosque list");
            }

            @Override
            public void startActivity(Activity activity) {
                Timber.v("Start activity %s", activity);
            }

            @Override
            public void stopActivity(Activity activity) {
                Timber.v("Stop activity %s", activity);
            }
        };
    }
}
