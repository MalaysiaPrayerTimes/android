package com.i906.mpt.settings.locationpicker;

import com.i906.mpt.api.prayer.PrayerCode;

import java.util.List;

/**
 * @author Noorzaini Ilhami
 */
interface CodeView {
    void showCodeList(List<PrayerCode> codeList);
    void showError(Throwable error);
    void showLoading();
}
