package com.i906.mpt.model;

import java.util.List;

public class Mosque implements Comparable<Mosque> {

    protected String id;
    protected String name;
    protected Contact contact;
    protected Location location;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<String> getAddress() {
        return location.formattedAddress;
    }

    public long getDistance() {
        return location.distance;
    }

    public double getLatitude() {
        return location.lat;
    }

    public double getLongitude() {
        return location.lng;
    }

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
    public int compareTo(Mosque another) {
        return (int) (getDistance() - another.getDistance());
    }

    @Override
    public String toString() {
        return String.format("[Mosque name=\"%s\"]", name);
    }
}
