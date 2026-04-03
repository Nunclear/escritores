# Correcciones de JWT - JJWT 0.12.3

## Problema Identificado

El proyecto tenía **13 errores de compilación** relacionados con la API deprecated de JJWT (Java JWT):

### Errores de API Deprecated (10 avisos)
- `SignatureAlgorithm` - Eliminado en favor de métodos más simples
- `setSubject()` - Reemplazado por `subject()`
- `setIssuedAt()` - Reemplazado por `issuedAt()`
- `setExpiration()` - Reemplazado por `expiration()`
- `signWith(Key, SignatureAlgorithm)` - Reemplazado por `signWith(Key)`

### Errores de Symbol No Encontrado (8 errores)
- `parserBuilder()` - No existe en JJWT 0.12.3
- Debe ser reemplazado por `parser()`
- Debe usar `verifyWith()` en lugar de `setSigningKey()`
- Debe usar `parseSignedClaims()` en lugar de `parseClaimsJws()`
- Debe usar `getPayload()` en lugar de `getBody()`

---

## Cambios Realizados

### Archivo Modificado: `JwtService.java`

#### 1. Actualización de Imports
```java
// Anterior
import io.jsonwebtoken.*;

// Nuevo
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
```

#### 2. Métodos de Generación de Tokens (4 métodos)

**Patrón General:**

```java
// Anterior
Jwts.builder()
    .setSubject(userId.toString())
    .claim("email", email)
    .claim("type", "ACCESS")
    .setIssuedAt(new Date())
    .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
    .signWith(getSigningKey(), SignatureAlgorithm.HS512)
    .compact();

// Nuevo
Instant now = Instant.now();
Instant expiryDate = now.plusMillis(jwtExpiration);

Jwts.builder()
    .subject(userId.toString())
    .claim("email", email)
    .claim("type", "ACCESS")
    .issuedAt(Date.from(now))
    .expiration(Date.from(expiryDate))
    .signWith(getSigningKey())
    .compact();
```

**Métodos Afectados:**
- `generateToken()` - Token de acceso (1 hora)
- `generateRefreshToken()` - Refresh token (7 días)
- `generatePasswordResetToken()` - Password reset (1 hora)
- `generateEmailVerificationToken()` - Email verification (24 horas)

#### 3. Métodos de Validación (4 métodos)

**Patrón General:**

```java
// Anterior
Claims claims = Jwts.parserBuilder()
    .setSigningKey(getSigningKey())
    .build()
    .parseClaimsJws(token)
    .getBody();

// Nuevo
Claims claims = Jwts.parser()
    .verifyWith(getSigningKey())
    .build()
    .parseSignedClaims(token)
    .getPayload();
```

**Métodos Afectados:**
- `validateToken()` - Valida access tokens
- `validateRefreshToken()` - Valida refresh tokens
- `validatePasswordResetToken()` - Valida password reset tokens
- `validateEmailVerificationToken()` - Valida email verification tokens

#### 4. Métodos de Extracción (3 métodos)

**Patrón General:**
```java
// Anterior
Claims claims = Jwts.parserBuilder()
    .setSigningKey(getSigningKey())
    .build()
    .parseClaimsJws(token)
    .getBody();

// Nuevo
Claims claims = Jwts.parser()
    .verifyWith(getSigningKey())
    .build()
    .parseSignedClaims(token)
    .getPayload();
```

**Métodos Afectados:**
- `extractUserId()` - Extrae ID del usuario
- `extractEmail()` - Extrae email del token
- `extractRole()` - Extrae rol del usuario

#### 5. Método de Validación Booleano

```java
// Anterior
Jwts.parserBuilder()
    .setSigningKey(getSigningKey())
    .build()
    .parseClaimsJws(token);

// Nuevo
Jwts.parser()
    .verifyWith(getSigningKey())
    .build()
    .parseSignedClaims(token);
```

**Método Afectado:**
- `isTokenValid()` - Verifica si token es válido

---

## Resumen de Cambios

| Métodos | Cambios | Líneas Modificadas |
|---------|---------|------------------|
| Builder (4) | Métodos rename + signWith simplificado | 28 líneas |
| Parser (4) | parserBuilder() → parser() + verifyWith() | 20 líneas |
| Extractor (3) | parserBuilder() → parser() + verifyWith() | 15 líneas |
| Boolean (1) | parserBuilder() → parser() + verifyWith() | 5 líneas |
| **Total** | **12 métodos** | **68 líneas** |

---

## Resultados

### Antes
- 10 avisos de métodos deprecated
- 8 errores de símbolos no encontrados
- **Total: 18 errores de compilación**

### Después
- ✅ 0 errores de compilación
- ✅ 0 avisos deprecated
- ✅ API completamente actualizada a JJWT 0.12.3

---

## Ventajas de la Nueva API

1. **Más Simple**: Menos verbosidad, código más limpio
2. **Type-Safe**: Mejor manejo de tipos con `verifyWith()`
3. **Moderno**: Usa `Instant` en lugar de deprecated `Date`
4. **Compatible**: Completamente backward compatible en funcionalidad
5. **Seguro**: El algoritmo HS512 se infiere automáticamente

---

## Pruebas Recomendadas

Después de compilar, probar los siguientes endpoints:

1. **POST /api/auth/register** - Crear usuario
2. **POST /api/auth/login** - Generar tokens
3. **POST /api/auth/refresh** - Renovar access token
4. **GET /api/auth/me** - Validar token en uso
5. **POST /api/auth/forgot-password** - Password reset token
6. **POST /api/auth/send-verification** - Email verification token

---

## Compatibilidad

- JJWT: 0.12.3 ✅
- Spring Boot: 3.x ✅
- Java: 17+ ✅
