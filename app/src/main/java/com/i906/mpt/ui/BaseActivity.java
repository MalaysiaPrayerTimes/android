package com.i906.mpt.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.i906.mpt.MptApplication;
import com.i906.mpt.extension.ExtensionManager;

import javax.inject.Inject;

public abstract class BaseActivity extends ActionBarActivity {

    @Inject
    protected ExtensionManager mExtensionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MptApplication.component(this).inject(this);
    }
}
