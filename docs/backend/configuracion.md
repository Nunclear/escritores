# Configuración del Backend

## Archivo de Configuración Principal

### `application.properties`

El archivo `src/main/resources/application.properties` contiene toda la configuración de la aplicación.

```properties
# Nombre de la aplicación
spring.application.name=escritores

# Base de datos MySQL en AWS RDS
spring.datasource.url=jdbc:mysql://sg-rds-mysql.clx2xb6nogbs.us-east-1.rds.amazonaws.com/historias_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=admin12
spring.datasource.password=Nunclear55

# Configuración JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.open-in-view=false

# Servidor
server.port=8080
server.address=0.0.0.0

# JWT
app.jwt.secret=MiClaveSuperSecretaParaJwtDebeTenerAlMenos32Caracteres123456
app.jwt.access-expiration-seconds=3600
app.jwt.refresh-expiration-seconds=604800

# Logs opcionales
logging.level.org.springframework.security=DEBUG
```

## Configuración por Secciones

### 1. Base de Datos

```properties
spring.datasource.url=jdbc:mysql://[HOST]/[DATABASE]?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=admin12
spring.datasource.password=Nunclear55
```

**Detalles**:
- **Host**: AWS RDS MySQL
- **Database**: `historias_db`
- **Usuario**: `admin12`
- **Parámetros de conexión**:
  - `useSSL=false`: Conexión sin SSL (en desarrollo)
  - `serverTimezone=UTC`: Zona horaria UTC
  - `allowPublicKeyRetrieval=true`: Permite autenticación de clave pública

**Recomendación**: En producción, usar SSL y credenciales en variables de entorno.

### 2. JPA/Hibernate

```properties
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.open-in-view=false
```

| Propiedad | Valor | Descripción |
|---|---|---|
| **ddl-auto** | `update` | Actualiza esquema automáticamente (cambiar a `validate` en producción) |
| **show-sql** | `true` | Muestra SQL en logs |
| **format_sql** | `true` | Formatea SQL para legibilidad |
| **open-in-view** | `false` | Desactiva sesión abierta en vista (mejor práctica) |

### 3. Servidor

```properties
server.port=8080
server.address=0.0.0.0
```

- **Puerto**: 8080 (configurable)
- **Dirección**: 0.0.0.0 (todas las interfaces)

### 4. JWT (Autenticación)

```properties
app.jwt.secret=MiClaveSuperSecretaParaJwtDebeTenerAlMenos32Caracteres123456
app.jwt.access-expiration-seconds=3600
app.jwt.refresh-expiration-seconds=604800
```

| Propiedad | Valor | Descripción |
|---|---|---|
| **secret** | 32+ caracteres | Clave secreta para firmar JWT |
| **access-expiration-seconds** | 3600 | Access token expira en 1 hora |
| **refresh-expiration-seconds** | 604800 | Refresh token expira en 7 días |

### 5. Logs

```properties
logging.level.org.springframework.security=DEBUG
```

Nivel de log para Spring Security (útil para debugging).

## Clases de Configuración Java

### `SecurityConfig.java`

```java
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        // Configuración de seguridad
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) {
        // Gestor de autenticación
    }
}
```

**Responsabilidades**:
- Deshabilita CSRF
- Habilita CORS
- Sesiones sin estado (Stateless)
- Autorización basada en rutas
- Filtro JWT personalizado
- Codificación BCrypt de contraseñas

### `CorsConfig.java`

```java
@Configuration
public class CorsConfig {
    
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry
                    .addMapping("/**")
                    .allowedOrigins("http://localhost:5173")
                    .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                    .allowedHeaders("*")
                    .allowCredentials(true);
            }
        };
    }
}
```

**CORS Configuration**:
- **Origen permitido**: `http://localhost:5173` (cliente frontend en desarrollo)
- **Métodos**: GET, POST, PUT, PATCH, DELETE, OPTIONS
- **Headers**: Todos (`*`)
- **Credenciales**: Habilitadas

### `OpenApiConfig.java`

```java
@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Escritores API")
                .version("v1")
                .description("Documentación interactiva de la API...")
                .license(new License().name("Apache 2.0")...)
            );
    }
}
```

**Genera documentación Swagger/OpenAPI** accesible en:
- Swagger UI: `/swagger-ui.html`
- OpenAPI JSON: `/v3/api-docs`
- OpenAPI YAML: `/v3/api-docs.yaml`

## Variables de Entorno Recomendadas

Para producción, mover sensibles a variables de entorno:

```bash
# Base de datos
DB_URL=jdbc:mysql://[HOST]/[DATABASE]
DB_USER=admin
DB_PASSWORD=***

# JWT
JWT_SECRET=***
JWT_ACCESS_EXPIRATION=3600
JWT_REFRESH_EXPIRATION=604800

# CORS
ALLOWED_ORIGINS=https://ejemplo.com

# Logs
LOG_LEVEL=INFO
```

## Perfiles de Spring (Profiles)

Es recomendable crear archivos de configuración por perfil:

```
application.properties           # Configuración por defecto
application-dev.properties       # Desarrollo
application-prod.properties      # Producción
application-test.properties      # Testing
```

**Activar perfil**:
```bash
java -jar app.jar --spring.profiles.active=prod
```

### Ejemplo: `application-prod.properties`

```properties
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
logging.level.org.springframework.security=WARN

# Usar variables de entorno
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
app.jwt.secret=${JWT_SECRET}
```

## Rutas Autorizadas sin Token JWT

Definidas en `SecurityConfig.java`:

### Públicas (sin autenticación)
- `/auth/register` - Registro de usuario
- `/auth/login` - Login
- `/auth/refresh` - Refrescar token
- `/auth/forgot-password` - Recuperar contraseña
- `/auth/reset-password` - Resetear contraseña
- `/auth/verify-email` - Verificar email
- `/stories` - Listar historias públicas
- `/stories/{id}` - Obtener historia
- `/chapters/**` - Listar capítulos
- `/comments/**` - Listar comentarios
- `/ratings/**` - Obtener ratings
- `/metrics/**` - Métricas públicas
- Swagger UI: `/swagger-ui/**`, `/v3/api-docs/**`

### Autenticadas (requieren token)
- `/dashboard/**` - Dashboard del usuario
- `/ideas/**` - Gestión de ideas
- `/favorites/**` - Favoritos
- `/follows/**` - Seguimientos
- `/reports/**` - Reportes de contenido
- `/admin/**` - Panel de administración

### Por Rol
- `/stories` (POST) - `@PreAuthorize("hasAnyRole('USER','MODERATOR','ADMIN')")`
- `/admin/**` - Generalmente requiere rol ADMIN

## Parámetros de Conexión MySQL

| Parámetro | Valor | Descripción |
|---|---|---|
| `useSSL` | false (dev), true (prod) | Usar SSL para conexión |
| `serverTimezone` | UTC | Zona horaria del servidor |
| `allowPublicKeyRetrieval` | true | Permitir autenticación con clave pública |
| `autoReconnect` | true | Reconectar automáticamente |
| `cachePrepStmts` | true | Cachear prepared statements |

## Health Check Recomendado

Spring Boot proporciona endpoint de health:

```bash
curl http://localhost:8080/actuator/health
```

**Respuesta esperada**:
```json
{
  "status": "UP"
}
```

## Monitoreo y Métricas

Para habilitar métricas de Actuator, agregar a `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

Y a `application.properties`:

```properties
management.endpoints.web.exposure.include=health,metrics,info
```

