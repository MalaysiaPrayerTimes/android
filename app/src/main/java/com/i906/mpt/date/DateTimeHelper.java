package com.i906.mpt.date;

import com.github.msarhan.ummalqura.calendar.UmmalquraCalendar;
import com.i906.mpt.prefs.CommonPreferences;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DateTimeHelper {

    private final DateProvider mProvider;
    private final CommonPreferences mPrefs;
    private Calendar mCalendar;
    private UmmalquraCalendar mHijriCalendar;

    @Inject
    public DateTimeHelper(DateProvider provider, CommonPreferences prefs) {
        mProvider = provider;
        mPrefs = prefs;
        refresh();
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

    public List<Integer> getHijriDate(boolean maghribPassed) {
        List<Integer> l = new ArrayList<>(3);
        refresh();

        if (maghribPassed) {
            mHijriCalendar.add(Calendar.DATE, 1);
        }

        mHijriCalendar.add(Calendar.DATE, mPrefs.getHijriOffset());
        l.add(mHijriCalendar.get(Calendar.DATE));
        l.add(mHijriCalendar.get(Calendar.MONTH));
        l.add(mHijriCalendar.get(Calendar.YEAR));

        return l;
    }

    private void refresh() {
        mCalendar = mProvider.getNow();
        mHijriCalendar = new UmmalquraCalendar();
        mHijriCalendar.setTimeInMillis(mProvider.getCurrentTime());
    }

    public Calendar getNow() {
        return mProvider.getNow();
    }

    public Calendar getCalendarInstance() {
        return mProvider.getCalendarInstance();
    }

    public boolean isInPast(Date date) {
        if (date != null) {
            Calendar now = getNow();

            Calendar cur = getCalendarInstance();
            cur.setTime(date);

            return now.after(cur);
        }

        return false;
    }
}
