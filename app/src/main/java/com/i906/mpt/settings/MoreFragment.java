package com.i906.mpt.settings;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.i906.mpt.R;
import com.i906.mpt.analytics.AnalyticsProvider;
import com.i906.mpt.internal.Dagger;
import com.i906.mpt.widget.DashClockService;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * @author Noorzaini Ilhami
 */
public class MoreFragment extends BasePreferenceFragment implements MoreView {

    @Inject
    MorePresenter mPresenter;

    @BindView(android.R.id.list)
    View mLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_more);
        activityGraph().inject(this);

        Preference advanced = findPreference("advanced_settings");

        if (!Dagger.getGraph(getActivity()).getHiddenPreferences().isVisible()) {
            getPreferenceScreen().removePreference(advanced);
        } else {
            advanced.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    HiddenSettingsActivity.start(getActivity());
                    return true;
                }
            });
        }

        Preference dashclock = findPreference("dashclock_visibility");
        dashclock.setEnabled(isAppInstalled("net.nurik.roman.dashclock"));
        dashclock.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                DashClockService.start(getActivity());
                return true;
            }
        });

        SettingsActivity.bindPreferenceSummaryToValue(dashclock);
        SettingsActivity.bindPreferenceSummaryToValue(findPreference("hijri_offset"));

        Preference clearLocationCache = findPreference("clear_location_cache");
        clearLocationCache.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                clearLocationCache();
                return true;
            }
        });

        Preference clearPrayerDataCache = findPreference("clear_prayerdata_cache");
        clearPrayerDataCache.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                clearPrayerDataCache();
                return true;
            }
        });

        Dagger.getGraph(getActivity())
                .getAnalyticsProvider()
                .trackViewedScreen(AnalyticsProvider.SCREEN_SETTINGS_MORE);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.setView(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mPresenter.setView(null);
    }

    public void clearLocationCache() {
        mPresenter.clearLocationCache();
    }

    public void clearPrayerDataCache() {
        mPresenter.clearPrayerDataCache();
    }

    @Override
    public void showLocationCacheCleared() {
        Snackbar s = Snackbar.make(mLayout, R.string.label_cache_location_cleared, Snackbar.LENGTH_LONG);
        s.show();
    }

    @Override
    public void showLocationCacheErrored() {
        Snackbar s = Snackbar.make(mLayout, R.string.label_cache_location_errored, Snackbar.LENGTH_LONG);
        s.show();
    }

    @Override
    public void showPrayerDataCacheCleared() {
        Snackbar s = Snackbar.make(mLayout, R.string.label_cache_prayerdata_cleared, Snackbar.LENGTH_LONG);
        s.show();
    }

    @Override
    public void showPrayerDataCacheErrored() {
        Snackbar s = Snackbar.make(mLayout, R.string.label_cache_prayerdata_errored, Snackbar.LENGTH_LONG);
        s.show();
    }

    private boolean isAppInstalled(String packageName) {
        PackageManager pm = getActivity().getPackageManager();

        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            //
        }

        return false;
    }
}
