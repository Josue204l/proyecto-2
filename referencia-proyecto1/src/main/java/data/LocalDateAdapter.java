package data;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import java.time.LocalDate;

public class LocalDateAdapter extends XmlAdapter<String, LocalDate> {

    @Override
    public LocalDate unmarshal(String v) {
        if (v == null || v.isBlank()) return null;
        return LocalDate.parse(v);
    }

    @Override
    public String marshal(LocalDate v) {
        if (v == null) return null;
        return v.toString();
    }
}
