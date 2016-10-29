package com.i906.mpt.mosque.ui;

import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.i906.mpt.R;
import com.i906.mpt.api.foursquare.Mosque;
import com.i906.mpt.common.BaseFragment;
import com.i906.mpt.common.DividerItemDecoration;
import com.i906.mpt.location.LocationRepository;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

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

    @BindView(R.id.viewflipper)
    ViewFlipper mViewFlipper;

    @BindView(R.id.tv_error)
    TextView mErrorMessageView;

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    @BindView(R.id.progress)
    ImageView mProgressView;

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

        mRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.getMosqueList(true);
            }
        });

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(ContextCompat.getDrawable(getActivity(), R.drawable.divider)));
        if (mRecyclerView.getAdapter() == null) mRecyclerView.setAdapter(mAdapter);

        Drawable drawable = mProgressView.getDrawable();
        if (drawable instanceof Animatable) {
            ((Animatable) drawable).start();
        }
    }

    @Override
    public void showLoading() {
        showSwipeRefreshLoading(true);

        if (mAdapter.isEmpty() || mViewFlipper.getDisplayedChild() != 1) {
            mViewFlipper.setDisplayedChild(0);
        }
    }

    @Override
    public void showMosqueList(List<Mosque> mosqueList) {
        showSwipeRefreshLoading(false);
        mAdapter.setMosqueList(mosqueList);
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
            mSnackbar = Snackbar.make(getView(), errorMessage, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.label_retry, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mPresenter.getMosqueList(true);
                        }
                    });

            mSnackbar.show();
        }
    }

    @OnClick(R.id.btn_retry)
    void onRetryButtonClicked() {
        mPresenter.getMosqueList(true);
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

    @Override
    protected int getErrorMessage(Throwable e, int defaultResId) {
        if (LocationRepository.isThrowableLocationSettingsRelated(e)) {
            return R.string.error_no_location;
        }
        return super.getErrorMessage(e, defaultResId);
    }
}
