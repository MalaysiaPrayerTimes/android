package com.i906.mpt.settings;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.provider.Settings;
import android.support.annotation.RequiresApi;

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
        Preference headsup = findPreference("prayer_headsup");

        notification.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showNotificationSettings();
                return true;
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getPreferenceScreen().removePreference(headsup);
        }

        SettingsActivity.bindPreferenceSummaryToValue(findPreference("notification_appear_before_duration"));
        SettingsActivity.bindPreferenceSummaryToValue(findPreference("notification_clear_after_duration"));
        SettingsActivity.bindPreferenceSummaryToValue(findPreference("notification_alarm_offset"));

        Dagger.getGraph(getActivity())
                .getAnalyticsProvider()
                .trackViewedScreen(AnalyticsProvider.SCREEN_SETTINGS_NOTIFICATIONS);
    }

    private void showNotificationSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            showOreoNotificationSettings();
        } else {
            showLegacyNotificationSettings();
        }
    }

    private void showLegacyNotificationSettings() {
        startActivity(new Intent(getActivity(), NotificationActivity.class));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showOreoNotificationSettings() {
        Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, getActivity().getPackageName());
        startActivity(intent);
    }
}
