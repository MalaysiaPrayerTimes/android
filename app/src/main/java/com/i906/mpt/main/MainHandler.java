package com.i906.mpt.main;

import android.location.Location;

import com.i906.mpt.prayer.PrayerContext;

/**
 * @author Noorzaini Ilhami
 */
interface MainHandler {
    void handlePrayerContext(PrayerContext prayerContext);
    void handleLocation(Location location);
    void handleError(Throwable t);
}
