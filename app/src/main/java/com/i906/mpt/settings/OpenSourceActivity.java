package com.i906.mpt.settings;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.i906.mpt.R;
import com.i906.mpt.analytics.AnalyticsProvider;
import com.i906.mpt.common.BaseActivity;
import com.i906.mpt.internal.Dagger;

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

        Dagger.getGraph(this)
                .getAnalyticsProvider()
                .trackViewedScreen(AnalyticsProvider.SCREEN_OPEN_SOURCE);
    }

    private void initDataset() {
        mDataset = new ArrayList<>();

        mDataset.add(Licenses.noContent("Android SDK", "Google Inc.", "https://developer.android.com/sdk/terms.html"));
        mDataset.add(Licenses.fromGitHub("JakeWharton/butterknife"));
        mDataset.add(Licenses.fromGitHub("gabrielemariotti/changeloglib", Licenses.LICENSE_APACHE_V2));
        mDataset.add(Licenses.fromGitHub("kizitonwose/colorpreference", Licenses.FILE_NO_EXTENSION));
        mDataset.add(Licenses.fromGitHub("google/dagger"));
        mDataset.add(Licenses.fromGitHub("google/gson", Licenses.FILE_NO_EXTENSION));
        mDataset.add(Licenses.fromGitHub("yshrsmz/LicenseAdapter", Licenses.FILE_NO_EXTENSION));
        mDataset.add(Licenses.fromGitHub("frankiesardo/LinearListView", Licenses.LICENSE_APACHE_V2));
        mDataset.add(Licenses.fromGitHub("HeinrichReimer/material-intro"));
        mDataset.add(Licenses.fromGitHub("square/okhttp"));
        mDataset.add(Licenses.fromGitHub("zsoltk/paperwork", Licenses.FILE_NO_EXTENSION));
        mDataset.add(Licenses.fromGitHub("square/retrofit"));
        mDataset.add(Licenses.fromGitHub("reactivex/rxandroid", Licenses.FILE_NO_EXTENSION));
        mDataset.add(Licenses.fromGitHub("reactivex/rxjava", Licenses.FILE_NO_EXTENSION));
        mDataset.add(Licenses.fromGitHub("pushtorefresh/storio"));
        mDataset.add(Licenses.fromGitHub("JakeWharton/timber"));
        mDataset.add(Licenses.fromGitHub("msarhan/ummalqura-calendar", Licenses.NAME_MIT, Licenses.FILE_NO_EXTENSION));
    }

    @Override
    protected void applyTheme() {
        if (graph().getInterfacePreferences().isLightTheme()) {
            setTheme(R.style.MptTheme_Light_Settings);
        }
    }
}
