package com.i906.mpt.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.i906.mpt.R;
import com.i906.mpt.adapter.MosqueAdapter;
import com.i906.mpt.model.Mosque;
import com.i906.mpt.util.Utils;
import com.i906.mpt.view.DividerItemDecoration;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import pl.charmas.android.reactivelocation.observables.GoogleAPIConnectionException;
import rx.Subscription;
import timber.log.Timber;

public class MosqueFragment extends BaseRecyclerFragment implements MosqueAdapter.MosqueListener {

    private LinearLayoutManager mLayoutManager;
    private MosqueAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new MosqueAdapter();
        mAdapter.setMosqueList(mMosqueHelper.getCachedMosques());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mAdapter.isEmpty()) {
            onRefresh(false);
        } else {
            showContent();
        }
    }

    @Override
    public void setupRecyclerView() {
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), null));
        if (mRecyclerView.getAdapter() == null) mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onRefresh(boolean pull) {
        Subscription s = mMosqueHelper.getNearbyMosques()
                .compose(Utils.applySchedulers())
                .subscribe(mosques -> {
                    mAdapter.setMosqueList(mosques);
                    showContent();
                }, e -> {
                    showError(getErrorMessage(e));
                });

        addSubscription(s);
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

    private int getErrorMessage(Throwable e) {
        if (e instanceof SocketTimeoutException || e instanceof UnknownHostException) {
            return R.string.mpt_error_no_network;
        } else if (e instanceof retrofit.HttpException) {
            return R.string.mpt_error_unexpected;
        } else if (e instanceof GoogleAPIConnectionException) {
            return R.string.mpt_error_play_service;
        } else {
            Timber.w(e, "Mosque error.");
            return R.string.mpt_error_unexpected;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter.addMosqueListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.removeMosqueListener(this);
    }
}
