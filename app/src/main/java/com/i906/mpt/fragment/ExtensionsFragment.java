package com.i906.mpt.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.i906.mpt.adapter.ExtensionsAdapter;
import com.i906.mpt.extension.ExtensionInfo;
import com.i906.mpt.view.DividerItemDecoration;

public class ExtensionsFragment extends BaseRecyclerFragment implements ExtensionsAdapter.ExtensionListener {

    protected ExtensionsAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new ExtensionsAdapter(getActivity());
        mAdapter.setExtensionList(mExtensionManager.getExtensions());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mAdapter.isEmpty()) {
            onRefresh(false);
        } else {
            showContent();
        }
    }

    @Override
    protected void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), null));
        if (mRecyclerView.getAdapter() == null) mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onRefresh(boolean pull) {
        mAdapter.setExtensionList(mExtensionManager.getExtensions());
        mSwipeContainer.setRefreshing(false);
    }

    @Override
    public void onScreenSelected(ExtensionInfo.Screen screen) {
        mPrefs.setSelectedPrayerView(screen.getView());
    }

    @Override
    public void onExtensionUninstall(ExtensionInfo extension) {
        mExtensionManager.uninstallExtension(getActivity(), extension);
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
