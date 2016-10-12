package com.i906.mpt.prayer;

import com.i906.mpt.date.DateTimeHelper;

import java.util.Date;

/**
 * @author Noorzaini Ilhami
 */
class PrayerImpl implements Prayer {

    private final DateTimeHelper mDateHelper;
    private final int mIndex;
    private final Date mDate;

    PrayerImpl(DateTimeHelper helper, int index, Date date) {
        mDateHelper = helper;
        mIndex = index;
        mDate = date;
    }

    @Override
    public int getIndex() {
        return mIndex;
    }

    @Override
    public Date getDate() {
        return mDate;
    }

    @Override
    public String toString() {
        return "PrayerImpl{" +
                "mIndex=" + mIndex +
                ", mDate=" + mDate +
                '}';
    }
}
