# Errores Comunes y Soluciones

## Errores de Conexión a Base de Datos

### Error: "Communications link failure"

**Mensaje**:
```
Caused by: java.sql.SQLRecoverableException: IO Error: Communications link failure
```

**Causas comunes**:
1. MySQL no está corriendo
2. Credenciales incorrectas
3. Host o puerto incorrecto
4. Firewall bloqueando conexión
5. BD no existe

**Soluciones**:

```bash
# 1. Verificar que MySQL está corriendo
mysql -u admin12 -p
# Si funciona, la BD está accesible

# 2. Probar conexión con los parámetros
mysql -h sg-rds-mysql.clx2xb6nogbs.us-east-1.rds.amazonaws.com \
      -u admin12 -p historias_db

# 3. Verificar que la BD existe
SHOW DATABASES;

# 4. Verificar usuario y permisos
SHOW GRANTS FOR 'admin12'@'%';
```

### Error: "Unknown database 'historias_db'"

**Mensaje**:
```
Unknown database 'historias_db'
```

**Solución**:
```sql
-- Crear la base de datos
CREATE DATABASE historias_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
GRANT ALL PRIVILEGES ON historias_db.* TO 'admin12'@'%';
FLUSH PRIVILEGES;
```

### Error: "Access denied for user"

**Mensaje**:
```
Access denied for user 'admin12'@'localhost': using password: YES
```

**Soluciones**:

```bash
# 1. Verificar contraseña
mysql -u admin12 -p historias_db
# Ingresar la contraseña

# 2. Reset de contraseña (si lo olvidaste)
sudo mysql -u root
mysql> ALTER USER 'admin12'@'%' IDENTIFIED BY 'NuevaContraseña123!';
mysql> FLUSH PRIVILEGES;
mysql> EXIT;

# 3. Verificar permisos
mysql -u root -p
mysql> GRANT ALL PRIVILEGES ON historias_db.* TO 'admin12'@'%';
mysql> FLUSH PRIVILEGES;
```

## Errores de Compilación

### Error: "Cannot find symbol"

**Mensaje**:
```
[ERROR] cannot find symbol
```

**Causas comunes**:
1. Dependencias no descargadas
2. Java version incorrecta
3. Imports faltantes

**Soluciones**:

```bash
# 1. Limpiar y re-descargar dependencias
mvn clean dependency:resolve

# 2. Verificar versión de Java
java -version
# Debe ser Java 21 o superior

# 3. Si el error persiste, limpiar completamente
mvn clean install -U

# 4. Recargar en IDE (si lo usas)
# IntelliJ: File → Invalidate Caches
# VS Code: Reload Window (Ctrl+Shift+P)
```

### Error: "Package does not exist"

**Mensaje**:
```
[ERROR] package com.nunclear.escritores.service does not exist
```

**Solución**:
```bash
# Compilar primero
mvn clean compile

# Si persiste, verificar que la clase existe
find src -name "*.java" -type f | grep -i servicename
```

### Error: "Method not found"

**Mensaje**:
```
[ERROR] cannot find symbol: method getStoryById(int)
```

**Solución**:
```bash
# 1. Verificar que el método existe en el servicio
grep -n "getStoryById" src/main/java/com/nunclear/escritores/service/StoryService.java

# 2. Si no existe, agregarlo
# 3. Si existe pero no se ve, ejecutar mvn clean compile
```

## Errores de Runtime

### Error: "Port 8080 already in use"

**Mensaje**:
```
Caused by: java.net.BindException: Address already in use: bind
```

**Soluciones**:

```bash
# Opción 1: Cambiar puerto
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"

# O en application.properties:
server.port=8081

# Opción 2: Matar proceso actual
# macOS/Linux
lsof -ti:8080 | xargs kill -9

# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Opción 3: Verificar qué proceso usa el puerto
lsof -i :8080
```

### Error: "No qualifying bean of type found"

**Mensaje**:
```
No qualifying bean of type 'com.nunclear.escritores.repository.StoryRepository' available
```

**Causas**:
1. Clase no está anotada con `@Repository`
2. Clase no está en ruta de escaneo de Spring
3. Hay conflicto de nombres

**Soluciones**:

```java
// 1. Asegurar que el repositorio está anotado
@Repository
public interface StoryRepository extends JpaRepository<Story, Integer> {
    // ...
}

// 2. Verificar que está en el package correcto
// src/main/java/com/nunclear/escritores/repository/

// 3. Si está en package diferente, configurar escaneo
@SpringBootApplication(scanBasePackages = {
    "com.nunclear.escritores",
    "com.otra.ubicacion"
})
```

### Error: "Circular dependency"

**Mensaje**:
```
BeanCurrentlyInCreationException: Error creating bean with name 'serviceA': 
Requested bean is currently in creation
```

**Solución**:
```java
// Opción 1: Usar @Lazy
@Service
public class ServiceA {
    @Lazy
    @Autowired
    private ServiceB serviceB;
}

// Opción 2: Inyectar ApplicationContext
@Service
public class ServiceA {
    @Autowired
    private ApplicationContext context;
    
    public void usarServiceB() {
        ServiceB serviceB = context.getBean(ServiceB.class);
    }
}

// Opción 3: Refactorizar para eliminar circularity
// Crear ServiceC que tenga la lógica compartida
```

## Errores de Validación

### Error: "Validation failed"

**Mensaje**:
```
{
    "timestamp": "2026-07-07T10:30:00",
    "status": 400,
    "errors": {
        "title": "must not be blank"
    }
}
```

**Soluciones**:

```java
// 1. Revisar anotaciones de validación
public record CreateStoryRequest(
    @NotBlank(message = "El título no puede estar vacío")
    String title
) {}

// 2. Validar request antes de procesar
// El framework lo hace automáticamente con @Valid

// 3. Mensajes personalizados en español
@NotBlank(message = "El título es requerido")
@Size(min = 3, max = 255, message = "Título entre 3 y 255 caracteres")
String title;
```

## Errores de Autenticación

### Error: "Invalid token"

**Mensaje**:
```
{
    "status": 401,
    "message": "Invalid token"
}
```

**Soluciones**:

```bash
# 1. Verificar que incluyes el token correcto
curl -H "Authorization: Bearer <tu_token>" http://localhost:8080/auth/me

# 2. Verificar que el token no ha expirado
# Access tokens expiran en 1 hora

# 3. Obtener nuevo token
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "loginOrEmail": "usuario123",
    "password": "password"
  }'
```

### Error: "Unauthorized"

**Mensaje**:
```
{
    "status": 401,
    "message": "No autenticado"
}
```

**Soluciones**:

```bash
# 1. Usar endpoint público sin autenticación
curl http://localhost:8080/stories

# 2. Para endpoints autenticados, incluir token
curl -H "Authorization: Bearer <access_token>" http://localhost:8080/dashboard/my-stories

# 3. Login primero si no tienes token
curl -X POST http://localhost:8080/auth/login ...
```

### Error: "User not found"

**Mensaje**:
```
{
    "status": 401,
    "message": "Usuario no encontrado"
}
```

**Causas**:
1. Token tiene ID de usuario que no existe
2. Usuario fue eliminado
3. Token corrupto

**Soluciones**:
```bash
# 1. Hacer login nuevamente
curl -X POST http://localhost:8080/auth/login ...

# 2. Si el usuario no existe, registrarse
curl -X POST http://localhost:8080/auth/register ...
```

## Errores de Base de Datos

### Error: "Integrity constraint violation"

**Mensaje**:
```
Integrity constraint violation: unique constraint or index violation
```

**Causas**:
1. loginName ya existe
2. emailAddress ya existe
3. Datos duplicados

**Solución**:

```bash
# Verificar registros duplicados
mysql -u admin12 -p historias_db

mysql> SELECT COUNT(*), login_name FROM app_user GROUP BY login_name HAVING COUNT(*) > 1;

# Limpiar duplicados (ejemplo)
DELETE FROM app_user WHERE login_name = 'usuario' AND id != 1;
```

### Error: "Deadlock detected"

**Mensaje**:
```
Deadlock detected. The transaction will be rolled back
```

**Soluciones**:

```java
// 1. Usar @Transactional con timeout
@Service
public class MyService {
    @Transactional(timeout = 30)  // 30 segundos timeout
    public void metodoLargo() {
        // ...
    }
}

// 2. Ordenar acceso a tablas de forma consistente
// Siempre: tabla A → tabla B
// Nunca: B → A luego A → B

// 3. Usar READ_UNCOMMITTED para transacciones de lectura
@Transactional(isolation = Isolation.READ_UNCOMMITTED)
public void lecturaDatos() {
    // ...
}
```

## Errores de Logs

### "Hibernate dialect warning"

**Mensaje**:
```
WARN o.h.d.c.DatabaseMetaData : HHH000094: Persistence layer is using mysql 
dialect which doesn't properly support SEQUENCES
```

**Solución**: Es una advertencia, no error. Ignorar o usar estrategia IDENTITY:

```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Integer id;
```

### "SQL debug logs muy largos"

**Mensaje**: Logs llenos de SQL (en desarrollo)

**Soluciones**:

```properties
# Desactivar en producción
spring.jpa.show-sql=false
logging.level.org.hibernate.SQL=WARN

# O crear application-prod.properties
# Usar spring.profiles.active=prod
```

## Debugging

### Habilitar modo verbose

```bash
# Maven
mvn clean compile -X

# Java
java -XX:+UnlockDiagnosticVMOptions -XX:+LogCompilation -jar app.jar

# Spring Boot
mvn spring-boot:run --debug
```

### Agregar logs temporales

```java
// Usar @Slf4j de Lombok
@Service
@Slf4j
public class MyService {
    public void metodo() {
        log.debug("Valor de variable: {}", variable);
        log.info("Iniciando proceso...");
        log.warn("Advertencia: {}", mensaje);
        log.error("Error: {}", error);
    }
}
```

## Soporte Adicional

Si el error no está aquí:

1. **Revisar logs completos**:
   ```bash
   tail -200 /opt/escritores/logs/app.log
   ```

2. **Buscar en Spring Documentation**:
   - https://spring.io/projects/spring-boot
   - https://spring.io/projects/spring-security

3. **GitHub Issues**:
   - https://github.com/Nunclear/escritores/issues

4. **Stack Overflow**:
   - Tag: spring-boot, spring-security, mysql

