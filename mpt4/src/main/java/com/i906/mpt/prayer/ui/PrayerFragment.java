package com.i906.mpt.prayer.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.i906.mpt.R;
import com.i906.mpt.common.BaseFragment;
import com.i906.mpt.prayer.PrayerContext;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * @author Noorzaini Ilhami
 */
public class PrayerFragment extends BaseFragment implements PrayerView {

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
        mPresenter.getPrayerContext(false);

        SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.getPrayerContext(true);
            }
        };

        mRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mRefreshLayout.setOnRefreshListener(refreshListener);
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

        if (mViewFlipper.getDisplayedChild() != 1) {
            mViewFlipper.setDisplayedChild(2);
            mErrorMessageView.setText(getErrorMessage(error, R.string.error_unexpected));
        }
    }

    @Override
    public void showLoading() {
        showSwipeRefreshLoading(true);
        mPrayerListView.showLoading();

        if (mViewFlipper.getDisplayedChild() != 1) {
            mViewFlipper.setDisplayedChild(0);
        }
    }

    @OnClick(R.id.btn_retry)
    void onRetryButtonClicked() {
        mPresenter.getPrayerContext(true);
    }

    private void showSwipeRefreshLoading(final boolean loading) {
        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(loading);
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mPresenter.setView(null);
    }
}
