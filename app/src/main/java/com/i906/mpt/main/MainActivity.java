package com.i906.mpt.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.crashlytics.android.Crashlytics;
import com.i906.mpt.R;
import com.i906.mpt.common.BaseActivity;
import com.i906.mpt.intro.MainIntroActivity;
import com.i906.mpt.prefs.CommonPreferences;
import com.i906.mpt.prefs.LocationPreferences;
import com.i906.mpt.prefs.NotificationPreferences;

import javax.inject.Inject;

import butterknife.BindView;
import io.fabric.sdk.android.Fabric;

/**
 * @author Noorzaini Ilhami
 */
public class MainActivity extends BaseActivity {

    private final int[] mTabIcons = {
            R.drawable.ic_tab_qibla,
            R.drawable.ic_tab_prayer,
            R.drawable.ic_tab_mosque
    };

    private final int[] mTabDesc = {
            R.string.label_qibla,
            R.string.label_prayer,
            R.string.label_mosque
    };

    private MainAdapter mAdapter;

    @Inject
    CommonPreferences mCommonPreferences;

    @Inject
    NotificationPreferences mNotificationPreferences;

    @Inject
    LocationPreferences mLocationPreferences;

    @BindView(R.id.coordinator)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.viewpager)
    ViewPager mViewPager;

    @BindView(R.id.tabs)
    TabLayout mTabLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        activityGraph().inject(this);

        if (mCommonPreferences.isFirstStart()) {
            showIntro();
            mCommonPreferences.convertLegacyPreferences();
            mNotificationPreferences.convertLegacyPreferences();
            mLocationPreferences.convertLegacyPreferences();
        } else {
            setup();
        }
    }

    private void setup() {
        mAdapter = new MainAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(1, false);
        mViewPager.setOffscreenPageLimit(3);
        mTabLayout.setupWithViewPager(mViewPager);

        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = mTabLayout.getTabAt(i);
            tab.setCustomView(R.layout.tab_icon);
            tab.setIcon(mTabIcons[i]);
            tab.setContentDescription(mTabDesc[i]);
        }
    }

    private void showIntro() {
        Intent intent = new Intent(this, MainIntroActivity.class);
        startActivityForResult(intent, 1);
        mCommonPreferences.setFirstStart(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_CANCELED) {
                mCommonPreferences.setFirstStart(true);
                finish();
            }

            if (resultCode == RESULT_OK) {
                setup();
            }
        }
    }
}
