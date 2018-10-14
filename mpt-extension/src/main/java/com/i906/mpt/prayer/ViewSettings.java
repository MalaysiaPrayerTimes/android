package com.i906.mpt.prayer;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.i906.mpt.extension.Columns;

/**
 * @author Noorzaini Ilhami
 */
class ViewSettings implements PrayerContext.ViewSettings, Parcelable {

    private static final int PARCEL_VERSION = 1;

    private boolean currentPrayerHighlightMode;
    private boolean dhuhaEnabled;
    private boolean imsakEnabled;
    private boolean syurukEnabled;
    private boolean hijriEnabled;
    private boolean masihiEnabled;
    private boolean ampmEnabled;

    ViewSettings(Cursor c) {
        currentPrayerHighlightMode = getSettings(c, Columns.CURRENT_PRAYER_HIGHLIGHT_MODE);
        dhuhaEnabled = getSettings(c, Columns.DHUHA_ENABLED);
        imsakEnabled = getSettings(c, Columns.IMSAK_ENABLED);
        syurukEnabled = getSettings(c, Columns.SYURUK_ENABLED);
        hijriEnabled = getSettings(c, Columns.HIJRI_ENABLED);
        masihiEnabled = getSettings(c, Columns.MASIHI_ENABLED);
        ampmEnabled = getSettings(c, Columns.AM_PM_ENABLED);
    }

    ViewSettings(Parcel in) {
        int version = in.readInt();

        if (version >= 1) {
            currentPrayerHighlightMode = in.readInt() == 1;
            dhuhaEnabled = in.readInt() == 1;
            imsakEnabled = in.readInt() == 1;
            syurukEnabled = in.readInt() == 1;
            hijriEnabled = in.readInt() == 1;
            masihiEnabled = in.readInt() == 1;
            ampmEnabled = in.readInt() == 1;
        }
    }

    @Override
    public boolean isCurrentPrayerHighlightMode() {
        return currentPrayerHighlightMode;
    }

    @Override
    public boolean isDhuhaEnabled() {
        return dhuhaEnabled;
    }

    @Override
    public boolean isImsakEnabled() {
        return imsakEnabled;
    }

    @Override
    public boolean isSyurukEnabled() {
        return syurukEnabled;
    }

    @Override
    public boolean isHijriDateEnabled() {
        return hijriEnabled;
    }

    @Override
    public boolean isMasihiDateEnabled() {
        return masihiEnabled;
    }

    @Override
    public boolean isAmPmEnabled() {
        return ampmEnabled;
    }

    private boolean getSettings(Cursor c, String column) {
        return c.getInt(c.getColumnIndex(column)) == 1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(PARCEL_VERSION);
        dest.writeInt(currentPrayerHighlightMode ? 1 : 0);
        dest.writeInt(dhuhaEnabled ? 1 : 0);
        dest.writeInt(imsakEnabled ? 1 : 0);
        dest.writeInt(syurukEnabled ? 1 : 0);
        dest.writeInt(hijriEnabled ? 1 : 0);
        dest.writeInt(masihiEnabled ? 1 : 0);
        dest.writeInt(ampmEnabled ? 1 : 0);
    }

    public static final Creator<ViewSettings> CREATOR = new Creator<ViewSettings>() {
        @Override
        public ViewSettings createFromParcel(Parcel in) {
            return new ViewSettings(in);
        }

        @Override
        public ViewSettings[] newArray(int size) {
            return new ViewSettings[size];
        }
    };
}
