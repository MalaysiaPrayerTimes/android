package com.i906.mpt.settings;

import android.os.Bundle;
import android.preference.Preference;

import com.i906.mpt.R;
import com.i906.mpt.analytics.AnalyticsProvider;
import com.i906.mpt.internal.Dagger;
import com.i906.mpt.widget.WidgetService;

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

        Preference widgetBackground = findPreference("widget_background_color");
        Preference widgetImsak = findPreference("widget_show_imsak");
        Preference widgetDhuha = findPreference("widget_show_dhuha");
        Preference widgetMasihi = findPreference("widget_show_masihi");
        Preference widgetHijri = findPreference("widget_show_hijri");

        Preference.OnPreferenceChangeListener widgetChange = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                WidgetService.start(getActivity());
                return true;
            }
        };

        widgetBackground.setOnPreferenceChangeListener(widgetChange);
        widgetImsak.setOnPreferenceChangeListener(widgetChange);
        widgetDhuha.setOnPreferenceChangeListener(widgetChange);
        widgetMasihi.setOnPreferenceChangeListener(widgetChange);
        widgetHijri.setOnPreferenceChangeListener(widgetChange);

        Dagger.getGraph(getActivity())
                .getAnalyticsProvider()
                .trackViewedScreen(AnalyticsProvider.SCREEN_SETTINGS_INTERFACE);
    }
}
