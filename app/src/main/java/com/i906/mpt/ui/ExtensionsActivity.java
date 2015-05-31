package com.i906.mpt.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.i906.mpt.R;
import com.i906.mpt.fragment.ExtensionsFragment;

import butterknife.ButterKnife;

public class ExtensionsActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        ButterKnife.inject(this);

        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            if (findViewById(R.id.fragment_container) != null) {
                ExtensionsFragment ef = new ExtensionsFragment();

                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.fragment_container, ef)
                        .commit();
            }
        }
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, ExtensionsActivity.class);
        context.startActivity(intent);
    }
}
