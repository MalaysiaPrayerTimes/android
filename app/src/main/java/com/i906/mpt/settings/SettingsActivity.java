package com.i906.mpt.settings;

import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.i906.mpt.BuildConfig;
import com.i906.mpt.R;
import com.i906.mpt.analytics.AnalyticsProvider;
import com.i906.mpt.api.prayer.PrayerCode;
import com.i906.mpt.internal.ActivityModule;
import com.i906.mpt.internal.Dagger;
import com.i906.mpt.prefs.LocationPreferences;
import com.i906.mpt.prefs.NotificationPreferences;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Noorzaini Ilhami
 */
public class SettingsActivity extends AppCompatPreferenceActivity {

    @Inject
    AnalyticsProvider mAnalyticsProvider;

    @Inject
    LocationPreferences mLocationPreferences;

    @Inject
    NotificationPreferences mNotificationPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Dagger.getGraph(this)
                .activityGraph(new ActivityModule(this))
                .inject(this);

        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mAnalyticsProvider.trackViewedScreen(AnalyticsProvider.SCREEN_SETTINGS);
    }

    @Override
    public boolean onIsMultiPane() {
        return getResources().getBoolean(R.bool.is_tablet);
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);

        for (Header header : target) {
            if (header.id == R.id.header_location) {
                PrayerCode pl = mLocationPreferences.getPreferredLocation();
                boolean auto = mLocationPreferences.isUsingAutomaticLocation();

                if (pl != null) {
                    header.summary = getString(R.string.summary_location_manual, pl.getCity());
                }

                if (auto) {
                    header.summary = getString(R.string.summary_location_automatic);
                } else if (pl == null) {
                    header.summary = getString(R.string.summary_location_forced);
                }
            }

            if (header.id == R.id.header_notifications) {
                int count = getEnabledNotificationCount();

                if (count == 8) {
                    header.summary = getString(R.string.summary_notification_all);
                } else if (count == 0) {
                    header.summary = getString(R.string.summary_notification_none);
                } else {
                    header.summary = getString(R.string.summary_notification_some, count);
                }
            }

            if (header.id == R.id.header_about) {
                header.summary = "MPT " + BuildConfig.VERSION_NAME;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateHeaders();
    }

    @Override
    public void setListAdapter(ListAdapter adapter) {
        super.setListAdapter(new ModifiedHeaderAdapter(this, adapter));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected boolean isValidFragment(String fragmentName) {
        return true;
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }

    public static void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    public static void bindPreferenceSummaryToValue(Preference preference, String name) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                preference.getContext().getSharedPreferences(name, MODE_PRIVATE)
                        .getString(preference.getKey(), ""));
    }

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else if (preference instanceof RingtonePreference) {
                if (TextUtils.isEmpty(stringValue)) {
                    preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        preference.setSummary(null);
                    } else {
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            } else {
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    private int getEnabledNotificationCount() {
        int notification = 0;

        for (int i = 0; i < 8; i++) {
            if (mNotificationPreferences.isPrayerEnabled(i)) {
                if (mNotificationPreferences.isNotificationEnabled(i)) {
                    notification++;
                }
            }
        }

        return notification;
    }

    static class ModifiedHeaderAdapter extends ArrayAdapter<Header> {

        private final ListAdapter mAdapter;
        private final LayoutInflater mInflater;


        ModifiedHeaderAdapter(Context context, ListAdapter adapter) {
            super(context, 0);
            mAdapter = adapter;
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            HeaderViewHolder holder;
            View view;

            if (convertView == null) {
                view = mInflater.inflate(R.layout.preference_header_item, parent, false);
                holder = new HeaderViewHolder(view);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (HeaderViewHolder) view.getTag();
            }

            Header header = getItem(position);

            if (header.iconRes == 0) {
                holder.icon.setVisibility(View.GONE);
            } else {
                holder.icon.setVisibility(View.VISIBLE);
                holder.icon.setImageResource(header.iconRes);
            }

            holder.title.setText(header.getTitle(getContext().getResources()));
            CharSequence summary = header.getSummary(getContext().getResources());

            if (!TextUtils.isEmpty(summary)) {
                holder.summary.setVisibility(View.VISIBLE);
                holder.summary.setText(summary);
            } else {
                holder.summary.setVisibility(View.GONE);
            }

            return view;
        }

        @Override
        public int getCount() {
            return mAdapter.getCount();
        }

        @Override
        public Header getItem(int position) {
            return (Header) mAdapter.getItem(position);
        }

        static class HeaderViewHolder {

            @BindView(android.R.id.icon)
            ImageView icon;

            @BindView(android.R.id.title)
            TextView title;

            @BindView(android.R.id.summary)
            TextView summary;

            HeaderViewHolder(View itemView) {
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
