package com.i906.mpt.prayer.ui;

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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.i906.mpt.R;
import com.i906.mpt.common.BaseFragment;
import com.i906.mpt.location.LocationRepository;
import com.i906.mpt.prayer.PrayerContext;
import com.i906.mpt.settings.SettingsActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author Noorzaini Ilhami
 */
public class PrayerFragment extends BaseFragment implements PrayerView {

    private Snackbar mSnackbar;

    @Inject
    PrayerPresenter mPresenter;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout mRefreshLayout;

    @BindView(R.id.viewflipper)
    ViewFlipper mViewFlipper;

    @BindView(R.id.tv_error)
    TextView mErrorMessageView;

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
        mPrayerListView.showError(error);
        if (mSnackbar != null) mSnackbar.dismiss();

        int errorMessage = getErrorMessage(error, R.string.error_unexpected);

        if (mViewFlipper.getDisplayedChild() != 1) {
            mViewFlipper.setDisplayedChild(2);
            mErrorMessageView.setText(getErrorMessage(error, R.string.error_unexpected));
        } else {
            mSnackbar = Snackbar.make(getView(), errorMessage, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.label_retry, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mPresenter.getPrayerContext(true);
                        }
                    });

            mSnackbar.show();
        }
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
        mPresenter.getPrayerContext(true);
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
    public void onDetach() {
        super.onDetach();
        mPresenter.setView(null);
    }

    @Override
    protected int getErrorMessage(Throwable e, int defaultResId) {
        if (LocationRepository.isThrowableLocationSettingsRelated(e)) {
            return R.string.error_no_location;
        }
        return super.getErrorMessage(e, defaultResId);
    }
}
