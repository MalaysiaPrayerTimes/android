package com.i906.mpt.api.foursquare;

import java.util.List;

public class FoursquareResponse {

    protected Meta meta;
    protected Response response;

    public List<Mosque> getMosques() {
        return response.venues;
    }

    public static class Meta {
        int code;
    }

    public static class Response {
        List<Mosque> venues;
    }
}
