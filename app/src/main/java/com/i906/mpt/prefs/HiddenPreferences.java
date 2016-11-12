package com.i906.mpt.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * @author Noorzaini Ilhami
 */
public class HiddenPreferences {

    private final SharedPreferences mPrefs;

    public HiddenPreferences(Context context) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public long getLocationRequestTimeout() {
        return Long.valueOf(mPrefs.getString("location_request_timeout", "15000"));
    }

    public long getLocationCacheDuration() {
        return Long.valueOf(mPrefs.getString("location_cache_duration", "900000"));
    }

    public long getLocationFastestInterval() {
        return Long.valueOf(mPrefs.getString("location_fastest_interval", "60000"));
    }

    public long getLocationInterval() {
        return Long.valueOf(mPrefs.getString("location_interval", "900000"));
    }

    public long getLocationDistanceLimit() {
        return Long.valueOf(mPrefs.getString("location_distance_limit", "5000"));
    }

    public boolean isCompassEnabled() {
        return true;
    }
}
