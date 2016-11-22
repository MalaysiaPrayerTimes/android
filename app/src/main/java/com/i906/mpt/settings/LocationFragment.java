package com.i906.mpt.settings;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.support.annotation.NonNull;
import android.support.v4.content.PermissionChecker;

import com.i906.mpt.R;
import com.i906.mpt.analytics.AnalyticsProvider;
import com.i906.mpt.internal.ActivityModule;
import com.i906.mpt.internal.Dagger;
import com.i906.mpt.prayer.PrayerManager;
import com.i906.mpt.settings.locationpicker.LocationPickerActivity;

import javax.inject.Inject;

/**
 * @author Noorzaini Ilhami
 */
public class LocationFragment extends BasePreferenceFragment {

    private final static int DEFAULT_PERMISSIONS_REQUEST_CODE = 1349;

    @Inject
    AnalyticsProvider mAnalyticsProvider;

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
                CheckBoxPreference auto = (CheckBoxPreference) preference;

                if (!auto.isChecked()) {
                    mPrayerManager.notifyPreferenceChanged();
                    return true;
                }

                boolean change = requestLocationPermissions();

                if (change) {
                    mPrayerManager.notifyPreferenceChanged();
                }

                return change;
            }
        });

        mAnalyticsProvider.trackViewedScreen(AnalyticsProvider.SCREEN_SETTINGS_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == DEFAULT_PERMISSIONS_REQUEST_CODE) {
            CheckBoxPreference auto = (CheckBoxPreference) findPreference("location_automatic");

            int permission = PermissionChecker.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION);

            auto.setChecked(permission == PermissionChecker.PERMISSION_GRANTED);
        }
    }

    protected boolean requestLocationPermissions() {
        int permission = PermissionChecker.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (permission == PermissionChecker.PERMISSION_GRANTED) {
            return true;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[] {
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, DEFAULT_PERMISSIONS_REQUEST_CODE);

            return false;
        }

        return true;
    }
}
