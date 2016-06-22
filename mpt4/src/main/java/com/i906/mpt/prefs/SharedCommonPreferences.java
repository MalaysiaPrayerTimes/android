package com.i906.mpt.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * @author Noorzaini Ilhami
 */
public class SharedCommonPreferences implements CommonPreferences {

    private final static String KEY_IS_FIRST_START = "is_first_start";

    private final Context mContext;
    private final SharedPreferences mPrefs;

    public SharedCommonPreferences(Context context) {
        mContext = context;
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public boolean isFirstStart() {
        return mPrefs.getBoolean(KEY_IS_FIRST_START, true);
    }

    @Override
    public void setFirstStart(boolean firstStart) {
        mPrefs.edit()
                .putBoolean(KEY_IS_FIRST_START, firstStart)
                .apply();
    }
}
