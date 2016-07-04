package com.i906.mpt.date;

import java.util.Calendar;
import java.util.TimeZone;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Noorzaini Ilhami
 */
@Singleton
class DateProvider {

    private final Calendar mCalendar;

    @Inject
    public DateProvider() {
        mCalendar = getCalendarInstance();
    }

    private void refresh() {
        mCalendar.setTimeInMillis(getCurrentTime());
    }

    public long getCurrentTime() {
        return System.currentTimeMillis();
    }

    public Calendar getNow() {
        refresh();
        return mCalendar;
    }

    public Calendar getCalendarInstance() {
        return Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
    }
}
