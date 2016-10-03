package com.i906.mpt.prayer;

import java.util.Date;

/**
 * @author Noorzaini Ilhami
 */
class CursorPrayer implements Prayer {

    private int index;
    private Date date;

    CursorPrayer(int index, Date date) {
        this.index = index;
        this.date = date;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "CursorPrayer{" +
                "index=" + index +
                ", date=" + date +
                '}';
    }
}
