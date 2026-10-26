package data;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import java.time.LocalTime;

public class LocalTimeAdapter extends XmlAdapter<String, LocalTime> {

    @Override
    public LocalTime unmarshal(String v) {
        if (v == null || v.isBlank()) return null;
        return LocalTime.parse(v);
    }

    @Override
    public String marshal(LocalTime v) {
        if (v == null) return null;
        return v.toString();
    }
}
