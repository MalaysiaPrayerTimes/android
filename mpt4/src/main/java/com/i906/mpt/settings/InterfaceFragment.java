package com.i906.mpt.settings;

import android.os.Bundle;

import com.i906.mpt.R;

/**
 * @author Noorzaini Ilhami
 */
public class InterfaceFragment extends BasePreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_interface);
    }
}
