package com.i906.mpt.prayer;

import com.i906.mpt.api.prayer.PrayerData;
import com.i906.mpt.date.DateTimeHelper;
import com.i906.mpt.prefs.InterfacePreferences;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.i906.mpt.prayer.Prayer.PRAYER_MAGRHIB;
import static com.i906.mpt.prayer.Prayer.PRAYER_SUBUH;

/**
 * @author Noorzaini Ilhami
 */
class PrayerContextImpl implements PrayerContext {

    private final DateTimeHelper mDateHelper;
    private final InterfacePreferences mPreferences;
    private final PrayerData mCurrentPrayer;
    private final PrayerData mNextPrayer;

    PrayerContextImpl(DateTimeHelper date, InterfacePreferences pref, PrayerData current, PrayerData next) {
        mDateHelper = date;
        mPreferences = pref;
        mCurrentPrayer = current;
        mNextPrayer = next;
    }

    @Override
    public Prayer getCurrentPrayer() {
        int index = getCurrentPrayerIndex();
        Date date = getCurrentPrayerTime();
        return new PrayerImpl(mDateHelper, index, date);
    }

    @Override
    public Prayer getNextPrayer() {
        int index = getNextPrayerIndex();
        Date date = getNextPrayerTime();
        return new PrayerImpl(mDateHelper, index, date);
    }

    @Override
    public List<Prayer> getCurrentPrayerList() {
        List<Prayer> list = new ArrayList<>();
        List<Date> dates = getCurrentPrayerTimeList();

        for (int i = 0; i < 8; i++) {
            list.add(new PrayerImpl(mDateHelper, i, dates.get(i)));
        }

        return list;
    }

    @Override
    public List<Prayer> getNextPrayerList() {
        List<Prayer> list = new ArrayList<>();
        List<Date> dates = getNextDayPrayerTimes();

        for (int i = 0; i < 8; i++) {
            list.add(new PrayerImpl(mDateHelper, i, dates.get(i)));
        }

        return list;
    }

    @Override
    public String getLocationName() {
        return mCurrentPrayer.getLocation();
    }

    private int getCurrentPrayerIndex() {
        int pos = 0;

        for (int i = 0; i < 8; i++) {
            if (!prayerHasPassed(i)) break;
            pos++;
        }

        return (pos - 1 == -1) ? 7 : pos - 1;
    }

    @Override
    public List<Integer> getHijriDate() {
        return mDateHelper.getHijriDate(hasMaghribPassed());
    }

    private int getNextPrayerIndex() {
        int cpi = getCurrentPrayerIndex();
        if (cpi == -1) return -1;
        return (cpi + 1) % 8;
    }

    @Override
    public ViewSettings getViewSettings() {
        return mPreferences;
    }

    private Date getCurrentPrayerTime() {
        return getCurrentPrayerTime(getCurrentPrayerIndex());
    }

    private Date getCurrentPrayerTime(int index) {
        return getCurrentPrayerTimeList().get(index);
    }

    private List<Date> getCurrentPrayerTimeList() {
        return mCurrentPrayer.getPrayerTimes().get(mDateHelper.getCurrentDate() - 1);
    }

    private Date getNextPrayerTime() {
        int index = getNextPrayerIndex();

        if (index == 0 && hasSubuhPassed()) {
            return getNextDayPrayerTime(index);
        } else {
            return getCurrentPrayerTime(index);
        }
    }

    private Date getNextDayPrayerTime(int index) {
        return getNextDayPrayerTimes().get(index);
    }

    private List<Date> getNextDayPrayerTimes() {
        if (!mDateHelper.isTommorowNewMonth()) {
            return mCurrentPrayer.getPrayerTimes().get(mDateHelper.getNextDate() - 1);
        } else {
            return mNextPrayer.getPrayerTimes().get(0);
        }
    }

    private boolean hasSubuhPassed() {
        return prayerHasPassed(PRAYER_SUBUH);
    }

    private boolean hasMaghribPassed() {
        return prayerHasPassed(PRAYER_MAGRHIB);
    }

    private boolean prayerHasPassed(int prayer) {
        Calendar n = mDateHelper.getNow();
        Calendar s = mDateHelper.getCalendarInstance();
        List<Date> cdpt = getCurrentPrayerTimeList();

        s.setTime(cdpt.get(prayer));
        return n.after(s);
    }

    PrayerData getCurrentPrayerData() {
        return mCurrentPrayer;
    }

    PrayerData getNextPrayerData() {
        return mNextPrayer;
    }

    @Override
    public String toString() {
        return "PrayerContextImpl{" +
                "current=" + mCurrentPrayer +
                ", next=" + mNextPrayer +
                '}';
    }
}
