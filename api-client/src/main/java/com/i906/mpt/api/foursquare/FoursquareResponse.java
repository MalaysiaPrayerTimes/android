package com.i906.mpt.api.foursquare;

import java.util.List;

class FoursquareResponse {

    private Meta meta;
    private Response response;

    public List<Mosque> getMosques() {
        return response.venues;
    }

    private static class Meta {
        int code;
    }

    private static class Response {
        List<Mosque> venues;
    }
}
