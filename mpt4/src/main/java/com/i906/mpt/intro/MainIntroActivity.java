package com.i906.mpt.intro;

import android.Manifest;
import android.os.Bundle;

import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;
import com.i906.mpt.R;

/**
 * @author Noorzaini Ilhami
 */
public class MainIntroActivity extends IntroActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setButtonBackVisible(false);
        setButtonNextVisible(false);
        setButtonCtaVisible(true);

        addSlide(new SimpleSlide.Builder()
                .title(R.string.intro_title_prayer)
                .description(R.string.intro_description_prayer)
                .image(R.drawable.drawing)
                .background(R.color.colorPrimary)
                .backgroundDark(R.color.colorPrimaryDark)
                .build());

        addSlide(new SimpleSlide.Builder()
                .title(R.string.intro_title_mosque)
                .description(R.string.intro_description_mosque)
                .image(R.drawable.drawing)
                .background(R.color.colorPrimary)
                .backgroundDark(R.color.colorPrimaryDark)
                .permissions(new String[] {
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                })
                .build());

        addSlide(new SimpleSlide.Builder()
                .title(R.string.intro_title_qibla)
                .description(R.string.intro_description_qibla)
                .image(R.drawable.drawing)
                .background(R.color.colorPrimary)
                .backgroundDark(R.color.colorPrimaryDark)
                .build());
    }
}
