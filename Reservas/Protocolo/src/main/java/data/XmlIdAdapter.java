package data;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/**
 * JAXB {@code @XmlID} debe ser un NCName (no puede empezar con dígito).
 * Prefija con '_' solo en el XML; el valor en memoria no cambia.
 */
public class XmlIdAdapter extends XmlAdapter<String, String> {

    @Override
    public String unmarshal(String v) {
        if (v == null || v.isEmpty()) return v;
        if (v.charAt(0) == '_' && v.length() > 1 && Character.isDigit(v.charAt(1))) {
            return v.substring(1);
        }
        return v;
    }

    @Override
    public String marshal(String v) {
        if (v == null || v.isEmpty()) return v;
        if (Character.isDigit(v.charAt(0))) {
            return "_" + v;
        }
        return v;
    }
}
