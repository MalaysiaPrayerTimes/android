package com.i906.mpt.analytics;

import android.app.Activity;
import android.app.Application;

import java.util.Arrays;
import java.util.List;

/**
 * @author Noorzaini Ilhami
 */
class CombinedAnalyticsProvider implements AnalyticsProvider {

    private final List<AnalyticsProvider> mProviders;

    CombinedAnalyticsProvider(FabricProvider fa,
                              FacebookProvider fb,
                              FirebaseProvider fi,
                              GoogleProvider g) {
        mProviders = Arrays.asList(fa, fb, fi, g);
    }

    @Override
    public void initialize(Application application) {
        for (AnalyticsProvider p : mProviders) {
            p.initialize(application);
        }
    }

    @Override
    public void trackViewedScreen(String screen) {
        for (AnalyticsProvider p : mProviders) {
            p.trackViewedScreen(screen);
        }
    }

    @Override
    public void trackViewedPrayerTimes() {
        for (AnalyticsProvider p : mProviders) {
            p.trackViewedPrayerTimes();
        }
    }

    @Override
    public void trackViewedQibla() {
        for (AnalyticsProvider p : mProviders) {
            p.trackViewedQibla();
        }
    }

    @Override
    public void trackViewedMosqueList() {
        for (AnalyticsProvider p : mProviders) {
            p.trackViewedMosqueList();
        }
    }

    @Override
    public void startActivity(Activity activity) {
        for (AnalyticsProvider p : mProviders) {
            p.startActivity(activity);
        }
    }

    @Override
    public void stopActivity(Activity activity) {
        for (AnalyticsProvider p : mProviders) {
            p.stopActivity(activity);
        }
    }
}
