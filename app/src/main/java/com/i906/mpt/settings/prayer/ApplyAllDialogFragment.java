package com.i906.mpt.settings.prayer;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.i906.mpt.R;
import com.i906.mpt.common.BaseDialogFragment;
import com.i906.mpt.prefs.NotificationPreferences;
import com.i906.mpt.settings.NotificationActivity;
import com.i906.mpt.settings.azanpicker.AzanPickerFragment;
import com.i906.mpt.util.RingtoneHelper;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author Noorzaini Ilhami
 */
public class ApplyAllDialogFragment extends BaseDialogFragment implements AzanPickerFragment.AzanListener {

    private Unbinder unbinder;

    private boolean mPrayerEnabled = true;
    private boolean mNotificationEnabled = true;
    private boolean mVibrationEnabled = true;
    private String mNotificationTone;
    private String mReminderTone;

    @Inject
    NotificationPreferences mNotificationPreferences;

    @Inject
    RingtoneHelper mRingtoneHelper;

    @BindView(R.id.tv_prayer_name)
    TextView mPrayerTextView;

    @BindView(R.id.sw_prayer)
    SwitchCompat mPrayerSwitch;

    @BindView(R.id.cb_notification)
    CheckBox mNotificationCheckBox;

    @BindView(R.id.cb_vibrate)
    CheckBox mVibrateCheckBox;

    @BindView(R.id.btn_reminder)
    Button mReminderButton;

    @BindView(R.id.btn_notification)
    Button mNotificationButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, 0);
        activityGraph().inject(this);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_remarks, null);
        unbinder = ButterKnife.bind(this, v);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setPositiveButton(R.string.label_apply, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        applyToAll();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .create();
    }

    @Override
    public void onToneSelected(int prayer, String toneUri, boolean isNotification) {
        if (isNotification) {
            mNotificationTone = toneUri;
            setToneName(mNotificationButton, toneUri, true);
        } else {
            mReminderTone = toneUri;
            setToneName(mReminderButton, toneUri, false);
        }
    }

    private void setToneName(Button b, String name, boolean isNotification) {
        if (name != null) {
            b.setText(mRingtoneHelper.getToneName(name));
        } else {
            b.setText(isNotification ? R.string.label_set_notification_tone :
                    R.string.label_set_reminder_tone);
        }
    }

    @OnClick(R.id.sw_prayer)
    void onPrayerChanged(CompoundButton v) {
        mPrayerEnabled = v.isChecked();
        mPrayerTextView.setEnabled(mPrayerEnabled);
        mNotificationCheckBox.setEnabled(mPrayerEnabled);
        mVibrateCheckBox.setEnabled(mPrayerEnabled);
        mReminderButton.setEnabled(mPrayerEnabled);
        mNotificationButton.setEnabled(mPrayerEnabled);
    }

    @OnClick(R.id.cb_notification)
    void onNotificationChanged(CompoundButton v) {
        mNotificationEnabled = v.isChecked();
    }

    @OnClick(R.id.cb_vibrate)
    void onVibrateChanged(CompoundButton v) {
        mVibrationEnabled = v.isEnabled();
    }

    @OnClick(R.id.btn_reminder)
    void onReminderButtonClicked() {
        showAzanPicker(false);
    }

    @OnClick(R.id.btn_notification)
    void onNotificationButtonClicked() {
        showAzanPicker(true);
    }

    private void showAzanPicker(boolean notifcation) {
        AzanPickerFragment.newInstance(-1, null, notifcation)
                .setListener(this)
                .show(getFragmentManager(), "AZAN_PICKER");
    }

    private void applyToAll() {
        for (int i = 0; i < 8; i++) {
            mNotificationPreferences.setPrayerEnabled(i, mPrayerEnabled);
            mNotificationPreferences.setNotificationEnabled(i, mNotificationEnabled);
            mNotificationPreferences.setVibrationEnabled(i, mVibrationEnabled);
            mNotificationPreferences.setReminderTone(i, mReminderTone);
            mNotificationPreferences.setNotificationTone(i, mNotificationTone);
        }

        ((NotificationActivity) getActivity()).refresh();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) unbinder.unbind();
    }
}
