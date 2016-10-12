package com.i906.mpt.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.MenuItem;

/**
 * @author Noorzaini Ilhami
 */
public abstract class BasePreferenceFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
}
