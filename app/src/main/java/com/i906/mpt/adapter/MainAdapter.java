package com.i906.mpt.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.i906.mpt.fragment.MosqueFragment;
import com.i906.mpt.fragment.PrayerFragment;
import com.i906.mpt.fragment.QiblaFragment;

public class MainAdapter extends FragmentStatePagerAdapter {

    public MainAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new QiblaFragment();
            case 1:
                return new PrayerFragment();
            case 2:
                return new MosqueFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
