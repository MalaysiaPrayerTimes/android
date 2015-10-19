package com.i906.mpt.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.i906.mpt.adapter.NotificationAdapter;
import com.i906.mpt.view.NotificationItemDecoration;

/**
 * Created by Noorzaini Ilhami on 17/10/2015.
 */
public class NotificationFragment extends BaseRecyclerFragment implements NotificationAdapter.NotificationListener {

    protected NotificationAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new NotificationAdapter(getActivity());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSwipeContainer.setEnabled(false);
        showContent();
    }

    @Override
    protected void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new NotificationItemDecoration(getActivity()));
        if (mRecyclerView.getAdapter() == null) mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onRefresh(boolean pull) {
    }

    @Override
    public void onReminderButtonClicked(int prayer) {

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
