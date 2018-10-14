package com.i906.mpt.extension;

import com.i906.mpt.prayer.Prayer;

/**
 * @author Noorzaini Ilhami
 */
public interface Columns {

    String PRAYER_PREFIX = "prayer_";
    String HIJRI_DATE_PREFIX = "hijri_";
    String VIEW_SETTINGS_PREFIX = "viewsettings_";

    String LOCATION = "location";
    String CURRENT_PRAYER_TIME = "current_prayer_time";
    String CURRENT_PRAYER_INDEX = "current_prayer_index";
    String NEXT_PRAYER_TIME = "next_prayer_time";
    String NEXT_PRAYER_INDEX = "next_prayer_index";
    String PRAYER_IMSAK = PRAYER_PREFIX + Prayer.PRAYER_IMSAK;
    String PRAYER_SUBUH = PRAYER_PREFIX + Prayer.PRAYER_SUBUH;
    String PRAYER_SYURUK = PRAYER_PREFIX + Prayer.PRAYER_SYURUK;
    String PRAYER_DHUHA = PRAYER_PREFIX + Prayer.PRAYER_DHUHA;
    String PRAYER_ZOHOR = PRAYER_PREFIX + Prayer.PRAYER_ZOHOR;
    String PRAYER_ASAR = PRAYER_PREFIX + Prayer.PRAYER_ASAR;
    String PRAYER_MAGRHIB = PRAYER_PREFIX + Prayer.PRAYER_MAGRHIB;
    String PRAYER_ISYAK = PRAYER_PREFIX + Prayer.PRAYER_ISYAK;
    String HIJRI_DAY = HIJRI_DATE_PREFIX + Prayer.HIJRI_DATE;
    String HIJRI_MONTH = HIJRI_DATE_PREFIX + Prayer.HIJRI_MONTH;
    String HIJRI_YEAR = HIJRI_DATE_PREFIX + Prayer.HIJRI_YEAR;

    String CURRENT_PRAYER_HIGHLIGHT_MODE = VIEW_SETTINGS_PREFIX + "current_prayer_highlight";
    String DHUHA_ENABLED = VIEW_SETTINGS_PREFIX + "show_dhuha";
    String IMSAK_ENABLED = VIEW_SETTINGS_PREFIX + "show_imsak";
    String SYURUK_ENABLED = VIEW_SETTINGS_PREFIX + "show_syuruk";
    String HIJRI_ENABLED = VIEW_SETTINGS_PREFIX + "show_hijri";
    String MASIHI_ENABLED = VIEW_SETTINGS_PREFIX + "show_masihi";
    String AM_PM_ENABLED = VIEW_SETTINGS_PREFIX + "show_ampm";
}
