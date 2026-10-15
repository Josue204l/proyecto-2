# Sistema de Reservas — Proyecto 2

Aplicación Maven en tres módulos: `Protocolo` define el contrato serializable, `Backend` ejecuta sockets TCP y persiste en MySQL, y `Frontend` contiene Swing y el Proxy.

## Preparar y ejecutar

1. Instala MySQL e importa `sistema_reservas.sql` una sola vez.
2. Desde `Reservas`, construye los ejecutables y corre las pruebas con `mvn clean verify`.
3. Configura e inicia el Backend:

```powershell
$env:DB_HOST = "localhost"
$env:DB_PORT = "3306"
$env:DB_NAME = "sistema_reservas"
$env:DB_USER = "root"
$env:DB_PASSWORD = "TU_CLAVE_MYSQL"
$env:SERVER_PORT = "5050"
java -jar .\Backend\target\backend-1.0.0.jar
```

4. En otra terminal, inicia el Frontend:

```powershell
$env:BACKEND_HOST = "localhost"
$env:BACKEND_PORT = "5050"
java -jar .\Frontend\target\frontend-1.0.0.jar
```

Para ejecutar Frontend en otra computadora, cambia `BACKEND_HOST` por la IP de la máquina que ejecuta el Backend y permite el puerto TCP configurado en el firewall.

El SQL de ejemplo crea los accesos `admin` / `222` y `_111` / `111`. Cambia las claves de demostración antes de usar datos reales. No guardes contraseñas de MySQL en archivos versionados.

## Arquitectura

Las solicitudes y respuestas usan `java.io.Serializable`. Un hilo lector del Frontend separa respuestas correlacionadas y alertas asincrónicas; el Backend atiende clientes con workers independientes. El panel lateral de alertas permite ver el detalle y descartar localmente con confirmación.

`mvn -DskipTests package` compila los módulos y el código de pruebas. `mvn verify` ejecuta las pruebas unitarias y de integración. MySQL debe estar configurado en la máquina Backend.
