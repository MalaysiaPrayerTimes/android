package com.i906.mpt.settings;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;

import com.i906.mpt.R;
import com.i906.mpt.analytics.AnalyticsProvider;
import com.i906.mpt.internal.Dagger;

/**
 * @author Noorzaini Ilhami
 */
public class NotificationFragment extends BasePreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_notifications);

        Preference notification = findPreference("notification_configure");

        notification.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(getActivity(), NotificationActivity.class));
                return true;
            }
        });

        SettingsActivity.bindPreferenceSummaryToValue(findPreference("notification_appear_before_duration"));
        SettingsActivity.bindPreferenceSummaryToValue(findPreference("notification_clear_after_duration"));
        SettingsActivity.bindPreferenceSummaryToValue(findPreference("notification_alarm_offset"));

        Dagger.getGraph(getActivity())
                .getAnalyticsProvider()
                .trackViewedScreen(AnalyticsProvider.SCREEN_SETTINGS_NOTIFICATIONS);
    }
}
