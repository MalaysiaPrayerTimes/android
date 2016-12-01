package com.i906.mpt.prayer.ui;

import android.content.Intent;
import android.content.IntentSender;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.i906.mpt.R;
import com.i906.mpt.analytics.AnalyticsProvider;
import com.i906.mpt.api.prayer.PrayerProviderException;
import com.i906.mpt.common.LocationFragment;
import com.i906.mpt.location.LocationDisabledException;
import com.i906.mpt.location.LocationTimeoutException;
import com.i906.mpt.prayer.PrayerContext;
import com.i906.mpt.settings.SettingsActivity;
import com.i906.mpt.settings.locationpicker.LocationPickerActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author Noorzaini Ilhami
 */
public class PrayerFragment extends LocationFragment implements PrayerView {

    private Snackbar mSnackbar;

    @Inject
    PrayerPresenter mPresenter;

    @Inject
    AnalyticsProvider mAnalytics;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout mRefreshLayout;

    @BindView(R.id.viewflipper)
    ViewFlipper mViewFlipper;

    @BindView(R.id.tv_error)
    TextView mErrorMessageView;

    @BindView(R.id.btn_retry)
    Button mRetryButton;

    @BindView(R.id.btn_secondary_action)
    Button mSecondaryAction;

    @BindView(R.id.prayerlist)
    PrayerListView mPrayerListView;

    @BindView(R.id.progress)
    ImageView mProgressView;

    @BindView(R.id.btn_settings)
    ImageButton mSettingsButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityGraph().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_prayer, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.setView(this);
        refresh(false);

        SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh(true);
            }
        };

        mRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mRefreshLayout.setOnRefreshListener(refreshListener);

        Drawable drawable = mProgressView.getDrawable();
        if (drawable instanceof Animatable) {
            ((Animatable) drawable).start();
        }

        mAnalytics.trackViewedPrayerTimes();
    }

    private void refresh(boolean force) {
        mPresenter.getPrayerContext(force);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            refresh(true);
            return true;
        }

        if (id == R.id.action_settings) {
            SettingsActivity.start(getActivity());
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showPrayerContext(PrayerContext prayerContext) {
        showSwipeRefreshLoading(false);
        mPrayerListView.showPrayerContext(prayerContext);

        if (mViewFlipper.getDisplayedChild() != 1) {
            mViewFlipper.setDisplayedChild(1);
        }
    }

    @Override
    public void showError(Throwable error) {
        showSwipeRefreshLoading(false);
        setErrorFlags(error);

        mPrayerListView.showError(error);
        if (mSnackbar != null) mSnackbar.dismiss();

        String errorMessage = null;
        mSecondaryAction.setVisibility(View.GONE);

        if (error instanceof SecurityException) {
            errorMessage = getString(R.string.error_no_location_permission_prayer);

            mRetryButton.setText(R.string.label_grant_permission);
            mSecondaryAction.setVisibility(View.VISIBLE);
            mSecondaryAction.setText(R.string.label_set_location);
        } else if (error instanceof LocationDisabledException || error instanceof LocationTimeoutException) {
            if (hasLocationResolution()) {
                mRetryButton.setText(R.string.label_enable_location);
            } else {
                mRetryButton.setText(R.string.label_open_location_settings);
            }
        } else if (error instanceof PrayerProviderException) {
            PrayerProviderException ppe = (PrayerProviderException) error;

            if (ppe.hasProviderName()) {
                errorMessage = getString(R.string.error_prayer_provider_named, ppe.getProviderName());
            }
        } else {
            mRetryButton.setText(R.string.label_retry);
        }

        if (errorMessage == null) {
            errorMessage = getString(getErrorMessage(error, R.string.error_unexpected));
        }

        if (mViewFlipper.getDisplayedChild() != 1) {
            mViewFlipper.setDisplayedChild(2);
            mErrorMessageView.setText(errorMessage);
        } else {
            mSnackbar = Snackbar.make(mRefreshLayout, errorMessage, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.label_retry, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onRetryButtonClicked();
                        }
                    });

            mSnackbar.show();
        }
    }

    @Override
    protected void recheckLocation() {
        onRetryButtonClicked();
    }

    @Override
    public void showLoading() {
        mPrayerListView.showLoading();

        if (mViewFlipper.getDisplayedChild() != 1) {
            mViewFlipper.setDisplayedChild(0);
        } else {
            showSwipeRefreshLoading(true);
        }
    }

    @OnClick(R.id.btn_retry)
    void onRetryButtonClicked() {
        if (hasPermissionError()) {
            requestLocationPermissions();
        } else if (hasLocationError()) {
            if (hasLocationResolution()) {
                try {
                    startIntentSenderForResult(getLocationResolution(),
                            DEFAULT_RESOLUTION_REQUEST_CODE, null, 0, 0, 0, null);
                } catch (IntentSender.SendIntentException e) {
                    openLocationSettings();
                }
            } else {
                openLocationSettings();
            }
        } else {
            mPresenter.getPrayerContext(true);
        }
    }

    @OnClick(R.id.btn_secondary_action)
    void onSecondaryActionButtonClicked() {
        Intent intent = new Intent(getActivity(), LocationPickerActivity.class);
        startActivityForResult(intent, DEFAULT_LOCATION_REQUEST_CODE);
    }

    @OnClick(R.id.btn_settings)
    void onSettingsButtonClicked() {
        PopupMenu menu = new PopupMenu(getActivity(), mSettingsButton);
        mSettingsButton.setOnTouchListener(menu.getDragToOpenListener());

        menu.inflate(R.menu.menu_main);
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return onOptionsItemSelected(item);
            }
        });

        menu.show();
    }

    private void showSwipeRefreshLoading(final boolean loading) {
        mRefreshLayout.setRefreshing(loading);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isAdded() && isVisibleToUser) {
            mAnalytics.trackViewedPrayerTimes();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mPresenter.setView(null);
    }
}
