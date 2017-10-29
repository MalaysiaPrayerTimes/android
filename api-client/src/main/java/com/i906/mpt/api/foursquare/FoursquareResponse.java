package com.i906.mpt.api.foursquare;

import java.util.List;

class FoursquareResponse {

    Meta meta;
    Response response;

    public List<Mosque> getMosques() {
        return response.venues;
    }

    static class Meta {
        int code;
        String errorType;
        String errorMessage;
    }

    static class Response {
        List<Mosque> venues;
    }
}
