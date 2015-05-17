package com.i906.mpt.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;

public class GeocoderHelper {

    private final String[] mExclusion = {"Johor", "Kedah", "Kelantan", "Melaka", "Negeri Sembilan",
            "Pahang", "Perak", "Perlis", "Sabah", "Sarawak", "Selangor", "Terengganu", "Malaysia"};

    protected int mMaxResults = 5;
    protected Geocoder mGeocoder;

    @Inject
    public GeocoderHelper(Context context) {
        mGeocoder = new Geocoder(context);
    }

    public Observable<List<String>> getAddresses(Location location) {
        return Observable.create(subscriber -> {
            try {
                List<String> components = new ArrayList<>();
                List<String> exclude = Arrays.asList(mExclusion);
                List<Address> addresses = mGeocoder.getFromLocation(
                        location.getLatitude(), location.getLongitude(), mMaxResults);

                if (!addresses.isEmpty()) {

                    String a1;
                    String a2;

                    for (Address ad : addresses) {
                        a1 = ad.getLocality();
                        a2 = ad.getAdminArea();

                        if (a1 != null && !components.contains(a1) && !exclude.contains(a1)) {
                            components.add(a1);
                        }

                        if (a2 != null && !components.contains(a2) && !exclude.contains(a2)) {
                            components.add(a2);
                        }
                    }

                    if (!components.isEmpty()) {
                        subscriber.onNext(components);
                        subscriber.onCompleted();
                    } else {
                        subscriber.onError(new EmptyPlaceError());
                    }
                } else {
                    subscriber.onError(new EmptyAddressError());
                }
            } catch (IOException e) {
                subscriber.onError(new GeocoderError(e));
            }
        });
    }

    public void setMaxResults(int maxResults) {
        mMaxResults = maxResults;
    }

    public static class GeocoderError extends RuntimeException {

        public GeocoderError() {
        }

        public GeocoderError(Throwable throwable) {
            super(throwable);
        }
    }

    public static class EmptyPlaceError extends GeocoderError {
    }

    public static class EmptyAddressError extends GeocoderError {
    }
}
