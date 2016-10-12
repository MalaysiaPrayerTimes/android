package com.i906.mpt.settings.prayer;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.i906.mpt.R;
import com.i906.mpt.common.BaseFragment;
import com.i906.mpt.prefs.NotificationPreferences;
import com.i906.mpt.settings.NotificationActivity;
import com.i906.mpt.util.RingtoneHelper;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by Noorzaini Ilhami on 17/10/2015.
 */
public class PrayerNotificationFragment extends BaseFragment implements PrayerNotificationAdapter.NotificationListener {

    private PrayerNotificationAdapter mAdapter;

    @Inject
    NotificationPreferences mNotificationPrefs;

    @Inject
    RingtoneHelper mRingtoneHelper;

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityGraph().inject(this);
        mAdapter = new PrayerNotificationAdapter(getActivity(), mNotificationPrefs, mRingtoneHelper);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_prayer_notification, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new NotificationItemDecoration(getActivity()));
        if (mRecyclerView.getAdapter() == null) mRecyclerView.setAdapter(mAdapter);
    }

    public void refresh() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onReminderButtonClicked(int prayer) {
        ((NotificationActivity) getActivity())
                .showAzanPicker(prayer, mNotificationPrefs.getReminderTone(prayer), false);
    }

    @Override
    public void onNotificationButtonClicked(int prayer) {
        ((NotificationActivity) getActivity())
                .showAzanPicker(prayer, mNotificationPrefs.getNotificationTone(prayer), true);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter.setListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.removeListener();
    }
}
