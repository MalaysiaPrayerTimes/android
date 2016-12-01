package com.i906.mpt.api.prayer;

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
                case "provider":
                    pd.setProvider(in.nextString());
                    break;
                case "provider_code":
                    pd.setProviderCode(in.nextString());
                    break;
                case "code":
                    pd.setCode(in.nextString());
                    break;
                case "year":
                    pd.setYear(in.nextInt());
                    break;
                case "month":
                    pd.setMonth(in.nextInt());
                    break;
                case "place":
                    pd.setLocation(in.nextString());
                    break;
                case "attributes":
                    in.beginObject();

                    while (in.hasNext()) {
                        switch (in.nextName()) {
                            case "jakim_code":
                                in.nextString();
                                break;
                            case "jakim_source":
                                in.nextString();
                                break;
                            default:
                                in.nextString();
                        }
                    }

                    in.endObject();
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
        out.beginObject();
        out.name("provider").value(value.getProvider());
        out.name("provider_code").value(value.getProviderCode());
        out.name("code").value(value.getCode());
        out.name("year").value(value.getYear());
        out.name("month").value(value.getMonth());
        out.name("place").value(value.getLocation());
        out.name("times");

        out.beginArray();

        List<List<Date>> monthData = value.getPrayerTimes();

        for (List<Date> dayData : monthData) {
            out.beginArray();

            for (Date date : dayData) {
                out.value(date.getTime() / 1000);
            }

            out.endArray();
        }

        out.endArray();
        out.endObject();
    }
}
