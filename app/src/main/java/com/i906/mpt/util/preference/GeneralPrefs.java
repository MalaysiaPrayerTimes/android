package com.i906.mpt.util.preference;

import android.content.Context;

import com.i906.mpt.view.DefaultPrayerView;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class GeneralPrefs extends Prefs {

    private static final String PREFIX = "general_";

    @Inject
    public GeneralPrefs(Context context) {
        super(context);
    }

    @Override
    protected String getPrefix() {
        return PREFIX;
    }

    public String getSelectedPrayerView() {
        return getString("selected_prayer_view", DefaultPrayerView.class.getCanonicalName());
    }

    public void setSelectedPrayerView(String view) {
        setString("selected_prayer_view", view);
    }

    public void resetSelectedPrayerView() {
        setSelectedPrayerView(DefaultPrayerView.class.getCanonicalName());
    }

    public boolean is3DCompassEnabled() {
        return getBoolean("3d_compass", false);
    }

    public int getHijriOffset() {
        return Integer.valueOf(getString("hijri_offset", "0"));
    }
}
