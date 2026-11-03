package reservas.presentation;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public final class Horarios {
    public static final int HORA_INICIO = 7;
    public static final int HORA_FIN = 21;

    private Horarios() { }

    public static List<LocalTime> horasDelDia() {
        List<LocalTime> horas = new ArrayList<>();
        for (int hora = HORA_INICIO; hora <= HORA_FIN; hora++) horas.add(LocalTime.of(hora, 0));
        return horas;
    }
}
