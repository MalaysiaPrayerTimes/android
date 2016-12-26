package com.i906.mpt.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.i906.mpt.R;
import com.i906.mpt.analytics.AnalyticsProvider;
import com.i906.mpt.internal.Dagger;

/**
 * @author Noorzaini Ilhami
 */
public class HiddenSettingsActivity extends AppCompatPreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_hidden);

        SettingsActivity.bindPreferenceSummaryToValue(findPreference("location_request_timeout"));
        SettingsActivity.bindPreferenceSummaryToValue(findPreference("location_cache_duration"));
        SettingsActivity.bindPreferenceSummaryToValue(findPreference("location_fastest_interval"));
        SettingsActivity.bindPreferenceSummaryToValue(findPreference("location_interval"));
        SettingsActivity.bindPreferenceSummaryToValue(findPreference("location_distance_limit"));

        SettingsActivity.bindPreferenceSummaryToValue(findPreference("foursquare_intent"));
        SettingsActivity.bindPreferenceSummaryToValue(findPreference("foursquare_query"));

        Dagger.getGraph(this)
                .getAnalyticsProvider()
                .trackViewedScreen(AnalyticsProvider.SCREEN_SETTINGS_ADVANCED);
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, HiddenSettingsActivity.class);
        context.startActivity(intent);
    }
}
