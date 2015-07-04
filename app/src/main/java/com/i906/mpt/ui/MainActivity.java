package com.i906.mpt.ui;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.i906.mpt.R;
import com.i906.mpt.adapter.MainAdapter;

import butterknife.Bind;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    protected boolean mHasSensors = false;
    protected boolean mLandscapeMode = false;

    protected int mQPos = 0;
    protected int mPPos = 1;
    protected int mMPos = 2;

    protected MainAdapter mAdapter;

    @Bind(R.id.frame)
    protected ViewGroup mFrameView;

    @Bind(R.id.toolbar)
    protected Toolbar mToolbar;

    @Bind(R.id.viewpager)
    protected ViewPager mViewPager;

    @Bind(R.id.btn_qibla)
    protected ImageButton mQiblaButton;

    @Bind(R.id.btn_prayer)
    protected ImageButton mPrayerButton;

    @Bind(R.id.btn_mosque)
    protected ImageButton mMosqueButton;

    @Nullable
    @Bind(R.id.btn_settings)
    protected ImageButton mSettingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHasSensors = mUtils.hasSufficientSensors();

        mLandscapeMode = getResources().getBoolean(R.bool.landscape_mode);
        mAdapter = new MainAdapter(getSupportFragmentManager(), mHasSensors);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(mPageChangeListener);
        mViewPager.setCurrentItem(mHasSensors ? 1 : 0);
        mViewPager.setOffscreenPageLimit(3);

        if (!mHasSensors) {
            mQiblaButton.setVisibility(View.GONE);
            mQPos = -1;
            mPPos = 0;
            mMPos = 1;
        }

        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    @OnClick(R.id.btn_qibla)
    protected void onQiblaButtonClicked() {
        mViewPager.setCurrentItem(mQPos);
    }

    @OnClick(R.id.btn_prayer)
    protected void onPrayerButtonClicked() {
        mViewPager.setCurrentItem(mPPos);
    }

    @OnClick(R.id.btn_mosque)
    protected void onMosqueButtonClicked() {
        mViewPager.setCurrentItem(mMPos);
    }

    @Nullable
    @OnClick(R.id.btn_settings)
    protected void onSettingsButtonClicked() {
        PopupMenu menu = new PopupMenu(this, mSettingsButton);
        mSettingsButton.setOnTouchListener(menu.getDragToOpenListener());
        menu.inflate(R.menu.menu_main);
        menu.setOnMenuItemClickListener(this::onOptionsItemSelected);
        menu.show();
    }

    private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {

        int activeColor = -1;
        int normalColor = Color.WHITE;
        int moresize = 0;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            if (activeColor == -1) activeColor = getResources().getColor(R.color.mpt_color_accent);

            int active = swapColor(activeColor, normalColor, positionOffset);
            int normal = swapColor(activeColor, normalColor, 1 - positionOffset);

            if (position == mQPos) {
                mQiblaButton.setColorFilter(active, PorterDuff.Mode.SRC_IN);
                mPrayerButton.setColorFilter(normal, PorterDuff.Mode.SRC_IN);
            }

            if (position == mPPos) {
                mPrayerButton.setColorFilter(active, PorterDuff.Mode.SRC_IN);
                mMosqueButton.setColorFilter(normal, PorterDuff.Mode.SRC_IN);
            }

            if (position == mMPos) {
                mMosqueButton.setColorFilter(active, PorterDuff.Mode.SRC_IN);
                mPrayerButton.setColorFilter(normal, PorterDuff.Mode.SRC_IN);
            }

            if (!mLandscapeMode) {
                if (moresize == 0) moresize = mSettingsButton.getHeight() * 2;

                if (position == mPPos - 1) {
                    mSettingsButton.setTranslationY(moresize * (1 - positionOffset));
                }

                if (position == mPPos) {
                    mSettingsButton.setTranslationY(moresize * positionOffset);
                }
            }
        }

        @Override
        public void onPageSelected(int position) {
            /*
            if (position == mQPos) {
                mQiblaButton.setColorFilter(activeColor, PorterDuff.Mode.SRC_IN);
                mPrayerButton.setColorFilter(normalColor, PorterDuff.Mode.SRC_IN);
                mMosqueButton.setColorFilter(normalColor, PorterDuff.Mode.SRC_IN);
                if (mSettingsButton != null) mSettingsButton.setTranslationY(moresize);
            }

            if (position == mPPos) {
                mQiblaButton.setColorFilter(normalColor, PorterDuff.Mode.SRC_IN);
                mPrayerButton.setColorFilter(activeColor, PorterDuff.Mode.SRC_IN);
                mMosqueButton.setColorFilter(normalColor, PorterDuff.Mode.SRC_IN);
                if (mSettingsButton != null) mSettingsButton.setTranslationY(0);
            }

            if (position == mMPos) {
                mQiblaButton.setColorFilter(normalColor, PorterDuff.Mode.SRC_IN);
                mPrayerButton.setColorFilter(normalColor, PorterDuff.Mode.SRC_IN);
                mMosqueButton.setColorFilter(activeColor, PorterDuff.Mode.SRC_IN);
                if (mSettingsButton != null) mSettingsButton.setTranslationY(moresize);
            }
            */
        }

        @Override
        public void onPageScrollStateChanged(int state) { }

        private int swapColor(int c1, int c2, float p) {
            float i = 1 - p;
            int r = (int) (Color.red(c1) * i + Color.red(c2) * p);
            int g = (int) (Color.green(c1) * i + Color.green(c2) * p);
            int b = (int) (Color.blue(c1) * i + Color.blue(c2) * p);
            return Color.argb(0xFF, r, g, b);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mLandscapeMode) getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            mPrayerInterface.refresh();
            return true;
        }

        if (id == R.id.action_settings) {
            SettingsActivity.start(this);
        }

        return super.onOptionsItemSelected(item);
    }
}
