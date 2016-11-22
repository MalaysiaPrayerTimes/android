package com.i906.mpt.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.i906.mpt.R;
import com.i906.mpt.analytics.AnalyticsProvider;
import com.i906.mpt.common.BaseActivity;
import com.i906.mpt.prefs.NotificationPreferences;
import com.i906.mpt.settings.azanpicker.AzanPickerFragment;
import com.i906.mpt.settings.prayer.ApplyAllDialogFragment;
import com.i906.mpt.settings.prayer.PrayerNotificationFragment;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by Noorzaini Ilhami on 17/10/2015.
 */
public class NotificationActivity extends BaseActivity implements AzanPickerFragment.AzanListener {

    @Inject
    AnalyticsProvider mAnalyticsProvider;

    @Inject
    NotificationPreferences mNotificationPrefs;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        activityGraph().inject(this);

        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mAnalyticsProvider.trackViewedScreen(AnalyticsProvider.SCREEN_SETTINGS_CONFIGURE_NOTIFICATIONS);
    }

    public void showAzanPicker(int prayer, String toneUri, boolean notifcation) {
        AzanPickerFragment.newInstance(prayer, toneUri, notifcation)
                .setListener(this)
                .show(getSupportFragmentManager(), "AZAN_PICKER");
    }

    public void refresh() {
        PrayerNotificationFragment nf = (PrayerNotificationFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_prayer_notification);

        if (nf != null) nf.refresh();
    }

    @Override
    public void onToneSelected(int prayer, String toneUri, boolean isNotification) {
        if (isNotification) {
            mNotificationPrefs.setNotificationTone(prayer, toneUri);
        } else {
            mNotificationPrefs.setReminderTone(prayer, toneUri);
        }

        refresh();
    }

    private void showApplyAllDialog() {
        ApplyAllDialogFragment f = new ApplyAllDialogFragment();
        f.show(getSupportFragmentManager(), "APPLY");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_notification, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_apply_to_all) {
            showApplyAllDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, NotificationActivity.class);
        context.startActivity(intent);
    }
}
