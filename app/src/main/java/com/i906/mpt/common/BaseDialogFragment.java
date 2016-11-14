package com.i906.mpt.common;

import android.support.v4.app.DialogFragment;

import com.i906.mpt.internal.ActivityGraph;

/**
 * @author Noorzaini Ilhami
 */
public abstract class BaseDialogFragment extends DialogFragment {

    protected ActivityGraph activityGraph() {
        return ((BaseActivity) getActivity()).activityGraph();
    }
}
