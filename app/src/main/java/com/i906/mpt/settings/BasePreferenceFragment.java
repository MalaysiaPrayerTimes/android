package com.i906.mpt.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.MenuItem;
import android.view.View;

import com.i906.mpt.internal.ActivityGraph;
import com.i906.mpt.internal.ActivityModule;
import com.i906.mpt.internal.Dagger;
import com.i906.mpt.internal.Graph;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author Noorzaini Ilhami
 */
public abstract class BasePreferenceFragment extends PreferenceFragment {

    private Unbinder mUnbinder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUnbinder = ButterKnife.bind(this, view);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            getActivity().finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mUnbinder != null) mUnbinder.unbind();
    }

    protected Graph graph() {
        return Dagger.getGraph(getActivity());
    }

    protected ActivityGraph activityGraph() {
        return graph()
                .activityGraph(new ActivityModule(getActivity()));
    }
}
