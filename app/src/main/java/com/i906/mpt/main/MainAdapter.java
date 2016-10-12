package com.i906.mpt.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.i906.mpt.mosque.ui.MosqueFragment;
import com.i906.mpt.prayer.ui.PrayerFragment;
import com.i906.mpt.qibla.QiblaFragment;

import java.util.ArrayList;
import java.util.List;

class MainAdapter extends FragmentPagerAdapter {

    private List<Fragment> mFragmentList;

    MainAdapter(FragmentManager fm) {
        super(fm);
        mFragmentList = new ArrayList<>();
        mFragmentList.add(new QiblaFragment());
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
