package com.i906.mpt.location;

/**
 * @author Noorzaini Ilhami
 */
public class ConnectionSuspendedException extends RuntimeException {

    private int mErrorCode;

    public ConnectionSuspendedException(int errorCode) {
        mErrorCode = errorCode;
    }

    public int getErrorCode() {
        return mErrorCode;
    }
}
