package com.i906.mpt.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import com.i906.mpt.BuildConfig;
import com.i906.mpt.R;
import com.i906.mpt.analytics.AnalyticsProvider;
import com.i906.mpt.common.BaseActivity;
import com.i906.mpt.internal.Dagger;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author Noorzaini Ilhami
 */
public class LogoActivity extends BaseActivity {

    @BindView(R.id.tv_version)
    TextView mVersionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);

        mVersionView.setText(getString(R.string.label_version_value, BuildConfig.VERSION_NAME));

        Dagger.getGraph(this)
                .getAnalyticsProvider()
                .trackViewedScreen(AnalyticsProvider.SCREEN_COPYRIGHT);
    }

    @OnClick(R.id.tv_facebook)
    void onFacebookTextClicked() {
        openFacebook();
    }

    @OnClick(R.id.tv_github)
    void onGithubTextClicked() {
        openGithub();
    }

    private void openGithub() {
        startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://github.com/MalaysiaPrayerTimes")));
    }

    private void openFacebook() {
        startActivity(getFacebookIntent());
    }

    private Intent getFacebookIntent() {
        try {
            getPackageManager().getPackageInfo("com.facebook.katana", 0);
            return new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/369813589710705"));
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.facebook.com/MalaysiaPrayerTimes"));
        }
    }

    @Override
    protected void applyTheme() {
        if (graph().getInterfacePreferences().isLightTheme()) {
            setTheme(R.style.MptTheme_Light_Settings);
        }
    }
}
