package com.i906.mpt.location;

import com.google.android.gms.common.api.Status;

/**
 * @author Noorzaini Ilhami
 */
public class LocationDisabledException extends RuntimeException {

    private Status mStatus;

    LocationDisabledException() {
    }

    LocationDisabledException(Status status) {
        mStatus = status;
    }

    public boolean hasStatus() {
        return mStatus != null;
    }

    public Status getStatus() {
        return mStatus;
    }
}
