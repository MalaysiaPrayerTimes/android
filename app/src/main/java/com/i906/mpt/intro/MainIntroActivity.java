package com.i906.mpt.intro;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;
import com.i906.mpt.R;
import com.i906.mpt.alarm.StartupReceiver;
import com.i906.mpt.internal.ActivityModule;
import com.i906.mpt.internal.Dagger;

import javax.inject.Inject;

/**
 * @author Noorzaini Ilhami
 */
public class MainIntroActivity extends IntroActivity implements IntroView {

    @Inject
    IntroPresenter mIntroPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Dagger.getGraph(this)
                .activityGraph(new ActivityModule(this))
                .inject(this);

        setButtonBackVisible(false);
        setButtonNextVisible(false);
        setButtonCtaVisible(true);

        addSlide(new SimpleSlide.Builder()
                .layout(R.layout.slide_onboarding)
                .title(R.string.intro_title_prayer)
                .description(R.string.intro_description_prayer)
                .image(R.drawable.intro_prayer)
                .background(R.color.colorPrimary)
                .backgroundDark(R.color.colorPrimaryDark)
                .build());

        addSlide(new SimpleSlide.Builder()
                .layout(R.layout.slide_onboarding)
                .title(R.string.intro_title_mosque)
                .description(R.string.intro_description_mosque)
                .image(R.drawable.intro_mosque)
                .background(R.color.colorPrimary)
                .backgroundDark(R.color.colorPrimaryDark)
                .permissions(new String[] {
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                })
                .build());

        addSlide(new SimpleSlide.Builder()
                .layout(R.layout.slide_onboarding)
                .title(R.string.intro_title_qibla)
                .description(R.string.intro_description_qibla)
                .image(R.drawable.intro_qibla)
                .background(R.color.colorPrimary)
                .backgroundDark(R.color.colorPrimaryDark)
                .build());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mIntroPresenter.refreshPrayerContext();
        StartupReceiver.startPrayerService(this);
    }
}
