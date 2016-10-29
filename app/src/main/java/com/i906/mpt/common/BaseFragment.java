package com.i906.mpt.common;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.i906.mpt.internal.ActivityGraph;
import com.i906.mpt.internal.Graph;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment extends Fragment {

    protected static final int DEFAULT_LOCATION_REQUEST_CODE = 4079;
    protected static final int DEFAULT_PERMISSIONS_REQUEST_CODE = 4857;
    protected static final int DEFAULT_RESOLUTION_REQUEST_CODE = 2540;

    private Unbinder mUnbinder;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUnbinder = ButterKnife.bind(this, view);
    }

    protected void requestLocationPermissions() {
        requestPermissions(new String[] {
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        }, DEFAULT_PERMISSIONS_REQUEST_CODE);
    }

    protected void openLocationSettings() {
        Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent, DEFAULT_LOCATION_REQUEST_CODE);
    }

    protected void setViewVisibility(View view, boolean visible, boolean animate) {
        ((BaseActivity) getActivity()).setViewVisibility(view, visible, animate);
    }

    protected int getErrorMessage(Throwable e, int defaultResId) {
        return ((BaseActivity) getActivity()).getErrorMessage(e, defaultResId);
    }

    protected Graph graph() {
        return ((BaseActivity) getActivity()).graph();
    }

    protected ActivityGraph activityGraph() {
        return ((BaseActivity) getActivity()).activityGraph();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mUnbinder != null) mUnbinder.unbind();
    }
}
