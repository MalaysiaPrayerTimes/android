package com.i906.mpt.api;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.i906.mpt.model.PrayerData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class PrayerDataTypeAdapter extends TypeAdapter<PrayerData> {

    @Override
    public PrayerData read(JsonReader in) throws IOException {
        final PrayerData pd = new PrayerData();

        in.beginObject();

        while (in.hasNext()) {
            switch (in.nextName()) {
                case "code":
                    pd.setCode(in.nextString());
                    break;
                case "origin":
                    pd.setOrigin(in.nextString());
                    break;
                case "jakim":
                    pd.setJakimCode(in.nextString());
                    break;
                case "source":
                    pd.setSource(in.nextString());
                    break;
                case "readableDate":
                    pd.setReadableDate(in.nextString());
                    break;
                case "lastModified":
                    pd.setLastModified(in.nextString());
                    break;
                case "place":
                    pd.setLocation(in.nextString());
                    break;
                case "times":
                    final List<List<Date>> lld = new ArrayList<>();
                    in.beginArray();

                    while (in.hasNext()) {
                        final List<Date> ld = new ArrayList<>();
                        in.beginArray();

                        while (in.hasNext()) {
                            final Date d = new Date(in.nextLong() * 1000);
                            ld.add(d);
                        }

                        in.endArray();

                        if (ld.size() == 6) {
                            Date subuh = ld.get(0);
                            Date syuruk = ld.get(1);
                            long subuhR = subuh.getTime();
                            long syurukR = syuruk.getTime();
                            Date imsak = new Date(subuhR - 10 * 60 * 1000);
                            Date dhuha = new Date(syurukR + ((syurukR - subuhR) / 3));

                            ld.add(0, imsak);
                            ld.add(3, dhuha);
                        }

                        lld.add(ld);
                    }

                    in.endArray();
                    pd.setPrayerTimes(lld);
                    break;
            }
        }

        in.endObject();

        return pd;
    }

    @Override
    public void write(JsonWriter out, PrayerData value) throws IOException {
        // no need to serialize prayer data
    }
}
