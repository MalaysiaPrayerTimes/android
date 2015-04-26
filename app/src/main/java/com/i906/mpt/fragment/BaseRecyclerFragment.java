package com.i906.mpt.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import com.i906.mpt.R;

import butterknife.InjectView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import fr.castorflex.android.circularprogressbar.CircularProgressDrawable;

public abstract class BaseRecyclerFragment extends BaseFragment
        implements SwipeRefreshLayout.OnRefreshListener {

    protected boolean mListShown = false;

    @InjectView(R.id.list)
    protected RecyclerView mRecyclerView;

    @InjectView(R.id.progress_container)
    protected View mProgressContainer;

    @InjectView(R.id.recycler_container)
    protected SwipeRefreshLayout mListContainer;

    @InjectView(R.id.progress_bar)
    protected CircularProgressBar mProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recycler, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setListShown(mListShown, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupRecyclerView();
        mListContainer.setOnRefreshListener(this);
    }

    protected abstract void setupRecyclerView();

    protected void setListShown(boolean shown, boolean animate) {
        if (mListContainer.getVisibility() == View.VISIBLE && shown) return;
        if (mListContainer.getVisibility() == View.GONE && !shown) return;

        mListShown = shown;
        if (shown) {
            if (animate) {
                CircularProgressDrawable cpd = ((CircularProgressDrawable) mProgressBar.getProgressDrawable());
                boolean isRunning = cpd != null && cpd.isRunning();

                if (isRunning) {
                    mProgressBar.progressiveStop(circularProgressDrawable -> showList());
                } else {
                    showList();
                }
            } else {
                mProgressContainer.clearAnimation();
                mListContainer.clearAnimation();
                mProgressContainer.setVisibility(View.GONE);
                mListContainer.setVisibility(View.VISIBLE);
            }

        } else {
            if (animate) {
                mProgressContainer.startAnimation(AnimationUtils.loadAnimation(
                        getActivity(), android.R.anim.fade_in));
                mListContainer.startAnimation(AnimationUtils.loadAnimation(
                        getActivity(), android.R.anim.fade_out));
            } else {
                mProgressContainer.clearAnimation();
                mListContainer.clearAnimation();
            }
            mProgressContainer.setVisibility(View.VISIBLE);
            mListContainer.setVisibility(View.GONE);
        }
    }

    protected void showList() {
        mProgressContainer.startAnimation(AnimationUtils.loadAnimation(
                getActivity(), android.R.anim.fade_out));
        mListContainer.startAnimation(AnimationUtils.loadAnimation(
                getActivity(), android.R.anim.fade_in));
        mProgressContainer.setVisibility(View.GONE);
        mListContainer.setVisibility(View.VISIBLE);
    }
}
