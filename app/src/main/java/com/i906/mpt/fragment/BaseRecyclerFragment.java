package com.i906.mpt.fragment;

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.i906.mpt.R;

import butterknife.Bind;
import butterknife.OnClick;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import fr.castorflex.android.circularprogressbar.CircularProgressDrawable;

public abstract class BaseRecyclerFragment extends BaseFragment {

    @Bind(R.id.list)
    protected RecyclerView mRecyclerView;

    @Bind(R.id.progress_container)
    protected View mProgressContainer;

    @Bind(R.id.error_container)
    protected View mErrorContainer;

    @Bind(R.id.tv_error)
    protected TextView mErrorMessageView;

    @Bind(R.id.recycler_container)
    protected View mListContainer;

    @Bind(R.id.swipe_container)
    protected SwipeRefreshLayout mSwipeContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recycler, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showProgress();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupRecyclerView();
        setupSwipeRefreshView();
    }

    protected abstract void setupRecyclerView();

    protected void onRefresh(boolean pull) {
    }

    protected void setupSwipeRefreshView() {
        mSwipeContainer.setOnRefreshListener(() -> BaseRecyclerFragment.this.onRefresh(true));
    }

    @OnClick(R.id.btn_retry)
    protected void onRetryButtonClicked() {
        showProgress();
        onRefresh(false);
    }

    protected void showContent() {
        mSwipeContainer.setRefreshing(false);
        setContentVisibility(true, true);
        setProgressVisibility(false, true);
        setErrorVisibility(false, true);
    }

    protected void showProgress() {
        setContentVisibility(false, true);
        setProgressVisibility(true, true);
        setErrorVisibility(false, true);
    }

    protected void showError(@StringRes int resId) {
        showError(getString(resId));
    }

    protected void showError(String message) {
        mErrorMessageView.setText(message);
        setContentVisibility(false, true);
        setProgressVisibility(false, true);
        setErrorVisibility(true, true);
    }

    protected void setContentVisibility(boolean visible, boolean animate) {
        setViewVisibility(mListContainer, visible, animate);
    }

    protected void setProgressVisibility(boolean visible, boolean animate) {
        setViewVisibility(mProgressContainer, visible, animate);
    }

    protected void setErrorVisibility(boolean visible, boolean animate) {
        setViewVisibility(mErrorContainer, visible, animate);
    }

    protected void setViewVisibility(View view, boolean visible, boolean animate) {
        if (view.getVisibility() == View.VISIBLE && visible) return;
        if (view.getVisibility() == View.GONE && !visible) return;

        if (visible) {
            if (animate) {
                view.startAnimation(
                        AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
            } else {
                view.clearAnimation();
            }
            view.setVisibility(View.VISIBLE);
        } else {
            if (animate) {
                view.startAnimation(
                        AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
            } else {
                view.clearAnimation();
            }
            view.setVisibility(View.GONE);
        }
    }
}
