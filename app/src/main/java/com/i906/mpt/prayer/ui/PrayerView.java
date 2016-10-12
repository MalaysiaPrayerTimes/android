package com.i906.mpt.prayer.ui;

import com.i906.mpt.prayer.PrayerContext;

/**
 * @author Noorzaini Ilhami
 */
interface PrayerView {

    void showPrayerContext(PrayerContext prayerContext);
    void showError(Throwable error);
    void showLoading();
}
