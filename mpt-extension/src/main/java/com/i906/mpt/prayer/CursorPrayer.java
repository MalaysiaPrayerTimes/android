package com.i906.mpt.prayer;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * @author Noorzaini Ilhami
 */
class CursorPrayer implements Prayer, Parcelable {

    private static final int PARCEL_VERSION = 1;

    private int index;
    private Date date;

    CursorPrayer(int index, Date date) {
        this.index = index;
        this.date = date;
    }

    CursorPrayer(Parcel in) {
        int version = in.readInt();

        if (version == 1) {
            index = in.readInt();
            date = new Date(in.readLong());
        }
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(PARCEL_VERSION);
        parcel.writeInt(index);
        parcel.writeLong(date.getTime());
    }

    public static final Creator<CursorPrayer> CREATOR = new Creator<CursorPrayer>() {
        @Override
        public CursorPrayer createFromParcel(Parcel in) {
            return new CursorPrayer(in);
        }

        @Override
        public CursorPrayer[] newArray(int size) {
            return new CursorPrayer[size];
        }
    };
}
