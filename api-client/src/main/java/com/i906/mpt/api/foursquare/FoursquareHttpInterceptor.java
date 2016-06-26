package com.i906.mpt.api.foursquare;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author Noorzaini Ilhami
 */
public class FoursquareHttpInterceptor implements Interceptor {

    private final String FOURSQUARE_CLIENT_ID;
    private final String FOURSQUARE_CLIENT_SECRET;
    private final String FOURSQUARE_API_VERSION = "20160626";

    public FoursquareHttpInterceptor(String id, String secret) {
        FOURSQUARE_CLIENT_ID = id;
        FOURSQUARE_CLIENT_SECRET = secret;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        HttpUrl.Builder u = chain.request()
                .url()
                .newBuilder()
                .addQueryParameter("client_id", FOURSQUARE_CLIENT_ID)
                .addQueryParameter("client_secret", FOURSQUARE_CLIENT_SECRET)
                .addQueryParameter("v", FOURSQUARE_API_VERSION);

        Request r = chain.request()
                .newBuilder()
                .url(u.build())
                .build();

        return chain.proceed(r);
    }
}
