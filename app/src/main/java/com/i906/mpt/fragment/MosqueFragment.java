package com.i906.mpt.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.i906.mpt.adapter.MosqueAdapter;
import com.i906.mpt.model.Mosque;
import com.i906.mpt.view.DividerItemDecoration;

import java.util.List;
import java.util.Locale;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MosqueFragment extends BaseRecyclerFragment implements MosqueAdapter.MosqueListener {

    private Subscription mSubscription;
    private LinearLayoutManager mLayoutManager;
    private MosqueAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        onRefresh();
    }

    @Override
    public void setupRecyclerView() {
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addOnScrollListener(mScrollListener);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), null));

        if (mAdapter == null) {
            mAdapter = new MosqueAdapter();
            mAdapter.setOnMosqueSelectedListener(this);
        }

        if (mRecyclerView.getAdapter() == null) mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onRefresh() {
        mSubscription = mMosqueHelper.getNearbyMosques()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Mosque>>() {
                    @Override
                    public void onCompleted() {
                        mListContainer.setRefreshing(false);
                        setListShown(true, true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(List<Mosque> mosques) {
                        mAdapter.setMosqueList(mosques);
                    }
                });
    }

    @Override
    public void onMosqueSelected(Mosque mosque) {
        String name = mosque.getName();
        double lat = mosque.getLatitude();
        double lng = mosque.getLongitude();

        String coordinates = String.format(Locale.ENGLISH, "%f,%f", lat, lng);
        String uri = String.format(Locale.ENGLISH, "geo:%s(%s)?q=%s (%s)", coordinates, name,
                coordinates, name);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
    }

    private RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            boolean e = mLayoutManager.findFirstCompletelyVisibleItemPosition() == 0;
            mListContainer.setEnabled(e);
        }
    };

    @Override
    public void onStop() {
        super.onStop();
        mSubscription.unsubscribe();
        mRecyclerView.removeOnScrollListener(mScrollListener);
    }
}
