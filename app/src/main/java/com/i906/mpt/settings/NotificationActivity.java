package com.i906.mpt.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.i906.mpt.R;
import com.i906.mpt.common.BaseActivity;
import com.i906.mpt.prefs.NotificationPreferences;
import com.i906.mpt.settings.azanpicker.AzanPickerFragment;
import com.i906.mpt.settings.prayer.PrayerNotificationFragment;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by Noorzaini Ilhami on 17/10/2015.
 */
public class NotificationActivity extends BaseActivity implements AzanPickerFragment.AzanListener {

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
    }

    public void showAzanPicker(int prayer, String toneUri, boolean notifcation) {
        AzanPickerFragment.newInstance(prayer, toneUri, notifcation)
                .show(getSupportFragmentManager(), "AZAN_PICKER");
    }

    private void refresh() {
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

    public static void start(Context context) {
        Intent intent = new Intent(context, NotificationActivity.class);
        context.startActivity(intent);
    }
}
