package com.i906.mpt.model;

import java.util.List;

public class Mosque {

    protected String id;
    protected String name;
    protected Contact contact;
    protected Location location;

    public static class Contact {
        String phone;
        String formattedPhone;
    }

    public static class Location {
        String address;
        String crossStreet;
        double lat;
        double lng;
        long distance;
        String postalCode;
        String city;
        String state;
        String country;
        List<String> formattedAddress;
    }

    @Override
    public String toString() {
        return String.format("[Mosque name=\"%s\"]", name);
    }
}
