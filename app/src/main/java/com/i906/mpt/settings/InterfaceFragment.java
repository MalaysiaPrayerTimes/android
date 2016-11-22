package com.i906.mpt.settings;

import android.os.Bundle;

import com.i906.mpt.R;
import com.i906.mpt.analytics.AnalyticsProvider;
import com.i906.mpt.internal.Dagger;

/**
 * @author Noorzaini Ilhami
 */
public class InterfaceFragment extends BasePreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_interface);
        SettingsActivity.bindPreferenceSummaryToValue(findPreference("prayer_highlight"));
        SettingsActivity.bindPreferenceSummaryToValue(findPreference("ui_theme"));

        Dagger.getGraph(getActivity())
                .getAnalyticsProvider()
                .trackViewedScreen(AnalyticsProvider.SCREEN_SETTINGS_INTERFACE);
    }
}
