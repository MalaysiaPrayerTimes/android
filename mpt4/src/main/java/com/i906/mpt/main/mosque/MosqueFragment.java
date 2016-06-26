package com.i906.mpt.main.mosque;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.i906.mpt.R;
import com.i906.mpt.api.foursquare.Mosque;
import com.i906.mpt.common.BaseFragment;
import com.i906.mpt.common.DividerItemDecoration;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * @author Noorzaini Ilhami
 */
public class MosqueFragment extends BaseFragment implements MosqueView, MosqueAdapter.MosqueListener {

    @Inject
    MosquePresenter mPresenter;

    private MosqueAdapter mAdapter;
    private Snackbar mSnackbar;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout mRefreshLayout;

    @BindView(R.id.progress_container)
    View mProgressLayout;

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityGraph().inject(this);
        mAdapter = new MosqueAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mosque, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.setView(this);
        mPresenter.getMosqueList(false);

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.getMosqueList(true);
            }
        });

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), null));
        if (mRecyclerView.getAdapter() == null) mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void showLoading() {
        showSwipeRefreshLoading(true);
        if (mAdapter.isEmpty()) setViewVisibility(mProgressLayout, true, true);
        if (mSnackbar != null) mSnackbar.dismiss();
    }

    @Override
    public void showMosqueList(List<Mosque> mosqueList) {
        showSwipeRefreshLoading(false);
        mAdapter.setMosqueList(mosqueList);
        setViewVisibility(mProgressLayout, mAdapter.isEmpty(), true);
        if (mSnackbar != null) mSnackbar.dismiss();
    }

    @Override
    public void showError(Throwable error) {
        showSwipeRefreshLoading(false);
        if (mAdapter.isEmpty()) setViewVisibility(mProgressLayout, true, true);

        mSnackbar = Snackbar.make(getView(), getErrorMessage(error, R.string.error_unexpected),
                Snackbar.LENGTH_INDEFINITE);

        mSnackbar.show();
    }

    @Override
    public void onMosqueSelected(Mosque mosque) {
        String name = mosque.getName();
        double lat = mosque.getLatitude();
        double lng = mosque.getLongitude();

        String uri = "geo:0,0?q=" + Uri.encode(String.format("%s@%f,%f", name, lat, lng), "UTF-8");
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
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
    public void onStart() {
        super.onStart();
        mAdapter.setMosqueListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.removeMosqueListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mPresenter.setView(null);
    }
}
