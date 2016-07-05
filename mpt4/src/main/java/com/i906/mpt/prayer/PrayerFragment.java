package com.i906.mpt.prayer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.i906.mpt.R;
import com.i906.mpt.common.BaseFragment;

import javax.inject.Inject;

/**
 * @author Noorzaini Ilhami
 */
public class PrayerFragment extends BaseFragment implements PrayerView {

    @Inject
    PrayerPresenter mPresenter;

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
    }

    @Override
    public void showPrayerContext(PrayerContext prayerContext) {
        Prayer current = prayerContext.getCurrentPrayer();
        Prayer next = prayerContext.getNextPrayer();
        Log.d("PrayerFragment", "currentPrayer: " + current);
        Log.d("PrayerFragment", "nextPrayer: " + next);
    }

    @Override
    public void showError(Throwable error) {
        Log.e("PrayerFragment", "error", error);
    }

    @Override
    public void showLoading() {
        Log.v("PrayerFragment", "loading");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mPresenter.setView(null);
    }
}
