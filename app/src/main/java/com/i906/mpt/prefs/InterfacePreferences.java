package com.i906.mpt.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.i906.mpt.prayer.PrayerContext;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Noorzaini Ilhami
 */
@Singleton
public class InterfacePreferences implements PrayerContext.ViewSettings {

    private final SharedPreferences mPrefs;

    @Inject
    public InterfacePreferences(Context context) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public boolean isCurrentPrayerHighlightMode() {
        return mPrefs.getString("prayer_highlight", "current").equals("current");
    }

    @Override
    public boolean isDhuhaEnabled() {
        return mPrefs.getBoolean("show_dhuha", true);
    }

    @Override
    public boolean isImsakEnabled() {
        return mPrefs.getBoolean("show_imsak", true);
    }

    @Override
    public boolean isHijriDateEnabled() {
        return mPrefs.getBoolean("show_hijri", true);
    }

    @Override
    public boolean isMasihiDateEnabled() {
        return mPrefs.getBoolean("show_masihi", false);
    }
}
