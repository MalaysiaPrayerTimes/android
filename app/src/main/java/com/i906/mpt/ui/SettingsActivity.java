package com.i906.mpt.ui;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.i906.mpt.BuildConfig;
import com.i906.mpt.R;

import java.util.List;

public class SettingsActivity extends PreferenceActivity {

    private static final boolean ALWAYS_SIMPLE_PREFS = false;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        LinearLayout root = (LinearLayout) findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.view_settings_toolbar, root, false);
        root.addView(bar, 0);
        bar.setNavigationOnClickListener(v -> finish());

        setupSimplePreferencesScreen();
    }

    private void setupSimplePreferencesScreen() {
        if (!isSimplePreferences(this)) {
            return;
        }

        addPreferencesFromResource(R.xml.pref_interface);
        addPreferencesFromResource(R.xml.pref_about);

        Preference version = findPreference("general_pref_version");
        Preference build = findPreference("general_pref_build");
        Preference rate = findPreference("general_pref_review");
        Preference feedback = findPreference("general_pref_feedback");
        Preference screen = findPreference("general_pref_screen");

        version.setSummary(BuildConfig.VERSION_NAME);
        build.setSummary(String.format("%s %s %s",
                BuildConfig.VERSION_CODE, BuildConfig.GIT_SHA, BuildConfig.BUILD_TIME));

        rate.setOnPreferenceClickListener(preference -> {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=com.i906.mpt")));
            } catch (ActivityNotFoundException n) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=com.i906.mpt")));
                } catch (ActivityNotFoundException a) {
                    /* .. */
                }
            }
            return false;
        });

        feedback.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO,
                    Uri.fromParts("mailto", "feedback@i906.my", null));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Malaysia Prayer Times");

            startActivity(Intent.createChooser(intent, "Send email..."));
            return false;
        });

        screen.setOnPreferenceClickListener(preference -> {
            ExtensionsActivity.start(SettingsActivity.this);
            return false;
        });
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        if (!isSimplePreferences(this)) {
            loadHeadersFromResource(R.xml.pref_headers, target);
        }
    }

    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this) && !isSimplePreferences(this);
    }

    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    private static boolean isSimplePreferences(Context context) {
        return ALWAYS_SIMPLE_PREFS
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
                || !isXLargeTablet(context);
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }

    public static class InterfacePreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_interface);
        }
    }

    public static class AboutPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_about);
        }
    }
}
