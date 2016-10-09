package com.i906.mpt.location;

/**
 * @author Noorzaini Ilhami
 */
public class PreferredLocation {

    private String code;
    private String name;

    public PreferredLocation(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "PreferredLocation{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
