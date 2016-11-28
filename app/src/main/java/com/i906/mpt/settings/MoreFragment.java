package com.i906.mpt.settings;

import android.os.Bundle;

import com.i906.mpt.R;
import com.i906.mpt.analytics.AnalyticsProvider;
import com.i906.mpt.internal.Dagger;

/**
 * @author Noorzaini Ilhami
 */
public class MoreFragment extends BasePreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_more);
        SettingsActivity.bindPreferenceSummaryToValue(findPreference("hijri_offset"));

        Dagger.getGraph(getActivity())
                .getAnalyticsProvider()
                .trackViewedScreen(AnalyticsProvider.SCREEN_SETTINGS_MORE);
    }
}
