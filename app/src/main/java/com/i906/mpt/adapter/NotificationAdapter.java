package com.i906.mpt.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.i906.mpt.MptApplication;
import com.i906.mpt.R;
import com.i906.mpt.util.preference.NotificationPrefs;
import com.i906.mpt.view.NotificationSettingsView;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Noorzaini Ilhami on 17/10/2015.
 */
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    @Inject
    protected NotificationPrefs mPrefs;

    private final boolean[] mExpandState;
    private final String[] mPrayerNames;
    private NotificationListener mListener;

    public NotificationAdapter(Context context) {
        MptApplication.component(context).inject(this);
        mPrayerNames = context.getResources().getStringArray(R.array.mpt_prayer_names);

        mExpandState = new boolean[8];
        for (int i = 0; i < 8; i++) {
            mExpandState[i] = mPrefs.isPrayerEnabled(i);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(new NotificationSettingsView(parent.getContext()), this);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        NotificationSettingsView v = (NotificationSettingsView) holder.itemView;
        v.setPrayerName(mPrayerNames[position]);
        v.setPrayerEnabled(mPrefs.isPrayerEnabled(position));
        v.setNotificationEnabled(mPrefs.isNotificationEnabled(position));
        v.setVibrateEnabled(mPrefs.isVibrationEnabled(position));
        v.setCardExpanded(mExpandState[position], false);
    }

    @Override
    public int getItemCount() {
        return 8;
    }

    private void setPrayerEnabled(int prayer, boolean enabled) {
        mPrefs.setPrayerEnabled(prayer, enabled);
    }

    public void setNotificationEnabled(int prayer, boolean enabled) {
        mPrefs.setNotificationEnabled(prayer, enabled);
    }

    public void setVibrationEnabled(int prayer, boolean enabled) {
        mPrefs.setVibrationEnabled(prayer, enabled);
    }

    public void onReminderButtonClicked(int prayer) {

    }

    public void setListener(NotificationListener listener) {
        mListener = listener;
    }

    public void removeListener() {
        mListener = null;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private NotificationAdapter adapter;

        public ViewHolder(NotificationSettingsView v, NotificationAdapter adapter) {
            super(v);
            ButterKnife.bind(this, v);
            this.adapter = adapter;

            v.setPrayerOnClickListener(p -> {
                boolean checked = ((CompoundButton) p).isChecked();
                adapter.setPrayerEnabled(getAdapterPosition(), checked);
            });

            v.setCardExpandListener(c -> adapter.mExpandState[getAdapterPosition()] = v.isCardExpanded());
        }

        @OnClick(R.id.cb_notification)
        protected void onNotificationChanged(CompoundButton v) {
            adapter.setNotificationEnabled(getAdapterPosition(), v.isChecked());
        }

        @OnClick(R.id.cb_vibrate)
        protected void onVibrateChanged(CompoundButton v) {
            adapter.setVibrationEnabled(getAdapterPosition(), v.isChecked());
        }

        @OnClick(R.id.btn_reminder)
        protected void onReminderButtonClicked() {
            adapter.onReminderButtonClicked(getAdapterPosition());
        }
    }

    public interface NotificationListener {
        void onReminderButtonClicked(int prayer);
    }
}
