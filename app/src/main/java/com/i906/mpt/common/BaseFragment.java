package com.i906.mpt.common;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.i906.mpt.internal.ActivityGraph;
import com.i906.mpt.internal.Graph;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment extends Fragment {

    private Unbinder mUnbinder;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUnbinder = ButterKnife.bind(this, view);
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
