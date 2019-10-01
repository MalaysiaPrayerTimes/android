package com.i906.mpt.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
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
        return Long.valueOf(mPrefs.getString("location_cache_duration", "86400000"));
    }

    public long getLocationFastestInterval() {
        return Long.valueOf(mPrefs.getString("location_fastest_interval", "60000"));
    }

    public long getLocationInterval() {
        return Long.valueOf(mPrefs.getString("location_interval", "900000"));
    }

    public long getLocationDistanceLimit() {
        return Long.valueOf(mPrefs.getString("location_distance_limit", "2500"));
    }

    public boolean isCompassEnabled() {
        return true;
    }

    public Location getLocationCache() {
        long rawLat = mPrefs.getLong("location_cache_lat", -1);
        long rawLng = mPrefs.getLong("location_cache_lng", -1);
        long time = mPrefs.getLong("location_cache_time", -1);

        if (rawLat == -1 || rawLng == -1 || time == -1) {
            return null;
        }

        Location l = new Location("cache");
        l.setLatitude(Double.longBitsToDouble(rawLat));
        l.setLongitude(Double.longBitsToDouble(rawLng));
        l.setTime(time);

        return l;
    }

    public void setLocationCache(Location location) {
        mPrefs.edit()
                .putLong("location_cache_lat", Double.doubleToRawLongBits(location.getLatitude()))
                .putLong("location_cache_lng", Double.doubleToRawLongBits(location.getLongitude()))
                .putLong("location_cache_time", location.getTime())
                .apply();
    }

    public String getFoursquareIntent() {
        return mPrefs.getString("foursquare_intent", "browse");
    }

    public String getFoursquareQuery() {
        return mPrefs.getString("foursquare_query", "surau,masjid");
    }

    public boolean isVisible() {
        return mPrefs.getBoolean("hidden_preferences", false);
    }

    public void setVisible(boolean visible) {
        mPrefs.edit()
                .putBoolean("hidden_preferences", visible)
                .apply();
    }
}
