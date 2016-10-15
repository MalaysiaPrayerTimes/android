package com.i906.mpt.settings;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;

import com.i906.mpt.R;
import com.i906.mpt.internal.ActivityModule;
import com.i906.mpt.internal.Dagger;
import com.i906.mpt.prayer.PrayerManager;
import com.i906.mpt.settings.locationpicker.LocationPickerActivity;

import javax.inject.Inject;

/**
 * @author Noorzaini Ilhami
 */
public class LocationFragment extends BasePreferenceFragment {

    @Inject
    PrayerManager mPrayerManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_location);

        Dagger.getGraph(getActivity())
                .activityGraph(new ActivityModule(getActivity()))
                .inject(this);

        Preference manual = findPreference("location_manual");
        Preference auto = findPreference("location_automatic");

        manual.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(getActivity(), LocationPickerActivity.class));
                return true;
            }
        });

        auto.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                mPrayerManager.notifyPreferenceChanged();
                return true;
            }
        });
    }
}
