package com.i906.mpt.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.i906.mpt.location.PreferredLocation;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Noorzaini Ilhami
 */
@Singleton
public class LocationPreferences {

    private final SharedPreferences mPrefs;

    @Inject
    public LocationPreferences(Context context) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean isUsingAutomaticLocation() {
        return mPrefs.getBoolean("location_automatic", true);
    }

    public boolean hasPreferredLocation() {
        return getPreferredLocation() != null;
    }

    @Nullable
    public PreferredLocation getPreferredLocation() {
        String code = mPrefs.getString("location_manual_code", null);
        String name = mPrefs.getString("location_manual_name", null);

        if (code != null && name != null) {
            return new PreferredLocation(code, name);
        }

        return null;
    }

    public void convertLegacyPreferences() {
        boolean auto = mPrefs.getBoolean("automatic_location", true);
        String code = mPrefs.getString("manual_location", null);
        String name = mPrefs.getString("manual_location_name", null);

        mPrefs.edit()
                .remove("automatic_location")
                .remove("manual_location")
                .remove("manual_location_name")
                .putBoolean("location_automatic", auto)
                .putString("location_manual_code", code)
                .putString("location_manual_name", name)
                .apply();
    }
}
