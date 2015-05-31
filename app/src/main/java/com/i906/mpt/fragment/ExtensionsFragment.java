package com.i906.mpt.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.i906.mpt.adapter.ExtensionsAdapter;
import com.i906.mpt.view.DividerItemDecoration;

public class ExtensionsFragment extends BaseRecyclerFragment {

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
            onRefresh();
        } else {
            setListShown(true, false);
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
    public void onRefresh() {
        mAdapter.setExtensionList(mExtensionManager.getExtensions());
        setListShown(true, true);
        mListContainer.setRefreshing(false);
    }
}
