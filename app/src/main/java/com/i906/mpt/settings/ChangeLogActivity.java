package com.i906.mpt.settings;

import android.os.Bundle;

import com.i906.mpt.R;
import com.i906.mpt.analytics.AnalyticsProvider;
import com.i906.mpt.common.BaseActivity;

import javax.inject.Inject;

/**
 * @author Noorzaini Ilhami
 */
public class ChangeLogActivity extends BaseActivity {

    @Inject
    AnalyticsProvider mAnalyticsProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_log);
        activityGraph().inject(this);

        mAnalyticsProvider.trackViewedScreen(AnalyticsProvider.SCREEN_CHANGELOG);
    }

    @Override
    protected void applyTheme() {
        if (graph().getInterfacePreferences().isLightTheme()) {
            setTheme(R.style.MptTheme_Light_Settings);
        }
    }
}
