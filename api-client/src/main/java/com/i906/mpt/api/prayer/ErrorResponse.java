package com.i906.mpt.api.prayer;

/**
 * @author Noorzaini Ilhami
 */
@SuppressWarnings("checkstyle:membername")
class ErrorResponse {

    String message;
    int status_code;
    String provider;

    boolean hasProviderName() {
        return provider != null;
    }
}
