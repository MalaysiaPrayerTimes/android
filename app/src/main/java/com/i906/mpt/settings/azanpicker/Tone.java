package com.i906.mpt.settings.azanpicker;

/**
 * Created by Noorzaini Ilhami on 19/10/2015.
 */
public class Tone {

    private String name;
    private String uri;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tone tone = (Tone) o;

        return !(uri != null ? !uri.equals(tone.uri) : tone.uri != null);

    }

    @Override
    public int hashCode() {
        return uri != null ? uri.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Tone{" +
                "name='" + name + '\'' +
                ", uri='" + uri + '\'' +
                '}';
    }
}
