package com.i906.mpt.prefs;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Noorzaini Ilhami
 */
@Singleton
public class CommonPreferences {

    private final static String KEY_IS_FIRST_START = "is_first_start";

    private final SharedPreferences mPrefs;

    @Inject
    public CommonPreferences(Application context) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean isFirstStart() {
        return mPrefs.getBoolean(KEY_IS_FIRST_START, true);
    }

    public void setFirstStart(boolean firstStart) {
        mPrefs.edit()
                .putBoolean(KEY_IS_FIRST_START, firstStart)
                .apply();
    }

    public int getHijriOffset() {
        return Integer.valueOf(mPrefs.getString("hijri_offset", "0"));
    }

    public void convertLegacyPreferences() {
        boolean al = mPrefs.getBoolean("automatic_location", true);

        mPrefs.edit()
                .remove("automatic_location")
                .putBoolean("location_automatic", al)
                .apply();
    }
}
