package com.i906.mpt.api.prayer;

import java.util.List;

/**
 * @author Noorzaini Ilhami
 */
@SuppressWarnings("checkstyle:membername")
class CodeResponse {

    private Wrapper data;

    public List<PrayerCode> getPrayerCodes() {
        return data.supported_codes;
    }

    private static final class Wrapper {
        List<PrayerCode> supported_codes;
    }
}
