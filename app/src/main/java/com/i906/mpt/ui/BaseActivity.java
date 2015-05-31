package com.i906.mpt.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.i906.mpt.MptApplication;
import com.i906.mpt.R;
import com.i906.mpt.extension.ExtensionManager;
import com.i906.mpt.provider.MptInterface;
import com.i906.mpt.util.Utils;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.Optional;

public abstract class BaseActivity extends AppCompatActivity {

    @Inject
    protected Utils mUtils;

    @Inject
    protected ExtensionManager mExtensionManager;

    @Inject
    protected MptInterface mPrayerInterface;

    @Optional
    @InjectView(R.id.toolbar)
    protected Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MptApplication.component(this).inject(this);
    }
}
