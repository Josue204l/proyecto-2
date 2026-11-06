# Sistema de Reservas
Aplicación de escritorio para reservar recursos de una institución (salas, equipos y similares). El administrador mantiene funcionarios, categorías y recursos. El funcionario crea y cancela sus reservas. Ambos consultan el calendario, las actividades del día y estadísticas, y pueden guardar un reporte en PDF.
El trabajo se divide en tres módulos Maven. `Protocolo` define los datos y los códigos de cada operación. `Backend` escucha en el puerto 5050, atiende a cada cliente en su propio hilo y guarda todo en `datos/reservas.xml`. `Frontend` es la ventana Swing: envía cada pedido y espera la respuesta, y además escucha avisos cuando alguien crea una reserva. La primera vez el servidor carga usuarios de prueba: `admin` / `222` y `111` / `111`.
## Información del curso
- *Universidad Nacional de Costa Rica*
- *Curso:* Programación 3
- *Ciclo:* II 2026
- *Profesor:* José Sánchez
## Estudiantes
| Nombre | Cédula |
| --- | --- |
| Johan Jafeth Zuñiga Rodríguez | 703330212 |
| Jonathan Josué Rojas Estrada | 1-1907-0744 |
## Cómo ejecutar
Requisito: Java 21 y Maven. Desde la carpeta `Proyecto nuevo`, primero el servidor y, en otra terminal, la ventana:
```bash
1. Se ejecuta backend.
2. Se ejecuta frontend
```
También se puede abrir el proyecto en IntelliJ IDEA y ejecutar `reservas.server.Application` y después `reservas.presentation.Application`.
