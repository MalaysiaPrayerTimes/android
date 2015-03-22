package com.i906.mpt.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.i906.mpt.fragment.MosqueFragment;
import com.i906.mpt.fragment.PrayerFragment;
import com.i906.mpt.fragment.QiblaFragment;

import java.util.ArrayList;
import java.util.List;

public class MainAdapter extends FragmentStatePagerAdapter {

    protected boolean mShowQibla;
    protected List<Fragment> mFragmentList;

    public MainAdapter(FragmentManager fm, boolean showQibla) {
        super(fm);

        mShowQibla = showQibla;
        mFragmentList = new ArrayList<>();

        if (showQibla) mFragmentList.add(new QiblaFragment());
        mFragmentList.add(new PrayerFragment());
        mFragmentList.add(new MosqueFragment());
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }
}
