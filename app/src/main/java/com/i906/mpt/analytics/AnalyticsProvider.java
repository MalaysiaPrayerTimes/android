package com.i906.mpt.analytics;

import android.app.Activity;
import android.app.Application;

/**
 * @author Noorzaini Ilhami
 */
public interface AnalyticsProvider {

    String SCREEN_ABOUT = "About";
    String SCREEN_CHANGELOG = "Copyright";
    String SCREEN_COPYRIGHT = "Copyright";
    String SCREEN_DONATION = "Donation";
    String SCREEN_MOSQUE_LIST = "Mosque List";
    String SCREEN_OPEN_SOURCE = "Open Source";
    String SCREEN_PRAYER_TIMES = "Prayer Times";
    String SCREEN_QIBLA = "Qibla";
    String SCREEN_SETTINGS = "Settings";
    String SCREEN_SETTINGS_CONFIGURE_NOTIFICATIONS = "Settings - Configure Notifications";
    String SCREEN_SETTINGS_INTERFACE = "Settings - Interface";
    String SCREEN_SETTINGS_LOCATION = "Settings - Location";
    String SCREEN_SETTINGS_MORE = "Settings - More";
    String SCREEN_SETTINGS_NOTIFICATIONS = "Settings - Notifications";

    void initialize(Application application);

    void trackViewedScreen(String screen);

    void trackViewedPrayerTimes();

    void trackViewedQibla();

    void trackViewedMosqueList();

    void startActivity(Activity activity);

    void stopActivity(Activity activity);
}
