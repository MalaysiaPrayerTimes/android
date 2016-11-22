package com.i906.mpt.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.i906.mpt.R;
import com.i906.mpt.analytics.AnalyticsProvider;
import com.i906.mpt.common.BaseActivity;
import com.i906.mpt.prefs.CommonPreferences;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author Noorzaini Ilhami
 */
public class DonateActivity extends BaseActivity {

    @Inject
    AnalyticsProvider mAnalyticsProvider;

    @Inject
    CommonPreferences mPreferences;

    @BindView(R.id.tv_mbb)
    TextView mCodeView;

    @BindView(R.id.btn_donate)
    Button mDonateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);
        activityGraph().inject(this);

        if (mPreferences.isGenerousUser()) {
            mCodeView.setVisibility(View.VISIBLE);
            mDonateButton.setEnabled(false);
        }

        mAnalyticsProvider.trackViewedScreen(AnalyticsProvider.SCREEN_DONATION);
    }

    @OnClick(R.id.btn_share)
    void onShareButtonClicked() {
        mPreferences.setAsGoodUser();
        startActivity(Intent.createChooser(createShareIntent(), getResources()
                .getText(R.string.donate_share_via)));
    }

    @OnClick(R.id.btn_digitalocean)
    void onDigitalOceanButtonClicked() {
        mPreferences.setAsDeveloperUser();
        startActivity(createLinkIntent("https://m.do.co/c/7d5155e97d5c"));
    }

    @OnClick(R.id.btn_github)
    void onGithubButtonClicked() {
        mPreferences.setAsDeveloperUser();
        startActivity(createLinkIntent("https://github.com/MalaysiaPrayerTimes"));
    }

    @OnClick(R.id.btn_donate)
    void onDonateButtonClicked() {
        mPreferences.setAsGenerousUser();
        mDonateButton.setEnabled(false);
        mCodeView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void applyTheme() {
        if (graph().getInterfacePreferences().isLightTheme()) {
            setTheme(R.style.MptTheme_Light_Settings);
        }
    }

    private Intent createLinkIntent(String link) {
        return new Intent(Intent.ACTION_VIEW, Uri.parse(link));
    }

    private Intent createShareIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.donate_share_subject));
        intent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.i906.mpt");
        intent.setType("text/plain");
        intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        intent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
        return intent;
    }
}
