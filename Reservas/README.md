# Sistema de Reservas — Proyecto 2

Proyecto Maven con tres módulos: `Protocolo` (mensajes y modelos compartidos), `Backend` (servidor TCP y acceso a MySQL) y `Frontend` (cliente Swing y Proxy).

## Estado

La estructura Maven agregadora, los modelos compartidos, el formato de mensajes JSON por línea, el transporte concurrente del Backend y el cliente lector del Frontend están preparados. El reactor compila con `mvn -DskipTests package`.

La aplicación aún no está lista para ejecutarse de extremo a extremo. Faltan los repositorios y operaciones JDBC, la conexión de las pantallas Swing al Proxy, el panel de alertas y pruebas unitarias/de integración. 