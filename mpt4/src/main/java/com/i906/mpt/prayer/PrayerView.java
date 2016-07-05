package com.i906.mpt.prayer;

/**
 * @author Noorzaini Ilhami
 */
interface PrayerView {

    void showPrayerContext(PrayerContext prayerContext);
    void showError(Throwable error);
    void showLoading();
}
