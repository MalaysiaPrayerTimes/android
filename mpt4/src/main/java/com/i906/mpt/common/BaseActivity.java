package com.i906.mpt.common;

import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;

import com.i906.mpt.MptApplication;
import com.i906.mpt.internal.Graph;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }

    protected Graph graph() {
        return MptApplication.graph(this);
    }
}
