package com.i906.mpt.settings.locationpicker;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.i906.mpt.R;
import com.i906.mpt.api.prayer.PrayerCode;
import com.i906.mpt.common.BaseActivity;
import com.i906.mpt.prefs.LocationPreferences;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * @author Noorzaini Ilhami
 */
public class LocationPickerActivity extends BaseActivity implements CodeView, CodeAdapter.CodeListener {

    @Inject
    LocationPickerPresenter mPresenter;

    @Inject
    LocationPreferences mLocationPreferences;

    private CodeAdapter mAdapter;
    private Snackbar mSnackbar;

    @BindView(R.id.coordinator)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout mRefreshLayout;

    @BindView(R.id.viewflipper)
    ViewFlipper mViewFlipper;

    @BindView(R.id.tv_error)
    TextView mErrorMessageView;

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityGraph().inject(this);
        setContentView(R.layout.activity_location_picker);

        mAdapter = new CodeAdapter();
        mAdapter.setCodeListener(this);

        mPresenter.setView(this);
        mPresenter.getCodeList(false);

        mRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.getCodeList(true);
            }
        });

        LinearLayoutManager llm = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(llm);
        if (mRecyclerView.getAdapter() == null) mRecyclerView.setAdapter(mAdapter);
    }

    private void showSwipeRefreshLoading(boolean loading) {
        mRefreshLayout.setRefreshing(loading);
    }

    @Override
    public void onCodeSelected(PrayerCode code) {
        mLocationPreferences.setPreferredLocation(code);
        finish();
    }

    @Override
    public void showLoading() {
        showSwipeRefreshLoading(true);

        if (mAdapter.isEmpty() || mViewFlipper.getDisplayedChild() != 1) {
            mViewFlipper.setDisplayedChild(0);
        }
    }

    @Override
    public void showCodeList(List<PrayerCode> codeList) {
        showSwipeRefreshLoading(false);
        mAdapter.setCodeList(codeList);
        if (mSnackbar != null) mSnackbar.dismiss();

        if (mViewFlipper.getDisplayedChild() != 1) {
            mViewFlipper.setDisplayedChild(1);
        }
    }

    @Override
    public void showError(Throwable error) {
        showSwipeRefreshLoading(false);

        int errorMessage = getErrorMessage(error, R.string.error_unexpected);

        if (mAdapter.isEmpty() || mViewFlipper.getDisplayedChild() != 1) {
            mViewFlipper.setDisplayedChild(2);
            mErrorMessageView.setText(errorMessage);
        } else {
            mSnackbar = Snackbar.make(mCoordinatorLayout, errorMessage, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.label_retry, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mPresenter.getCodeList(true);
                        }
                    });

            mSnackbar.show();
        }
    }
}
