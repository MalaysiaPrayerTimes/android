package com.i906.mpt.model;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PrayerDataTypeAdapter extends TypeAdapter<PrayerData> {

    @Override
    public PrayerData read(JsonReader in) throws IOException {
        final PrayerData pd = new PrayerData();

        in.beginObject();

        while (in.hasNext()) {
            switch (in.nextName()) {
                case "code":
                    pd.code = in.nextString();
                    break;
                case "origin":
                    pd.origin = in.nextString();
                    break;
                case "jakim":
                    pd.jakim = in.nextString();
                    break;
                case "source":
                    pd.source = in.nextString();
                    break;
                case "readableDate":
                    pd.readableDate = in.nextString();
                    break;
                case "lastModified":
                    pd.lastModified = in.nextString();
                    break;
                case "place":
                    pd.place = in.nextString();
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
                    pd.times = lld;
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
