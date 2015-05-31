package com.i906.mpt.util;

import java.util.Calendar;
import java.util.TimeZone;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DateTimeHelper {

    protected Calendar mCalendar;

    @Inject
    public DateTimeHelper() {
        refresh();
    }

    public int getCurrentDate() {
        refresh();
        return mCalendar.get(Calendar.DATE);
    }

    public int getNextDate() {
        Calendar c = getNewCalendarInstance();
        c.add(Calendar.DATE, 1);
        return c.get(Calendar.DATE);
    }

    public int getCurrentMonth() {
        refresh();
        return mCalendar.get(Calendar.MONTH);
    }

    public int getNextMonth() {
        Calendar c = getNewCalendarInstance();
        c.add(Calendar.MONTH, 1);
        return c.get(Calendar.MONTH);
    }

    public int getCurrentYear() {
        refresh();
        return mCalendar.get(Calendar.YEAR);
    }

    public int getNextYear() {
        Calendar c = getNewCalendarInstance();
        c.add(Calendar.YEAR, 1);
        return c.get(Calendar.YEAR);
    }

    public boolean isTommorowNewMonth() {
        Calendar tom = getNewCalendarInstance();
        tom.add(Calendar.DATE, 1);

        int tommonth = tom.get(Calendar.MONTH);
        return getCurrentMonth() != tommonth;
    }

    public boolean isNextMonthNewYear() {
        Calendar tom = getNewCalendarInstance();
        tom.add(Calendar.MONTH, 1);

        int tomyear = tom.get(Calendar.YEAR);
        return getCurrentYear() != tomyear;
    }

    private void refresh() {
        mCalendar = getNewCalendarInstance();
    }

    public Calendar getNewCalendarInstance() {
        return Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
    }
}
