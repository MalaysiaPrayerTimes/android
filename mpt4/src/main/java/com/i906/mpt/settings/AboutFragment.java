package com.i906.mpt.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;

import com.i906.mpt.BuildConfig;
import com.i906.mpt.R;

import hu.supercluster.paperwork.Paperwork;

/**
 * @author Noorzaini Ilhami
 */
public class AboutFragment extends BasePreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_about);

        Paperwork paperwork = new Paperwork(getActivity());
        String buildTime = paperwork.get("buildTime");
        String gitInfo = paperwork.get("gitInfo");

        Preference build = findPreference("general_pref_build");
        Preference version = findPreference("general_pref_version");
        Preference playStore = findPreference("general_pref_review");

        version.setSummary(BuildConfig.VERSION_NAME);
        build.setSummary(String.format("%s %s %s", BuildConfig.VERSION_CODE, gitInfo, buildTime));

        playStore.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                openPlayStore();
                return true;
            }
        });
    }

    private void openPlayStore() {
        String appid = BuildConfig.APPLICATION_ID;
        try {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + appid)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + appid)));
        }
    }
}
