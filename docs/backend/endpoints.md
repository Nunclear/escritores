# API REST - Endpoints Documentados

## Información General

- **Base URL**: `http://localhost:8080`
- **Versión API**: v1
- **Formato de Respuesta**: JSON
- **Autenticación**: Bearer Token (JWT)
- **Documentación Interactiva**: [Swagger UI](http://localhost:8080/swagger-ui.html)

## Convenciones

### Headers Requeridos

```
Authorization: Bearer <access_token>
Content-Type: application/json
```

### Códigos HTTP

| Código | Significado |
|---|---|
| 200 | OK - Solicitud exitosa |
| 201 | Created - Recurso creado |
| 400 | Bad Request - Entrada inválida |
| 401 | Unauthorized - No autenticado |
| 403 | Forbidden - Sin permisos |
| 404 | Not Found - Recurso no encontrado |
| 409 | Conflict - Conflicto de datos |
| 500 | Server Error - Error del servidor |

## Endpoints de Autenticación

### POST /auth/register
Registra un nuevo usuario

```
POST /auth/register
Content-Type: application/json
```

**Request**:
```json
{
    "loginName": "usuario123",
    "emailAddress": "usuario@ejemplo.com",
    "displayName": "Mi Nombre Completo",
    "password": "MiContraseña123!"
}
```

**Response (201 Created)**:
```json
{
    "id": 1,
    "loginName": "usuario123",
    "emailAddress": "usuario@ejemplo.com",
    "displayName": "Mi Nombre Completo",
    "accessLevel": "user",
    "accountState": "pending_verification",
    "createdAt": "2026-07-07T10:30:00"
}
```

### POST /auth/login
Autentica un usuario y retorna tokens

```
POST /auth/login
Content-Type: application/json
```

**Request**:
```json
{
    "loginOrEmail": "usuario123",
    "password": "MiContraseña123!"
}
```

**Response (200 OK)**:
```json
{
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "550e8400-e29b-41d4-a716-446655440000",
    "tokenType": "Bearer",
    "expiresIn": 3600,
    "user": {
        "id": 1,
        "loginName": "usuario123",
        "displayName": "Mi Nombre Completo",
        "accessLevel": "user"
    }
}
```

### POST /auth/refresh
Refresca el access token usando el refresh token

```
POST /auth/refresh
Content-Type: application/json
```

**Request**:
```json
{
    "refreshToken": "550e8400-e29b-41d4-a716-446655440000"
}
```

**Response (200 OK)**:
```json
{
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "660e8400-e29b-41d4-a716-446655440001",
    "tokenType": "Bearer",
    "expiresIn": 3600
}
```

### POST /auth/logout
Cierra la sesión actual

```
POST /auth/logout
Authorization: Bearer <access_token>
Content-Type: application/json
```

**Request**:
```json
{
    "refreshToken": "550e8400-e29b-41d4-a716-446655440000"
}
```

**Response (200 OK)**:
```json
{
    "message": "Sesión cerrada correctamente"
}
```

### POST /auth/forgot-password
Solicita reset de contraseña

```
POST /auth/forgot-password
Content-Type: application/json
```

**Request**:
```json
{
    "emailAddress": "usuario@ejemplo.com"
}
```

**Response (200 OK)**:
```json
{
    "message": "Si el correo existe, se enviaron instrucciones"
}
```

### POST /auth/reset-password
Resetea la contraseña con token

```
POST /auth/reset-password
Content-Type: application/json
```

**Request**:
```json
{
    "resetToken": "token-enviado-por-email",
    "newPassword": "NuevaContraseña123!"
}
```

**Response (200 OK)**:
```json
{
    "message": "Contraseña actualizada correctamente"
}
```

### POST /auth/verify-email
Verifica el email del usuario

```
POST /auth/verify-email
Content-Type: application/json
```

**Request**:
```json
{
    "verificationToken": "token-enviado-por-email"
}
```

**Response (200 OK)**:
```json
{
    "message": "Correo confirmado correctamente"
}
```

### GET /auth/me
Obtiene información del usuario autenticado

```
GET /auth/me
Authorization: Bearer <access_token>
```

**Response (200 OK)**:
```json
{
    "id": 1,
    "loginName": "usuario123",
    "emailAddress": "usuario@ejemplo.com",
    "displayName": "Mi Nombre Completo",
    "bioText": "Mi biografía",
    "avatarUrl": "https://ejemplo.com/avatar.jpg",
    "accessLevel": "user",
    "accountState": "active"
}
```

## Endpoints de Historias

### POST /stories
Crea una nueva historia

```
POST /stories
Authorization: Bearer <access_token>
Content-Type: application/json
```

**Request**:
```json
{
    "title": "Mi Primera Historia",
    "description": "Una descripción interesante",
    "visibilityState": "public",
    "publicationState": "draft"
}
```

**Response (201 Created)**:
```json
{
    "id": 1,
    "title": "Mi Primera Historia",
    "slugText": "mi-primera-historia",
    "ownerUserId": 1
}
```

### GET /stories
Lista historias públicas (paginado)

```
GET /stories?page=0&size=20&sort=createdAt:desc
```

**Response (200 OK)**:
```json
{
    "content": [
        {
            "id": 1,
            "title": "Mi Primera Historia",
            "description": "Una descripción interesante",
            "coverImageUrl": "https://ejemplo.com/cover.jpg",
            "ownerUserId": 1,
            "publicationState": "published",
            "createdAt": "2026-07-07T10:30:00"
        }
    ],
    "totalElements": 100,
    "totalPages": 5,
    "currentPage": 0,
    "pageSize": 20
}
```

### GET /stories/{id}
Obtiene detalles de una historia

```
GET /stories/1
```

**Response (200 OK)**:
```json
{
    "id": 1,
    "title": "Mi Primera Historia",
    "slugText": "mi-primera-historia",
    "description": "Una descripción interesante",
    "ownerUserId": 1,
    "publicationState": "published",
    "visibilityState": "public",
    "allowFeedback": true,
    "allowScores": true,
    "publishedAt": "2026-07-07T10:30:00",
    "createdAt": "2026-07-07T10:30:00"
}
```

### GET /stories/slug/{slug}
Obtiene historia por slug

```
GET /stories/slug/mi-primera-historia
```

**Response (200 OK)**:
```json
{
    "id": 1,
    "title": "Mi Primera Historia",
    "slugText": "mi-primera-historia"
}
```

### GET /stories/search
Busca historias

```
GET /stories/search?q=aventura&page=0&size=20
```

**Query Parameters**:
- `q`: Término de búsqueda
- `visibilityState`: public, private, draft
- `publicationState`: draft, published
- `page`: Número de página (0-based)
- `size`: Elementos por página
- `sort`: Campo y dirección (ej: createdAt:desc)

**Response (200 OK)**:
```json
{
    "content": [...],
    "totalElements": 50,
    "totalPages": 3,
    "currentPage": 0
}
```

### GET /stories/user/{userId}
Obtiene historias de un usuario

```
GET /stories/user/1?includeDrafts=false&page=0&size=20
```

**Response (200 OK)**:
```json
{
    "content": [...],
    "totalElements": 10,
    "totalPages": 1,
    "currentPage": 0
}
```

### PUT /stories/{id}
Actualiza una historia

```
PUT /stories/1
Authorization: Bearer <access_token>
Content-Type: application/json
```

**Request**:
```json
{
    "title": "Título Actualizado",
    "description": "Nueva descripción",
    "visibilityState": "public"
}
```

**Response (200 OK)**:
```json
{
    "id": 1,
    "title": "Título Actualizado",
    "description": "Nueva descripción"
}
```

### POST /stories/{id}/publish
Publica una historia

```
POST /stories/1/publish
Authorization: Bearer <access_token>
```

**Response (200 OK)**:
```json
{
    "id": 1,
    "publicationState": "published",
    "publishedAt": "2026-07-07T10:30:00"
}
```

### POST /stories/{id}/archive
Archiva una historia

```
POST /stories/1/archive
Authorization: Bearer <access_token>
```

**Response (200 OK)**:
```json
{
    "id": 1,
    "archivedAt": "2026-07-07T10:30:00"
}
```

### DELETE /stories/{id}
Elimina una historia (soft delete)

```
DELETE /stories/1
Authorization: Bearer <access_token>
```

**Response (200 OK)**:
```json
{
    "message": "Historia eliminada correctamente"
}
```

## Endpoints de Capítulos

### POST /chapters
Crea un nuevo capítulo

```
POST /chapters
Authorization: Bearer <access_token>
Content-Type: application/json
```

**Request**:
```json
{
    "storyId": 1,
    "title": "Capítulo 1: El Inicio",
    "contentText": "Contenido del capítulo...",
    "sequenceNumber": 1
}
```

**Response (201 Created)**:
```json
{
    "id": 1,
    "storyId": 1,
    "title": "Capítulo 1: El Inicio",
    "sequenceNumber": 1
}
```

### GET /chapters/{id}
Obtiene detalles de un capítulo

```
GET /chapters/1
```

**Response (200 OK)**:
```json
{
    "id": 1,
    "storyId": 1,
    "title": "Capítulo 1: El Inicio",
    "contentText": "Contenido del capítulo...",
    "sequenceNumber": 1,
    "publicationState": "draft"
}
```

### GET /chapters/story/{storyId}
Lista capítulos de una historia

```
GET /chapters/story/1?page=0&size=20
```

**Response (200 OK)**:
```json
{
    "content": [...],
    "totalElements": 5,
    "totalPages": 1,
    "currentPage": 0
}
```

### PUT /chapters/{id}
Actualiza un capítulo

```
PUT /chapters/1
Authorization: Bearer <access_token>
Content-Type: application/json
```

### DELETE /chapters/{id}
Elimina un capítulo

```
DELETE /chapters/1
Authorization: Bearer <access_token>
```

## Endpoints de Comentarios

### POST /comments
Crea un nuevo comentario

```
POST /comments
Authorization: Bearer <access_token>
Content-Type: application/json
```

**Request**:
```json
{
    "storyId": 1,
    "chapterId": null,
    "contentText": "¡Excelente historia!"
}
```

**Response (201 Created)**:
```json
{
    "id": 1,
    "storyId": 1,
    "contentText": "¡Excelente historia!",
    "commenterUserId": 1
}
```

### GET /comments/story/{storyId}
Lista comentarios de una historia

```
GET /comments/story/1?page=0&size=20
```

**Response (200 OK)**:
```json
{
    "content": [...],
    "totalElements": 50,
    "totalPages": 3,
    "currentPage": 0
}
```

### DELETE /comments/{id}
Elimina un comentario

```
DELETE /comments/1
Authorization: Bearer <access_token>
```

## Endpoints de Ratings

### POST /ratings
Crea una calificación

```
POST /ratings
Authorization: Bearer <access_token>
Content-Type: application/json
```

**Request**:
```json
{
    "storyId": 1,
    "ratingValue": 5
}
```

**Response (201 Created)**:
```json
{
    "id": 1,
    "storyId": 1,
    "ratingValue": 5
}
```

### GET /ratings/story/{storyId}/average
Obtiene calificación promedio de una historia

```
GET /ratings/story/1/average
```

**Response (200 OK)**:
```json
{
    "storyId": 1,
    "averageRating": 4.5,
    "totalRatings": 20
}
```

## Endpoints de Favoritos

### POST /favorites
Agrega una historia a favoritos

```
POST /favorites
Authorization: Bearer <access_token>
Content-Type: application/json
```

**Request**:
```json
{
    "storyId": 1
}
```

**Response (201 Created)**:
```json
{
    "id": 1,
    "storyId": 1,
    "userId": 1
}
```

### GET /favorites/story/{storyId}/count
Obtiene cantidad de favoritos de una historia

```
GET /favorites/story/1/count
```

**Response (200 OK)**:
```json
{
    "storyId": 1,
    "favoriteCount": 150
}
```

### DELETE /favorites/{id}
Elimina de favoritos

```
DELETE /favorites/1
Authorization: Bearer <access_token>
```

## Endpoints de Usuarios

### GET /users/{id}
Obtiene perfil público de usuario

```
GET /users/1
```

**Response (200 OK)**:
```json
{
    "id": 1,
    "loginName": "usuario123",
    "displayName": "Mi Nombre",
    "bioText": "Mi biografía",
    "avatarUrl": "https://ejemplo.com/avatar.jpg",
    "createdAt": "2026-07-07T10:30:00"
}
```

### GET /users/{id}/public-profile
Obtiene perfil público completo

```
GET /users/1/public-profile
```

### GET /users/{id}/stories
Obtiene historias públicas de un usuario

```
GET /users/1/stories?page=0&size=20
```

## Endpoints de Dashboard

### GET /dashboard/my-stories
Obtiene todas las historias del usuario autenticado

```
GET /dashboard/my-stories?page=0&size=20
Authorization: Bearer <access_token>
```

### GET /dashboard/analytics
Obtiene análisis de usuarios autenticado

```
GET /dashboard/analytics
Authorization: Bearer <access_token>
```

## Endpoints de Métricas

### GET /metrics/views/story
Obtiene vistas de historia

```
GET /metrics/views/story?storyId=1
```

### GET /metrics/stories/top-viewed
Obtiene historias más vistas

```
GET /metrics/stories/top-viewed?limit=10
```

## Respuestas de Error

Todas las excepciones se manejan globalmente y devuelven:

```json
{
    "timestamp": "2026-07-07T10:30:00",
    "status": 400,
    "message": "Descripción del error"
}
```

Para errores de validación:

```json
{
    "timestamp": "2026-07-07T10:30:00",
    "status": 400,
    "errors": {
        "title": "El título no puede estar vacío",
        "description": "Descripción máximo 5000 caracteres"
    }
}
```

