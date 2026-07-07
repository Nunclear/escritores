# Ejecución Local del Backend

## Requisitos Previos

### Software Requerido

| Software | Versión | Propósito |
|---|---|---|
| **Java JDK** | 21 o superior | Compilación y ejecución |
| **Maven** | 3.8+ | Gestor de dependencias |
| **MySQL** | 8.0+ | Base de datos |
| **Git** | Cualquier versión | Control de versiones |

### Verificar Instalación

```bash
# Java
java -version
# Debe mostrar: java version "21" o superior

# Maven
mvn -version
# Debe mostrar: Apache Maven 3.8+

# MySQL (si está instalado localmente)
mysql --version
# Debe mostrar: mysql Ver 8.0+
```

## Configuración Inicial

### 1. Clonar el Repositorio

```bash
git clone https://github.com/Nunclear/escritores.git
cd escritores
```

### 2. Instalar Dependencias

Maven descarga automáticamente las dependencias. Para pre-descargarlas:

```bash
mvn dependency:resolve
```

Para ver el árbol de dependencias:

```bash
mvn dependency:tree
```

### 3. Configurar Base de Datos

#### Opción A: Usar AWS RDS (como en producción)

Base de datos ya está configurada en `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://sg-rds-mysql.clx2xb6nogbs.us-east-1.rds.amazonaws.com/historias_db
spring.datasource.username=admin12
spring.datasource.password=Nunclear55
```

> **Nota**: Verificar que tengas acceso a RDS desde tu máquina. Si es desarrollo local, considera usar una BD local.

#### Opción B: Usar MySQL Local

**1. Instalar MySQL Community Server**

```bash
# macOS (con Homebrew)
brew install mysql

# Ubuntu/Debian
sudo apt-get install mysql-server

# Windows
# Descargar desde https://dev.mysql.com/downloads/mysql/
```

**2. Iniciar MySQL**

```bash
# macOS/Linux
mysql.server start

# Windows
# Usar el servicio de Windows o MySQL Command Line Client
```

**3. Crear base de datos**

```bash
mysql -u root -p

# En el prompt de MySQL:
CREATE DATABASE historias_db;
CREATE USER 'admin12'@'localhost' IDENTIFIED BY 'Nunclear55';
GRANT ALL PRIVILEGES ON historias_db.* TO 'admin12'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

**4. Actualizar `application.properties`**

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/historias_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=admin12
spring.datasource.password=Nunclear55
```

### 4. Configuración de IDE (Opcional)

#### IntelliJ IDEA

1. Abrir proyecto: `File` → `Open` → Seleccionar carpeta `escritores`
2. Configurar JDK: `Project Structure` → `SDK` → Seleccionar Java 21
3. Enable Maven: `View` → `Tool Windows` → `Maven`

#### VS Code

1. Instalar extensiones:
   - Extension Pack for Java
   - Spring Boot Extension Pack
   - Maven for Java

2. Crear `.vscode/settings.json`:

```json
{
    "java.configuration.runtimes": [
        {
            "name": "JavaSE-21",
            "path": "/path/to/java21"
        }
    ]
}
```

## Ejecutar la Aplicación

### Opción 1: Usando Maven (Recomendado)

```bash
# Compilar
mvn clean compile

# Compilar y ejecutar
mvn spring-boot:run

# Compilar con tests
mvn clean compile test

# Compilar, tests y crear JAR
mvn clean package
```

**Output esperado**:

```
[INFO] Scanning for projects...
[INFO] --< com.nunclear.escritores:escritores >--
[INFO] Building escritores 0.0.1-SNAPSHOT
...
[INFO] Building jar: /path/to/escritores/target/escritores-0.0.1-SNAPSHOT.jar
...
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| |_| | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v4.0.6)

2026-07-07 10:30:00.000  INFO 12345 --- [  main] com.nunclear.escritores.EscritoresApplication : Starting EscritoresApplication...
...
2026-07-07 10:30:05.000  INFO 12345 --- [  main] com.nunclear.escritores.EscritoresApplication : Started EscritoresApplication in 5.123 seconds (JVM running for 5.678)
```

### Opción 2: Ejecutar JAR Compilado

```bash
# Compilar primero
mvn clean package -DskipTests

# Ejecutar JAR
java -jar target/escritores-0.0.1-SNAPSHOT.jar

# Ejecutar con puerto diferente
java -jar target/escritores-0.0.1-SNAPSHOT.jar --server.port=8081

# Ejecutar con perfil específico
java -jar target/escritores-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

### Opción 3: Ejecutar desde IDE

**IntelliJ IDEA**:
1. Click derecho en `EscritoresApplication.java`
2. Seleccionar `Run 'EscritoresApplication'`
3. O presionar `Shift + F10`

**VS Code**:
1. Presionar `Ctrl + F5` para ejecutar
2. Seleccionar `Spring Boot App`

## Verificar que Funciona

### 1. Probar Endpoint Base

```bash
# En otra terminal
curl http://localhost:8080/swagger-ui.html
# Debe abrir la página de Swagger

# O probando un endpoint público
curl http://localhost:8080/stories
# Debe devolver un JSON con historias
```

### 2. Ver Logs

**Live Logs en IDE**:
- IntelliJ: `Run` window muestra logs en tiempo real
- VS Code: `Terminal` window muestra logs

**Seguir logs en terminal**:

```bash
# Si ejecutas desde Maven
# Los logs aparecen en la misma terminal

# Si ejecutas JAR
java -jar target/escritores-0.0.1-SNAPSHOT.jar | tee app.log
```

### 3. Acceder a Documentación Swagger

```
http://localhost:8080/swagger-ui.html
```

Swagger UI permite:
- Ver todos los endpoints
- Probar endpoints interactivamente
- Ver esquemas de request/response
- Descargar especificación OpenAPI

### 4. Verificar Conexión a BD

Se debe ver en logs:

```
HikariPool-1 - Starting...
HikariPool-1 - Pool initialized with 10 connections
```

Si hay error, verificar:

```bash
# Probar conexión MySQL
mysql -h sg-rds-mysql.clx2xb6nogbs.us-east-1.rds.amazonaws.com -u admin12 -p historias_db

# O si es local:
mysql -h localhost -u admin12 -p historias_db
```

## Debugging

### Habilitar Debug

```bash
# Opción 1: Usar Maven con debug remoto
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005"

# Opción 2: Ejecutar JAR con debug
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5005 -jar target/escritores-0.0.1-SNAPSHOT.jar
```

### Puntos de Corte (Breakpoints)

**IntelliJ IDEA**:
1. Click en el margen izquierdo de línea de código para marcar breakpoint
2. Ejecutar en modo Debug (Shift + F9)
3. Usar Step Over (F10), Step Into (F11), etc.

### Aumentar Verbosidad de Logs

Crear `application-dev.properties`:

```properties
# Logs detallados
logging.level.root=DEBUG
logging.level.com.nunclear.escritores=DEBUG
logging.level.org.springframework.security=DEBUG
logging.level.org.springframework.web=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE

# Mostrar SQL
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

Ejecutar con perfil dev:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

## Problemas Comunes

### "Connection refused" (Base de datos no accesible)

```
Error: connect ECONNREFUSED 127.0.0.1:3306
```

**Soluciones**:
1. Verificar que MySQL está corriendo: `mysql.server status`
2. Verificar credentials en `application.properties`
3. Verificar URL de conexión (host, puerto, nombre BD)
4. Si usa AWS RDS, verificar conectividad de red

### "Cannot find symbol"

```
[ERROR] cannot find symbol
```

**Soluciones**:
1. Ejecutar `mvn clean` para limpiar build
2. Ejecutar `mvn dependency:resolve` para descargar dependencias
3. Recargar proyecto en IDE
4. Verificar versión de Java: `java -version`

### "Port 8080 already in use"

```
Caused by: java.net.BindException: Address already in use
```

**Soluciones**:
1. Cambiar puerto: `--server.port=8081`
2. Matar proceso en puerto 8080:
   - macOS/Linux: `lsof -ti:8080 | xargs kill -9`
   - Windows: `netstat -ano | findstr :8080` luego `taskkill /PID <pid> /F`

### "No such file or directory"

```
Could not find or load main class
```

**Soluciones**:
1. Verificar Java está en PATH: `echo $JAVA_HOME`
2. Compilar primero: `mvn clean compile`
3. Usar ruta completa a Java

## Desarrollo Productivo

### Hot Reload (DevTools)

Spring Boot DevTools permite recargar cambios automáticamente:

```bash
# Ya está incluido en pom.xml
# Simplemente ejecuta:
mvn spring-boot:run

# Los cambios en archivos Java/resources se recargan automáticamente
```

### Crear Nuevo Controlador

```java
// src/main/java/com/nunclear/escritores/controller/MyController.java
@RestController
@RequestMapping("/my-endpoint")
@RequiredArgsConstructor
public class MyController {
    
    private final MyService myService;
    
    @GetMapping
    public ResponseEntity<List<MyDto>> getAll() {
        return ResponseEntity.ok(myService.getAll());
    }
}
```

Con DevTools, recarga automáticamente el cambio.

### Testing Local

```bash
# Ejecutar tests
mvn test

# Ejecutar test específico
mvn test -Dtest=StoryServiceTest

# Ejecutar tests sin salida
mvn test -q

# Generar reporte de cobertura
mvn jacoco:report
# Abrir: target/site/jacoco/index.html
```

## Parar la Aplicación

```bash
# Presionar Ctrl + C en terminal

# O si ejecutas desde IDE:
# Click en botón "Stop" en la ventana de Run
```

