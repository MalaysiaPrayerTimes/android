package com.i906.mpt.api.prayer;

/**
 * @author Noorzaini Ilhami
 */
public class UnknownPlaceCodeException extends PrayerException {

    public UnknownPlaceCodeException(String s) {
        super(s);
    }

    public UnknownPlaceCodeException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
