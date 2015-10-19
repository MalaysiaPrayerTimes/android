package com.i906.mpt.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.i906.mpt.R;
import com.i906.mpt.fragment.NotificationFragment;

/**
 * Created by Noorzaini Ilhami on 17/10/2015.
 */
public class NotificationActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            if (findViewById(R.id.fragment_container) != null) {
                NotificationFragment ef = new NotificationFragment();

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, ef)
                        .commit();
            }
        }
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, NotificationActivity.class);
        context.startActivity(intent);
    }
}
