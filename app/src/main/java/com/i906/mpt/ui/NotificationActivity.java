package com.i906.mpt.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.i906.mpt.R;
import com.i906.mpt.fragment.AzanPickerFragment;
import com.i906.mpt.fragment.NotificationFragment;

/**
 * Created by Noorzaini Ilhami on 17/10/2015.
 */
public class NotificationActivity extends BaseActivity implements AzanPickerFragment.AzanListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            if (findViewById(R.id.fragment_container) != null) {
                NotificationFragment ef = new NotificationFragment();

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, ef, "NOTIFICATION")
                        .commit();
            }
        }
    }

    public void showAzanPicker(int prayer, String toneUri, boolean notifcation) {
        AzanPickerFragment.newInstance(prayer, toneUri, notifcation)
                .show(getSupportFragmentManager(), "AZAN_PICKER");
    }

    private void refresh() {
        NotificationFragment nf = (NotificationFragment) getSupportFragmentManager()
                .findFragmentByTag("NOTIFICATION");

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
