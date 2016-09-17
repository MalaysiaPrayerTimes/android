package com.i906.mpt.location;

import com.google.android.gms.common.ConnectionResult;

/**
 * @author Noorzaini Ilhami
 */
public class ConnectionException extends RuntimeException {

    private ConnectionResult mConnectionResult;

    public ConnectionException(String message, ConnectionResult result) {
        super(message);
        mConnectionResult = result;
    }

    public ConnectionResult getConnectionResult() {
        return mConnectionResult;
    }
}
