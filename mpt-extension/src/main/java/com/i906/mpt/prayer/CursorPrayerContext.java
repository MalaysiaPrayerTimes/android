package com.i906.mpt.prayer;

import android.database.Cursor;

import com.i906.mpt.extension.Columns;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Noorzaini Ilhami
 */
class CursorPrayerContext implements PrayerContext {

    private String location;
    private Prayer currentPrayer;
    private Prayer nextPrayer;

    private List<Prayer> currentPrayerList;
    private List<Integer> hijriDates;

    CursorPrayerContext(Cursor c) {
        long cpt = c.getLong(c.getColumnIndex(Columns.CURRENT_PRAYER_TIME));
        long npt = c.getLong(c.getColumnIndex(Columns.NEXT_PRAYER_TIME));
        int cpi = c.getInt(c.getColumnIndex(Columns.CURRENT_PRAYER_INDEX));
        int npi = c.getInt(c.getColumnIndex(Columns.NEXT_PRAYER_INDEX));

        currentPrayer = new CursorPrayer(cpi, new Date(cpt));
        nextPrayer = new CursorPrayer(npi, new Date(npt));

        location = c.getString(c.getColumnIndex(Columns.LOCATION));

        currentPrayerList = new ArrayList<>(8);
        hijriDates = new ArrayList<>(3);

        for (int i = 0; i < 8; i++) {
            long t = c.getLong(c.getColumnIndex(Columns.PRAYER_PREFIX + i));
            Date d = new Date(t);
            currentPrayerList.add(new CursorPrayer(i, d));
        }

        for (int i = 0; i < 3; i++) {
            int t = c.getInt(c.getColumnIndex(Columns.HIJRI_DATE_PREFIX + i));
            hijriDates.add(t);
        }
    }

    @Override
    public Prayer getCurrentPrayer() {
        return currentPrayer;
    }

    @Override
    public Prayer getNextPrayer() {
        return nextPrayer;
    }

    @Override
    public List<Prayer> getCurrentPrayerList() {
        return currentPrayerList;
    }

    @Override
    public List<Prayer> getNextPrayerList() {
        return null;
    }

    @Override
    public String getLocationName() {
        return location;
    }

    @Override
    public List<Integer> getHijriDate() {
        return hijriDates;
    }

    @Override
    public ViewSettings getViewSettings() {
        return null;
    }
}
