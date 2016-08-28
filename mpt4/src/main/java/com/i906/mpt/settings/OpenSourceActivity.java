package com.i906.mpt.settings;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.i906.mpt.R;
import com.i906.mpt.common.BaseActivity;

import net.yslibrary.licenseadapter.LicenseAdapter;
import net.yslibrary.licenseadapter.LicenseEntry;
import net.yslibrary.licenseadapter.Licenses;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @author Noorzaini Ilhami
 */
public class OpenSourceActivity extends BaseActivity {

    private List<LicenseEntry> mDataset;

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opensource);

        initDataset();
        LicenseAdapter adapter = new LicenseAdapter(mDataset);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(adapter);
        Licenses.load(mDataset);
    }

    private void initDataset() {
        mDataset = new ArrayList<>();

        mDataset.add(Licenses.noContent("Android SDK", "Google Inc.", "https://developer.android.com/sdk/terms.html"));
        mDataset.add(Licenses.fromGitHub("mcharmas/Android-ReactiveLocation", Licenses.LICENSE_APACHE_V2));
        mDataset.add(Licenses.fromGitHub("JakeWharton/butterknife"));
        mDataset.add(Licenses.fromGitHub("google/dagger"));
        mDataset.add(Licenses.fromGitHub("google/gson", Licenses.FILE_NO_EXTENSION));
        mDataset.add(Licenses.fromGitHub("dlew/joda-time-android", Licenses.FILE_NO_EXTENSION));
        mDataset.add(Licenses.fromGitHub("frankiesardo/LinearListView", Licenses.LICENSE_APACHE_V2));
        mDataset.add(Licenses.fromGitHub("yshrsmz/LicenseAdapter", Licenses.FILE_NO_EXTENSION));
        mDataset.add(Licenses.fromGitHub("HeinrichReimer/material-intro"));
        mDataset.add(Licenses.fromGitHub("square/okhttp"));
        mDataset.add(Licenses.fromGitHub("zsoltk/paperwork", Licenses.FILE_NO_EXTENSION));
        mDataset.add(Licenses.fromGitHub("square/retrofit"));
        mDataset.add(Licenses.fromGitHub("reactivex/rxandroid", Licenses.FILE_NO_EXTENSION));
        mDataset.add(Licenses.fromGitHub("reactivex/rxjava", Licenses.FILE_NO_EXTENSION));
        mDataset.add(Licenses.fromGitHub("pushtorefresh/storio"));
        mDataset.add(Licenses.fromGitHub("JakeWharton/timber"));
    }
}
