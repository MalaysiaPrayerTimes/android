package com.i906.mpt.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.i906.mpt.common.BaseActivity;
import com.i906.mpt.intro.MainIntroActivity;
import com.i906.mpt.prefs.CommonPreferences;

import javax.inject.Inject;

/**
 * @author Noorzaini Ilhami
 */
public class MainActivity extends BaseActivity {

    @Inject
    CommonPreferences mCommonPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        graph().inject(this);

        if (mCommonPreferences.isFirstStart()) {
            showIntro();
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

            }
        }
    }
}
