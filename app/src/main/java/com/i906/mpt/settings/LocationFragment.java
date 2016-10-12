package com.i906.mpt.settings;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;

import com.i906.mpt.R;
import com.i906.mpt.settings.locationpicker.LocationPickerActivity;

/**
 * @author Noorzaini Ilhami
 */
public class LocationFragment extends BasePreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_location);

        Preference manual = findPreference("location_manual");

        manual.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(getActivity(), LocationPickerActivity.class));
                return true;
            }
        });
    }
}
