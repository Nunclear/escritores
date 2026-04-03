# Guía de Configuración - Escritores

## Requisitos del Sistema

### Versiones Recomendadas
- **Java**: 21 o superior
- **Maven**: 3.8.1 o superior
- **MySQL**: 8.0 o superior
- **Git**: 2.30 o superior (opcional)

### Instalación de Requisitos

#### En Windows

1. **Descargar e Instalar Java 21**
   - Descargar desde: https://www.oracle.com/java/technologies/downloads/
   - Instalar y agregar a PATH

2. **Descargar e Instalar Maven**
   - Descargar desde: https://maven.apache.org/download.cgi
   - Extraer y agregar bin a PATH
   - Verificar: `mvn -version`

3. **Instalar MySQL**
   - Descargar desde: https://dev.mysql.com/downloads/mysql/
   - Instalar con MySQL Workbench o MySQL Shell
   - Crear usuario root si no existe

#### En macOS

```bash
# Instalar con Homebrew
brew install openjdk@21
brew install maven
brew install mysql

# Iniciar MySQL
brew services start mysql
```

#### En Linux (Ubuntu/Debian)

```bash
# Actualizar paquetes
sudo apt update

# Instalar Java 21
sudo apt install openjdk-21-jdk

# Instalar Maven
sudo apt install maven

# Instalar MySQL
sudo apt install mysql-server

# Iniciar MySQL
sudo systemctl start mysql
```

---

## Configuración de la Base de Datos

### 1. Acceder a MySQL

```bash
mysql -u root -p
```

Ingresa tu contraseña de MySQL.

### 2. Crear la Base de Datos

```sql
CREATE DATABASE historias_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'app_user'@'localhost' IDENTIFIED BY 'app_password123';
GRANT ALL PRIVILEGES ON historias_db.* TO 'app_user'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

### 3. Importar el Script de Inicialización

```bash
mysql -u root -p historias_db < scripts/Base-de-datos-ed8wD.sql
```

### 4. Cargar Datos de Prueba (Opcional)

```bash
mysql -u root -p historias_db < scripts/test-data.sql
```

---

## Configuración del Proyecto

### 1. Clonar o Descargar el Proyecto

```bash
# Si tienes Git
git clone <repository-url>
cd escritores

# O descargar el ZIP y extraerlo
unzip escritores.zip
cd escritores
```

### 2. Actualizar application.properties

Editar `src/main/resources/application.properties`:

```properties
# Database Configuration - MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/historias_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=app_user
spring.datasource.password=app_password123

# Ajustar logging si necesario
logging.level.root=INFO
logging.level.com.nunclear.escritores=DEBUG
```

### 3. Compilar el Proyecto

```bash
mvn clean compile
```

Este paso descargará todas las dependencias necesarias.

### 4. Empaquetar la Aplicación

```bash
mvn clean package
```

Esto creará un archivo JAR en `target/escritores-0.0.1-SNAPSHOT.jar`

---

## Ejecución de la Aplicación

### Opción 1: Ejecutar con Maven

```bash
mvn spring-boot:run
```

### Opción 2: Ejecutar JAR directamente

```bash
java -jar target/escritores-0.0.1-SNAPSHOT.jar
```

### Opción 3: Ejecutar con variables de entorno

```bash
java -Dspring.datasource.username=app_user \
     -Dspring.datasource.password=app_password123 \
     -jar target/escritores-0.0.1-SNAPSHOT.jar
```

---

## Verificar que la Aplicación Funciona

### 1. Acceder a la Aplicación

```
http://localhost:8080
```

### 2. Verificar Endpoints Básicos

```bash
# Listar todos los usuarios
curl -X GET http://localhost:8080/api/users

# Listar todas las historias
curl -X GET http://localhost:8080/api/stories

# Crear un usuario
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "loginName": "testuser",
    "emailAddress": "test@ejemplo.com",
    "displayName": "Usuario de Prueba",
    "password": "test123456"
  }'
```

---

## Solución de Problemas

### Problema: "Port 8080 is already in use"

**Solución 1**: Cambiar el puerto en `application.properties`
```properties
server.port=8081
```

**Solución 2**: Matar el proceso que usa el puerto (Windows)
```bash
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

**Solución 2**: Matar el proceso (Linux/Mac)
```bash
lsof -i :8080
kill -9 <PID>
```

### Problema: "Access denied for user 'root'@'localhost'"

**Causa**: Contraseña de MySQL incorrecta

**Solución**: 
1. Verificar la contraseña de MySQL
2. Actualizar en `application.properties`
3. Restablecer contraseña si es necesario (ver documentación de MySQL)

### Problema: "No database selected"

**Causa**: Base de datos no existe o nombre incorrecto

**Solución**:
1. Verificar que la base de datos existe: `SHOW DATABASES;`
2. Si no existe, crear con: `CREATE DATABASE historias_db;`
3. Importar el script de inicialización

### Problema: "ClassNotFoundException"

**Causa**: Dependencias no descargadas correctamente

**Solución**:
```bash
mvn clean install -DskipTests
```

### Problema: "Hibernate can't find entities"

**Causa**: Entidades no son detectadas por Spring

**Solución**: Asegúrate que las clases entity estén en el paquete correcto:
```
com.nunclear.escritores.entity.*
```

---

## Configuración para Desarrollo

### IDE Configuration (IntelliJ IDEA)

1. **Abrir Proyecto**
   - File → Open
   - Seleccionar carpeta `escritores`

2. **Configurar JDK**
   - File → Project Structure → Project
   - Seleccionar JDK 21

3. **Instalar Plugins Recomendados**
   - Spring Boot
   - Lombok
   - Database Tools

### IDE Configuration (Eclipse)

1. **Importar Proyecto**
   - File → Import → Existing Maven Projects
   - Seleccionar carpeta `escritores`

2. **Configurar Java**
   - Project → Properties → Java Compiler
   - Seleccionar JDK 21

3. **Instalar m2e**
   - Help → Eclipse Marketplace
   - Buscar "m2e" e instalar

### IDE Configuration (VS Code)

1. **Instalar Extensiones**
   - Extension Pack for Java
   - Spring Boot Dashboard
   - Lombok Annotations Support

2. **Abrir carpeta del proyecto**
   - File → Open Folder

---

## Desarrollo Local

### Ejecutar Tests

```bash
mvn test
```

### Ejecutar con Hot Reload

Agregar a `pom.xml`:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <scope>runtime</scope>
    <optional>true</optional>
</dependency>
```

Los cambios en el código se recargarán automáticamente.

### Debugging

En Maven:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--debug"
```

En IDE, colocar breakpoints y usar "Debug as Java Application"

---

## Configuración para Producción

### 1. Actualizar Configuración

```properties
# Cambiar modo de Hibernate
spring.jpa.hibernate.ddl-auto=validate

# Cambiar nivel de logs
logging.level.root=WARN
logging.level.com.nunclear.escritores=INFO

# Habilitar HTTPS
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=tu_contraseña
```

### 2. Usar Base de Datos en Servidor

```properties
spring.datasource.url=jdbc:mysql://tu-servidor-db:3306/historias_db
spring.datasource.username=prod_user
spring.datasource.password=prod_secure_password
```

### 3. Ejecutar en Servidor

```bash
nohup java -jar escritores-0.0.1-SNAPSHOT.jar &
```

---

## Monitoreo

### Ver Logs

```bash
tail -f nohup.out
```

### Verificar Estado de la Aplicación

```bash
curl http://localhost:8080/api/users
```

---

## Mantenimiento

### Respaldar Base de Datos

```bash
mysqldump -u app_user -p historias_db > backup_$(date +%Y%m%d).sql
```

### Restaurar Base de Datos

```bash
mysql -u app_user -p historias_db < backup_20240402.sql
```

---

## Recursos Adicionales

- **Documentación Oficial**: https://spring.io/projects/spring-boot
- **MySQL Docs**: https://dev.mysql.com/doc/
- **Maven Docs**: https://maven.apache.org/guides/
- **Java 21 Docs**: https://docs.oracle.com/en/java/javase/21/

---

**Última actualización**: Abril 2024  
**Versión**: 0.0.1
