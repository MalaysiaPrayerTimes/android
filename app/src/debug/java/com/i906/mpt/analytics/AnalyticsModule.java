package com.i906.mpt.analytics;

import android.app.Activity;
import android.app.Application;

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
        return new MockAnalytics();
    }

    public static class MockAnalytics implements AnalyticsProvider {

        public int trackPrayerTimesCount = 0;
        public int trackQiblaCount = 0;
        public int trackMosqueListCount = 0;

        @Override
        public void initialize(Application application) {
        }

        @Override
        public void trackViewedScreen(String screen) {
            Timber.v("Viewing %s", screen);
        }

        @Override
        public void trackViewedPrayerTimes() {
            trackPrayerTimesCount = trackPrayerTimesCount + 1;
            Timber.v("Viewing prayer times, count %s", trackPrayerTimesCount);
        }

        @Override
        public void trackViewedQibla() {
            trackQiblaCount++;
            Timber.v("Viewing prayer qibla, count %s", trackQiblaCount);
        }

        @Override
        public void trackViewedMosqueList() {
            trackMosqueListCount++;
            Timber.v("Viewing mosque list, count %s", trackMosqueListCount);
        }

        @Override
        public void startActivity(Activity activity) {
            Timber.v("Start activity %s", activity);
        }

        @Override
        public void stopActivity(Activity activity) {
            Timber.v("Stop activity %s", activity);
        }
    }
}
