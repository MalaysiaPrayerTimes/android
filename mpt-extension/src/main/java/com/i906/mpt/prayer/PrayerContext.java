package com.i906.mpt.prayer;

import android.database.Cursor;

import java.util.List;

/**
 * @author Noorzaini Ilhami
 */
public interface PrayerContext {

    Prayer getCurrentPrayer();

    Prayer getNextPrayer();

    List<Prayer> getCurrentPrayerList();

    List<Prayer> getNextPrayerList();

    String getLocationName();

    List<Integer> getHijriDate();

    ViewSettings getViewSettings();

    interface ViewSettings {
        boolean isCurrentPrayerHighlightMode();

        boolean isDhuhaEnabled();

        boolean isImsakEnabled();

        boolean isSyurukEnabled();

        boolean isHijriDateEnabled();

        boolean isMasihiDateEnabled();

        boolean isAmPmEnabled();
    }

    final class Mapper {
        public static PrayerContext fromCursor(Cursor c) {
            return new CursorPrayerContext(c);
        }
    }
}
