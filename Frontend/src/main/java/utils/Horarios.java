package utils;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public final class Horarios {

    public static final int HORA_INICIO = 7;
    public static final int HORA_FIN = 21;

    private Horarios() {}

    public static List<LocalTime> horasDelDia() {
        List<LocalTime> horas = new ArrayList<>();
        for (int h = HORA_INICIO; h <= HORA_FIN; h++) {
            horas.add(LocalTime.of(h, 0));
        }
        return horas;
    }
}
