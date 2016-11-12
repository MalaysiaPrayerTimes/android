package com.i906.mpt.analytics;

import android.app.Activity;
import android.app.Application;

/**
 * @author Noorzaini Ilhami
 */
public interface AnalyticsProvider {

    String SCREEN_MOSQUE_LIST = "Mosque List";
    String SCREEN_PRAYER_TIMES = "Prayer Times";
    String SCREEN_QIBLA = "Qibla";

    void initialize(Application application);

    void trackViewedPrayerTimes();

    void trackViewedQibla();

    void trackViewedMosqueList();

    void startActivity(Activity activity);

    void stopActivity(Activity activity);
}
