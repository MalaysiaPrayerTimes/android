package com.i906.mpt.util;

import com.i906.mpt.util.preference.GeneralPrefs;

import org.joda.time.DateTime;
import org.joda.time.chrono.IslamicChronology;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DateTimeHelper {

    private Calendar mCalendar;
    private DateTime mHijriCalendar;
    private GeneralPrefs mPrefs;

    @Inject
    public DateTimeHelper(GeneralPrefs p) {
        refresh();
        mPrefs = p;
    }

    public int getCurrentDate() {
        refresh();
        return mCalendar.get(Calendar.DATE);
    }

    public int getNextDate() {
        Calendar c = getNow();
        c.add(Calendar.DATE, 1);
        return c.get(Calendar.DATE);
    }

    public int getCurrentMonth() {
        refresh();
        return mCalendar.get(Calendar.MONTH);
    }

    public int getNextMonth() {
        Calendar c = getNow();
        c.add(Calendar.MONTH, 1);
        return c.get(Calendar.MONTH);
    }

    public int getCurrentYear() {
        refresh();
        return mCalendar.get(Calendar.YEAR);
    }

    public int getNextYear() {
        Calendar c = getNow();
        c.add(Calendar.YEAR, 1);
        return c.get(Calendar.YEAR);
    }

    public boolean isTommorowNewMonth() {
        Calendar tom = getNow();
        tom.add(Calendar.DATE, 1);

        int tommonth = tom.get(Calendar.MONTH);
        return getCurrentMonth() != tommonth;
    }

    public boolean isNextMonthNewYear() {
        Calendar tom = getNow();
        tom.add(Calendar.MONTH, 1);

        int tomyear = tom.get(Calendar.YEAR);
        return getCurrentYear() != tomyear;
    }

    private void refresh() {
        if (mCalendar == null) mCalendar = getNewCalendarInstance();
        mCalendar.setTimeInMillis(getCurrentTime());
        mHijriCalendar = new DateTime(IslamicChronology.getInstance());
    }

    public long getCurrentTime() {
        return System.currentTimeMillis();
    }

    public Calendar getNow() {
        refresh();
        return mCalendar;
    }

    public Calendar getNewCalendarInstance() {
        return Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
    }

    public List<Integer> getHijriDate(boolean maghribPassed) {
        List<Integer> l = new ArrayList<>(3);
        refresh();

        if (maghribPassed) mHijriCalendar = mHijriCalendar.plusDays(1);
        mHijriCalendar = mHijriCalendar.plusDays(mPrefs.getHijriOffset());
        l.add(mHijriCalendar.getDayOfMonth());
        l.add(mHijriCalendar.getMonthOfYear() - 1);
        l.add(mHijriCalendar.getYear());

        return l;
    }
}
