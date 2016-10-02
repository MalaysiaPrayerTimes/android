package com.i906.mpt.widget;

import com.i906.mpt.prayer.PrayerContext;

/**
 * @author Noorzaini Ilhami
 */
interface WidgetHandler {

    void handlePrayerContext(PrayerContext prayerContext);
    void handleError(Throwable throwable);
}
