package com.i906.mpt.prayer;

import java.util.Date;

/**
 * @author Noorzaini Ilhami
 */
class PrayerImpl implements Prayer {

    private final int mIndex;
    private final Date mDate;

    PrayerImpl(int index, Date date) {
        mIndex = index;
        mDate = date;
    }

    @Override
    public int getIndex() {
        return mIndex;
    }

    @Override
    public Date getTime() {
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
