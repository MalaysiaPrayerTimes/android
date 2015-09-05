package com.i906.mpt.db;

import android.content.Context;

import com.i906.mpt.R;
import com.i906.mpt.model.PrayerCode;
import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class PrayerCodePopulator {

    private Context mContext;
    private HashMap<String, PrayerCode> mKeyCache;

    public PrayerCodePopulator(Context context) {
        mContext = context;
        mKeyCache = new HashMap<>();
    }

    public List<PrayerCode> getJakimCodes() {
        List<PrayerCode> codes = new ArrayList<>();
        codes.addAll(getDefaultLocations());
        codes.addAll(getExtraLocations());
        return codes;
    }

    private List<PrayerCode> getDefaultLocations() {
        List<PrayerCode> codes = new ArrayList<>();
        InputStream in = mContext.getResources().openRawResource(R.raw.default_locations);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        CSVReader reader = new CSVReader(br);

        try {
            String[] line;
            String code;

            while ((line = reader.readNext()) != null) {
                code = line[3].trim();

                PrayerCode jc = new PrayerCode.Builder()
                        .setCode(code)
                        .setDistrict(line[0].trim())
                        .setPlace(line[1].trim())
                        .setJakimCode(line[2].trim())
                        .build();

                codes.add(jc);
                mKeyCache.put(jc.getCode(), jc);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                /* .. */
            }
        }

        return codes;
    }

    private List<PrayerCode> getExtraLocations() {
        List<PrayerCode> codes = new ArrayList<>();
        InputStream in = mContext.getResources().openRawResource(R.raw.extra_locations);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        CSVReader reader = new CSVReader(br);

        try {
            String[] line;
            String code;
            String origin;
            String duplicateOf;

            while ((line = reader.readNext()) != null) {
                origin = line[1].trim();
                code = line[2].trim();
                duplicateOf = line[3].trim();

                PrayerCode.Builder b = new PrayerCode.Builder()
                        .setCode(code)
                        .setDistrict(getDistrictName(origin))
                        .setPlace(line[0].trim())
                        .setJakimCode(getJakimCode(origin))
                        .setOrigin(origin);

                if (!duplicateOf.isEmpty()) {
                    b.setDuplicateOf(duplicateOf);
                } else {
                    b.setDuplicateOf(null);
                }

                PrayerCode jc = b.build();
                codes.add(jc);
                mKeyCache.put(jc.getCode(), jc);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                /* .. */
            }
        }

        return codes;
    }

    private String getDistrictName(String mptcode) {
        PrayerCode data = getCodeDetails(mptcode);

        if (data != null) return data.getDistrict();
        else return null;
    }

    private PrayerCode getCodeDetails(String code) {
        return mKeyCache.get(code);
    }

    private String getJakimCode(String mptcode) {
        PrayerCode data = getCodeDetails(mptcode);

        if (data != null) return data.getJakimCode();
        else return null;
    }
}
