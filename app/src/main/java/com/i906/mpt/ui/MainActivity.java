package com.i906.mpt.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.FrameLayout;

import com.i906.mpt.MptApplication;
import com.i906.mpt.R;
import com.i906.mpt.extension.ExtensionInfo;
import com.i906.mpt.extension.ExtensionManager;
import com.i906.mpt.extension.PrayerInterface;
import com.i906.mpt.extension.PrayerView;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends ActionBarActivity {

    protected ExamplePrayerInterface mInterface;

    @Inject
    protected ExtensionManager mExtensionManager;

    @InjectView(R.id.frame)
    protected FrameLayout mFrameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        MptApplication.component(this).inject(this);


        loadStuff();
    }

    protected void loadStuff() {
        List<ExtensionInfo> eil = mExtensionManager.getExtensions();
        Log.w("mpt-ma", "eil: " + eil);

        PrayerView pv = mExtensionManager.getPrayerView(eil.get(0).screens.get(0));
        mInterface = new ExamplePrayerInterface(pv);
        if (pv != null) {
            pv.setInterface(mInterface);
            mFrameView.addView(pv);
        }
    }

    public static class ExamplePrayerInterface implements PrayerInterface {

        protected PrayerView mPrayerView;

        public ExamplePrayerInterface(PrayerView prayerView) {
            mPrayerView = prayerView;
        }

        @Override
        public Date getCurrentPrayerTime() {
            return new Date();
        }

        @Override
        public Date getNextPrayerTime() {
            return new Date();
        }

        @Override
        public int getCurrentPrayerIndex() {
            return PRAYER_ZOHOR;
        }

        @Override
        public int getNextPrayerIndex() {
            return PRAYER_ASAR;
        }

        @Override
        public int[] getHijriDate() {
            return new int[] { 22, 4, 1436 } ;
        }

        @Override
        public String getLocation() {
            return "Kuala Lumpur";
        }

        @Override
        public int getAppVersion() {
            return 2631;
        }
    }
}
