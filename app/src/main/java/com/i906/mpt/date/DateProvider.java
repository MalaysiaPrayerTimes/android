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

    private Calendar mCalendar;

    @Inject
    DateProvider() {
    }

    private void refresh() {
        mCalendar = getCalendarInstance();
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
