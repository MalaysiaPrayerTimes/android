package com.i906.mpt.date;

import java.util.Calendar;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DateTimeHelper {

    private final DateProvider mProvider;
    private Calendar mCalendar;

    @Inject
    public DateTimeHelper(DateProvider provider) {
        mProvider = provider;
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

    private void refresh() {
        mCalendar = mProvider.getNow();
    }

    public Calendar getNow() {
        return mProvider.getNow();
    }

    public Calendar getCalendarInstance() {
        return mProvider.getCalendarInstance();
    }
}
