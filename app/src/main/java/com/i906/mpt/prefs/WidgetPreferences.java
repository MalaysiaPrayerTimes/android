package com.i906.mpt.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;

import com.i906.mpt.R;

/**
 * @author Noorzaini Ilhami
 */
public class WidgetPreferences {

    private final SharedPreferences mPrefs;
    private final int mDefaultBackgroundColor;

    public WidgetPreferences(Context context) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        mDefaultBackgroundColor = ContextCompat.getColor(context, R.color.widget_background);

    }

    public int getBackgroundColor() {
        return mPrefs.getInt("widget_background_color", mDefaultBackgroundColor);
    }

    public boolean isDhuhaEnabled() {
        return mPrefs.getBoolean("widget_show_dhuha", true);
    }

    public boolean isImsakEnabled() {
        return mPrefs.getBoolean("widget_show_imsak", true);
    }

    public boolean isHijriDateEnabled() {
        return mPrefs.getBoolean("widget_show_hijri", true);
    }

    public boolean isMasihiDateEnabled() {
        return mPrefs.getBoolean("widget_show_masihi", false);
    }
}
