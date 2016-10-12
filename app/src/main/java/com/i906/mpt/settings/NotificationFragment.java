package com.i906.mpt.settings;

import android.os.Bundle;

import com.i906.mpt.R;

/**
 * @author Noorzaini Ilhami
 */
public class NotificationFragment extends BasePreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_notifications);
        SettingsActivity.bindPreferenceSummaryToValue(findPreference("notification_appear_before_duration"));
        SettingsActivity.bindPreferenceSummaryToValue(findPreference("notification_clear_after_duration"));
        SettingsActivity.bindPreferenceSummaryToValue(findPreference("notification_alarm_offset"));

    }
}
