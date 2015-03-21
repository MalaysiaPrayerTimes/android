package com.i906.mpt.ui;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.i906.mpt.R;
import com.i906.mpt.adapter.MainAdapter;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    protected MainAdapter mAdapter;

    @InjectView(R.id.frame)
    protected ViewGroup mFrameView;

    @InjectView(R.id.toolbar)
    protected Toolbar mToolbar;

    @InjectView(R.id.viewpager)
    protected ViewPager mViewPager;

    @InjectView(R.id.btn_qibla)
    protected ImageButton mQiblaButton;

    @InjectView(R.id.btn_prayer)
    protected ImageButton mPrayerButton;

    @InjectView(R.id.btn_mosque)
    protected ImageButton mMosqueButton;

    @InjectView(R.id.btn_settings)
    protected ImageButton mSettingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        mAdapter = new MainAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(mPageChangeListener);
        mViewPager.setCurrentItem(1);
        mViewPager.setOffscreenPageLimit(3);
        if (mToolbar != null) setSupportActionBar(mToolbar);
    }

    @OnClick(R.id.btn_qibla)
    protected void onQiblaButtonClicked() {
        mViewPager.setCurrentItem(0);
    }

    @OnClick(R.id.btn_prayer)
    protected void onPrayerButtonClicked() {
        mViewPager.setCurrentItem(1);
    }

    @OnClick(R.id.btn_mosque)
    protected void onMosqueButtonClicked() {
        mViewPager.setCurrentItem(2);
    }

    @OnClick(R.id.btn_settings)
    protected void onSettingsButtonClicked() {
        PopupMenu menu = new PopupMenu(this, mSettingsButton, Gravity.BOTTOM | Gravity.RIGHT);
        mSettingsButton.setOnTouchListener(menu.getDragToOpenListener());
        menu.inflate(R.menu.menu_main);
        menu.show();
    }

    private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {

        int activeColor = -1;
        int moresize = 0;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            if (activeColor == -1) activeColor = getResources().getColor(R.color.mpt_color_accent);
            if (moresize == 0) moresize = mSettingsButton.getHeight() * 2;

            int active = swapColor(activeColor, Color.WHITE, positionOffset);
            int normal = swapColor(activeColor, Color.WHITE, 1 - positionOffset);

            if (position == 0) {
                mQiblaButton.setColorFilter(active, PorterDuff.Mode.SRC_IN);
                mPrayerButton.setColorFilter(normal, PorterDuff.Mode.SRC_IN);
            }

            if (position == 1) {
                mPrayerButton.setColorFilter(active, PorterDuff.Mode.SRC_IN);
                mMosqueButton.setColorFilter(normal, PorterDuff.Mode.SRC_IN);
            }

            if (position == 2) {
                mMosqueButton.setColorFilter(active, PorterDuff.Mode.SRC_IN);
                mPrayerButton.setColorFilter(normal, PorterDuff.Mode.SRC_IN);
            }

            if (position == 0) {
                mSettingsButton.setTranslationY(moresize * (1 - positionOffset));
            }

            if (position == 1) {
                mSettingsButton.setTranslationY(moresize * positionOffset);
            }
        }

        @Override
        public void onPageSelected(int position) { }

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
}
