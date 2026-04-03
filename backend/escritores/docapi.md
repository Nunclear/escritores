# Documentación API - Escritores (Plataforma de Historias)

## Descripción General

API REST para gestión de historias colaborativas, personajes, capítulos, comentarios, calificaciones, moderación y análisis. El sistema soporta autenticación JWT y diferentes niveles de acceso (Usuario, Moderador, Administrador).

## Base URL

```
http://localhost:8080/api
```

## Autenticación

- **Método**: Bearer Token (JWT)
- **Header**: `Authorization: Bearer {token}`
- **Alternativa**: `X-User-Id` (Integer) para endpoints heredados

---

## 1. Autenticación y Sesión

### 1.1 Registrar Usuario

**Endpoint:** `POST /auth/register`

**Request:**
```json
{
  "email": "usuario@example.com",
  "username": "usuario123",
  "password": "password123"
}
```

**Response (201 Created):**
```json
{
  "message": "Usuario registrado exitosamente",
  "user": {
    "id": 1,
    "username": "usuario123",
    "email": "usuario@example.com",
    "role": "USUARIO",
    "status": "ACTIVE",
    "createdAt": "2024-04-02T10:30:00"
  }
}
```

---

### 1.2 Iniciar Sesión

**Endpoint:** `POST /auth/login`

**Request:**
```json
{
  "email": "usuario@example.com",
  "password": "password123"
}
```

**Response (200 OK):**
```json
{
  "accessToken": "eyJhbGc...",
  "refreshToken": "eyJhbGc...",
  "user": {
    "id": 1,
    "username": "usuario123",
    "email": "usuario@example.com",
    "role": "USUARIO"
  },
  "expiresIn": 3600
}
```

---

### 1.3 Renovar Token

**Endpoint:** `POST /auth/refresh`

**Request:**
```json
{
  "refreshToken": "eyJhbGc..."
}
```

**Response (200 OK):**
```json
{
  "accessToken": "eyJhbGc...",
  "refreshToken": "eyJhbGc...",
  "expiresIn": 3600
}
```

---

### 1.4 Obtener Usuario Autenticado

**Endpoint:** `GET /auth/me`

**Headers:** `Authorization: Bearer {token}`

**Response (200 OK):**
```json
{
  "id": 1,
  "username": "usuario123",
  "email": "usuario@example.com",
  "displayName": "Usuario Ejemplo",
  "bio": "Mi biografía",
  "avatarUrl": "https://example.com/avatar.jpg",
  "role": "USUARIO",
  "status": "ACTIVE",
  "createdAt": "2024-04-02T10:30:00"
}
```

---

### 1.5 Cerrar Sesión

**Endpoint:** `POST /auth/logout`

**Response (200 OK):**
```json
{
  "message": "Sesión cerrada exitosamente. Elimina tus tokens del cliente."
}
```

---

### 1.6 Recuperar Contraseña

**Endpoint:** `POST /auth/forgot-password`

**Request:**
```json
{
  "email": "usuario@example.com"
}
```

**Response (200 OK):**
```json
{
  "message": "Se envió un email con instrucciones para recuperar tu contraseña"
}
```

---

### 1.7 Restablecer Contraseña

**Endpoint:** `POST /auth/reset-password`

**Request:**
```json
{
  "token": "reset_token_from_email",
  "newPassword": "newpassword456"
}
```

**Response (200 OK):**
```json
{
  "message": "Contraseña restablecida exitosamente"
}
```

---

### 1.8 Cambiar Contraseña (Usuario Autenticado)

**Endpoint:** `POST /auth/change-password`

**Headers:** `Authorization: Bearer {token}`

**Request:**
```json
{
  "currentPassword": "password123",
  "newPassword": "newpassword456"
}
```

**Response (200 OK):**
```json
{
  "message": "Contraseña cambiada exitosamente"
}
```

---

### 1.9 Verificar Email

**Endpoint:** `POST /auth/verify-email`

**Request:**
```json
{
  "token": "verification_token_from_email"
}
```

**Response (200 OK):**
```json
{
  "message": "Email verificado exitosamente"
}
```

---

### 1.10 Enviar Email de Verificación

**Endpoint:** `POST /auth/send-verification`

**Request:**
```json
{
  "email": "usuario@example.com"
}
```

**Response (200 OK):**
```json
{
  "message": "Se envió un email de verificación"
}
```

---

## 2. Gestión de Usuarios

### 2.1 Obtener Perfil de Usuario

**Endpoint:** `GET /users/{id}`

**Response (200 OK):**
```json
{
  "id": 1,
  "username": "usuario1",
  "email": "usuario@example.com",
  "displayName": "Usuario Ejemplo",
  "bio": "Mi biografía",
  "avatarUrl": "https://example.com/avatar.jpg",
  "role": "USUARIO",
  "status": "ACTIVE",
  "lastLoginAt": "2024-04-02T15:20:00",
  "createdAt": "2024-04-02T10:30:00"
}
```

---

### 2.2 Listar Todos los Usuarios

**Endpoint:** `GET /users`

**Query Parameters:**
- `page` (optional): Número de página (default: 0)
- `size` (optional): Tamaño de página (default: 20)
- `role` (optional): Filtrar por rol (USUARIO, MODERADOR, ADMINISTRADOR)
- `status` (optional): Filtrar por estado (ACTIVE, SUSPENDED, BANNED)

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "username": "usuario1",
    "email": "usuario@example.com",
    "displayName": "Usuario Ejemplo",
    "role": "USUARIO",
    "status": "ACTIVE",
    "createdAt": "2024-04-02T10:30:00"
  }
]
```

---

### 2.3 Actualizar Perfil

**Endpoint:** `PUT /users/{id}`

**Headers:** `Authorization: Bearer {token}`

**Request:**
```json
{
  "displayName": "Nuevo Nombre",
  "bio": "Nueva biografía",
  "avatarUrl": "https://example.com/new-avatar.jpg"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "username": "usuario1",
  "displayName": "Nuevo Nombre",
  "bio": "Nueva biografía",
  "avatarUrl": "https://example.com/new-avatar.jpg",
  "updatedAt": "2024-04-02T16:00:00"
}
```

---

### 2.4 Obtener Usuario por Username

**Endpoint:** `GET /users/username/{username}`

**Response (200 OK):**
```json
{
  "id": 1,
  "username": "usuario1",
  "displayName": "Usuario Ejemplo",
  "bio": "Mi biografía",
  "avatarUrl": "https://example.com/avatar.jpg",
  "createdAt": "2024-04-02T10:30:00"
}
```

---

### 2.5 Desactivar Cuenta

**Endpoint:** `POST /users/{id}/deactivate`

**Headers:** `Authorization: Bearer {token}`

**Response (200 OK):**
```json
{
  "message": "Cuenta desactivada"
}
```

---

## 3. Gestión de Favoritos

### Marcar como favorito
- **Endpoint**: `POST /api/favorites/stories/{storyId}`
- **Auth**: Bearer Token
- **Response**: StoryFavorite object

### Remover de favoritos
- **Endpoint**: `DELETE /api/favorites/stories/{storyId}`
- **Auth**: Bearer Token
- **Response**: 204 No Content

### Obtener mis favoritos
- **Endpoint**: `GET /api/favorites`
- **Auth**: Bearer Token
- **Response**: Array de StoryFavorite

### Verificar si es favorito
- **Endpoint**: `GET /api/favorites/stories/{storyId}/is-favorite`
- **Auth**: Bearer Token
- **Response**: `{"isFavorite": true}`

### Contar favoritos
- **Endpoint**: `GET /api/favorites/stories/{storyId}/count`
- **Response**: `{"count": 42}`

---

## 4. Seguimiento de Autores

### Seguir a un usuario
- **Endpoint**: `POST /api/follows/users/{followedId}`
- **Auth**: Bearer Token
- **Response**: UserFollow object

### Dejar de seguir
- **Endpoint**: `DELETE /api/follows/users/{followedId}`
- **Auth**: Bearer Token
- **Response**: 204 No Content

### Obtener usuarios seguidos
- **Endpoint**: `GET /api/follows/following`
- **Auth**: Bearer Token
- **Response**: Array de UserFollow

### Obtener mis seguidores
- **Endpoint**: `GET /api/follows/followers`
- **Auth**: Bearer Token
- **Response**: Array de UserFollow

### Verificar si sigo a un usuario
- **Endpoint**: `GET /api/follows/users/{followedId}/is-following`
- **Auth**: Bearer Token
- **Response**: `{"isFollowing": true}`

### Contar seguidores
- **Endpoint**: `GET /api/follows/users/{userId}/followers-count`
- **Response**: `{"count": 150}`

---

## 5. Comunicados Globales

### Crear comunicado
- **Endpoint**: `POST /api/notices`
- **Auth**: Bearer Token (Admin only)
- **Body**:
```json
{
  "title": "Mantenimiento",
  "content": "El servidor estará en mantenimiento...",
  "scheduledAt": "2024-04-15T00:00:00"
}
```

### Actualizar comunicado
- **Endpoint**: `PUT /api/notices/{id}`
- **Auth**: Bearer Token (Admin only)

### Activar/desactivar comunicado
- **Endpoint**: `PATCH /api/notices/{id}/enable`
- **Endpoint**: `PATCH /api/notices/{id}/disable`

### Obtener comunicados activos
- **Endpoint**: `GET /api/notices/active`
- **Response**: Array de GlobalNotice

---

## 6. Búsqueda y Filtrado

### Búsqueda de historias
- **Endpoint**: `GET /api/search/stories?title=query&page=0&size=20`
- **Response**: Paginated results

### Búsqueda por autor
- **Endpoint**: `GET /api/search/stories/author?author=username`

### Búsqueda por estado
- **Endpoint**: `GET /api/search/stories/status?status=published`

### Búsqueda de personajes
- **Endpoint**: `GET /api/search/characters?name=query`

### Búsqueda de capítulos
- **Endpoint**: `GET /api/search/chapters?title=query`

### Búsqueda avanzada
- **Endpoint**: `GET /api/search/stories/advanced?title=q&author=q&status=q`

### Búsqueda global
- **Endpoint**: `GET /api/search/global?q=query`
- **Response**: `{stories, characters, chapters}`

---

## 7. Métricas y Análitica

### Registrar visita
- **Endpoint**: `POST /api/metrics/stories/{storyId}/view`
- **Response**: StoryView object

### Total de visitas
- **Endpoint**: `GET /api/metrics/stories/{storyId}/total-views`
- **Response**: `{"storyId": 1, "totalViews": 500}`

### Usuarios únicos
- **Endpoint**: `GET /api/metrics/stories/{storyId}/unique-users`

### Métricas completas
- **Endpoint**: `GET /api/metrics/stories/{storyId}`
- **Response**: Objeto con all metrics

### Visitas recientes
- **Endpoint**: `GET /api/metrics/stories/{storyId}/recent?days=7`

### Ranking de historias
- **Endpoint**: `GET /api/metrics/top-stories?limit=10`

---

## 8. Relación Personaje-Habilidad

### Asignar habilidad
- **Endpoint**: `POST /api/character-skills`
- **Body**:
```json
{
  "characterId": 1,
  "skillId": 1,
  "proficiency": 75,
  "notes": "Maestría en combate"
}
```

### Obtener habilidades del personaje
- **Endpoint**: `GET /api/character-skills/characters/{characterId}`

### Actualizar proficiency
- **Endpoint**: `PUT /api/character-skills/{characterSkillId}/proficiency`

### Remover habilidad
- **Endpoint**: `DELETE /api/character-skills/{characterSkillId}`

---

## 9. Gestión de Historias

### 3.1 Crear Historia

**Endpoint:** `POST /stories`

**Headers:** `Authorization: Bearer {token}`

**Request:**
```json
{
  "title": "Mi Primera Historia",
  "description": "Una historia épica",
  "coverImageUrl": "https://example.com/cover.jpg",
  "visibilityState": "public",
  "publicationState": "draft",
  "allowFeedback": true,
  "allowScores": true,
  "startedOn": "2024-04-02"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "ownerUserId": 1,
  "ownerUserName": "usuario1",
  "title": "Mi Primera Historia",
  "slugText": "mi-primera-historia",
  "description": "Una historia épica",
  "visibilityState": "public",
  "publicationState": "draft",
  "createdAt": "2024-04-02T10:30:00"
}
```

---

### 3.2 Obtener Historia

**Endpoint:** `GET /stories/{id}`

**Response (200 OK):**
```json
{
  "id": 1,
  "ownerUserId": 1,
  "ownerUserName": "usuario1",
  "title": "Mi Primera Historia",
  "description": "Una historia épica",
  "visibilityState": "public",
  "publicationState": "published",
  "allowFeedback": true,
  "allowScores": true,
  "publishedAt": "2024-04-02T14:00:00",
  "createdAt": "2024-04-02T10:30:00"
}
```

---

### 3.3 Listar Historias Publicadas

**Endpoint:** `GET /stories`

**Query Parameters:**
- `page`, `size`: Paginación
- `visibility`: Filtrar por visibilidad (public, private)
- `sort`: Ordenamiento (createdAt, title)

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "ownerUserId": 1,
    "ownerUserName": "usuario1",
    "title": "Mi Primera Historia",
    "publicationState": "published",
    "createdAt": "2024-04-02T10:30:00"
  }
]
```

---

### 3.4 Listar Historias por Usuario

**Endpoint:** `GET /stories/user/{userId}`

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "ownerUserId": 1,
    "title": "Mi Primera Historia",
    "publicationState": "published"
  },
  {
    "id": 2,
    "ownerUserId": 1,
    "title": "Segunda Historia",
    "publicationState": "draft"
  }
]
```

---

### 3.5 Listar Historias por Visibilidad

**Endpoint:** `GET /stories/visibility/{visibility}`

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "ownerUserId": 1,
    "title": "Mi Primera Historia",
    "visibilityState": "public"
  }
]
```

---

### 3.6 Actualizar Historia

**Endpoint:** `PUT /stories/{id}`

**Headers:** `Authorization: Bearer {token}`

**Request:**
```json
{
  "title": "Mi Primera Historia - Actualizada",
  "description": "Una historia épica actualizada",
  "publicationState": "published"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "title": "Mi Primera Historia - Actualizada",
  "publicationState": "published",
  "publishedAt": "2024-04-02T16:00:00",
  "updatedAt": "2024-04-02T16:00:00"
}
```

---

### 3.7 Archivar Historia

**Endpoint:** `POST /stories/{id}/archive`

**Headers:** `Authorization: Bearer {token}`

**Response (200 OK):**
```json
{
  "message": "Historia archivada"
}
```

---

### 3.8 Eliminar Historia

**Endpoint:** `DELETE /stories/{id}`

**Headers:** `Authorization: Bearer {token}`

**Response (204 No Content)**

---

## 4. Gestión de Capítulos

### 4.1 Crear Capítulo

**Endpoint:** `POST /chapters`

**Headers:** `Authorization: Bearer {token}`

**Query Parameters:**
- `storyId` (required): ID de la historia

**Request:**
```json
{
  "title": "Capítulo 1",
  "subtitle": "El Comienzo",
  "content": "Érase una vez...",
  "publicationState": "draft"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "title": "Capítulo 1",
  "content": "Érase una vez...",
  "storyId": 1,
  "publicationState": "draft",
  "readingMinutes": 2,
  "wordCount": 125,
  "createdAt": "2024-04-02T10:30:00"
}
```

---

### 4.2 Obtener Capítulo

**Endpoint:** `GET /chapters/{id}`

**Response (200 OK):**
```json
{
  "id": 1,
  "title": "Capítulo 1",
  "content": "Érase una vez...",
  "storyId": 1,
  "publicationState": "published",
  "readingMinutes": 2,
  "wordCount": 125,
  "createdAt": "2024-04-02T10:30:00"
}
```

---

### 4.3 Listar Capítulos de Historia

**Endpoint:** `GET /chapters/story/{storyId}`

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "title": "Capítulo 1",
    "storyId": 1,
    "publicationState": "published",
    "readingMinutes": 2
  }
]
```

---

### 4.4 Listar Capítulos Publicados

**Endpoint:** `GET /chapters/story/{storyId}/published`

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "title": "Capítulo 1",
    "storyId": 1,
    "publicationState": "published",
    "readingMinutes": 2
  }
]
```

---

### 4.5 Actualizar Capítulo

**Endpoint:** `PUT /chapters/{id}`

**Headers:** `Authorization: Bearer {token}`

**Request:**
```json
{
  "title": "Capítulo 1 - Revisado",
  "content": "Contenido actualizado...",
  "publicationState": "published"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "title": "Capítulo 1 - Revisado",
  "publicationState": "published",
  "updatedAt": "2024-04-02T16:00:00"
}
```

---

## 5. Gestión de Comentarios

### 5.1 Crear Comentario

**Endpoint:** `POST /comments`

**Headers:** `Authorization: Bearer {token}`

**Request:**
```json
{
  "storyId": 1,
  "chapterId": null,
  "parentCommentId": null,
  "content": "Excelente historia!"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "storyId": 1,
  "authorUserId": 1,
  "authorUserName": "usuario1",
  "content": "Excelente historia!",
  "visibility": "VISIBLE",
  "createdAt": "2024-04-02T10:30:00"
}
```

---

### 5.2 Obtener Comentario

**Endpoint:** `GET /comments/{id}`

**Response (200 OK):**
```json
{
  "id": 1,
  "storyId": 1,
  "authorUserId": 1,
  "content": "Excelente historia!",
  "visibility": "VISIBLE",
  "createdAt": "2024-04-02T10:30:00"
}
```

---

### 5.3 Listar Comentarios de Historia

**Endpoint:** `GET /comments/story/{storyId}`

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "storyId": 1,
    "authorUserId": 1,
    "content": "Excelente historia!",
    "visibility": "VISIBLE"
  }
]
```

---

### 5.4 Listar Respuestas a Comentario

**Endpoint:** `GET /comments/{parentCommentId}/replies`

**Response (200 OK):**
```json
[
  {
    "id": 2,
    "parentCommentId": 1,
    "content": "¡Gracias!",
    "visibility": "VISIBLE"
  }
]
```

---

### 5.5 Actualizar Comentario

**Endpoint:** `PUT /comments/{id}`

**Headers:** `Authorization: Bearer {token}`

**Request:**
```json
{
  "content": "Comentario actualizado"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "content": "Comentario actualizado",
  "editedAt": "2024-04-02T11:00:00"
}
```

---

### 5.6 Eliminar Comentario

**Endpoint:** `DELETE /comments/{id}`

**Headers:** `Authorization: Bearer {token}`

**Response (204 No Content)**

---

## 6. Gestión de Calificaciones

### 6.1 Crear Calificación

**Endpoint:** `POST /ratings`

**Headers:** `Authorization: Bearer {token}`

**Request:**
```json
{
  "storyId": 1,
  "scoreValue": 5,
  "reviewText": "Una obra maestra"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "storyId": 1,
  "authorUserId": 1,
  "scoreValue": 5,
  "reviewText": "Una obra maestra",
  "createdAt": "2024-04-02T10:30:00"
}
```

---

### 6.2 Obtener Calificación

**Endpoint:** `GET /ratings/{id}`

**Response (200 OK):**
```json
{
  "id": 1,
  "storyId": 1,
  "authorUserId": 1,
  "scoreValue": 5,
  "reviewText": "Una obra maestra"
}
```

---

### 6.3 Listar Calificaciones de Historia

**Endpoint:** `GET /ratings/story/{storyId}`

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "storyId": 1,
    "authorUserId": 1,
    "scoreValue": 5
  }
]
```

---

### 6.4 Obtener Promedio de Calificación

**Endpoint:** `GET /ratings/story/{storyId}/average`

**Response (200 OK):**
```json
{
  "averageScore": 4.5,
  "totalRatings": 10
}
```

---

### 6.5 Obtener Mi Calificación

**Endpoint:** `GET /ratings/story/{storyId}/user/{userId}`

**Response (200 OK):**
```json
{
  "id": 1,
  "storyId": 1,
  "scoreValue": 5,
  "reviewText": "Una obra maestra"
}
```

---

### 6.6 Eliminar Calificación

**Endpoint:** `DELETE /ratings/{id}`

**Headers:** `Authorization: Bearer {token}`

**Response (204 No Content)**

---

## 7. Moderación de Contenido

### 7.1 Ocultar Comentario

**Endpoint:** `PATCH /moderator/comments/{id}/hide`

**Headers:** `Authorization: Bearer {token}` (Moderador/Admin)

**Request:**
```json
{
  "reason": "Contenido inapropiado"
}
```

**Response (200 OK):**
```json
{
  "message": "Comentario ocultado"
}
```

---

### 7.2 Restaurar Comentario

**Endpoint:** `PATCH /moderator/comments/{id}/restore`

**Headers:** `Authorization: Bearer {token}` (Moderador/Admin)

**Response (200 OK):**
```json
{
  "message": "Comentario restaurado"
}
```

---

### 7.3 Listar Comentarios Ocultos

**Endpoint:** `GET /moderator/comments/hidden`

**Headers:** `Authorization: Bearer {token}` (Moderador/Admin)

**Response (200 OK):**
```json
{
  "total": 5,
  "comments": [
    {
      "id": 1,
      "storyId": 1,
      "visibility": "HIDDEN"
    }
  ]
}
```

---

### 7.4 Crear Reporte de Contenido

**Endpoint:** `POST /moderator/reports`

**Headers:** `Authorization: Bearer {token}`

**Request:**
```json
{
  "resourceType": "STORY|CHAPTER|COMMENT",
  "resourceId": 1,
  "reason": "Contenido inapropiado"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "reporterUserId": 1,
  "resourceType": "STORY",
  "reasonText": "Contenido inapropiado",
  "status": "PENDING",
  "createdAt": "2024-04-02T10:30:00"
}
```

---

### 7.5 Listar Reportes

**Endpoint:** `GET /moderator/reports`

**Headers:** `Authorization: Bearer {token}` (Moderador/Admin)

**Query Parameters:**
- `status` (optional): PENDING, RESOLVED, REJECTED

**Response (200 OK):**
```json
{
  "total": 3,
  "reports": [
    {
      "id": 1,
      "reporterUserId": 1,
      "status": "PENDING",
      "createdAt": "2024-04-02T10:30:00"
    }
  ]
}
```

---

### 7.6 Resolver Reporte

**Endpoint:** `PATCH /moderator/reports/{id}/resolve`

**Headers:** `Authorization: Bearer {token}` (Moderador/Admin)

**Request:**
```json
{
  "resolution": "Contenido removido"
}
```

**Response (200 OK):**
```json
{
  "message": "Reporte resuelto"
}
```

---

### 7.7 Rechazar Reporte

**Endpoint:** `PATCH /moderator/reports/{id}/reject`

**Headers:** `Authorization: Bearer {token}` (Moderador/Admin)

**Request:**
```json
{
  "reason": "Reporte no válido"
}
```

**Response (200 OK):**
```json
{
  "message": "Reporte rechazado"
}
```

---

## 8. Sanciones de Usuarios

### 8.1 Crear Advertencia

**Endpoint:** `POST /moderator/sanctions/warning`

**Headers:** `Authorization: Bearer {token}` (Moderador/Admin)

**Request:**
```json
{
  "userId": 5,
  "reason": "Comportamiento inapropiado"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "userId": 5,
  "type": "WARNING",
  "reason": "Comportamiento inapropiado",
  "status": "ACTIVE",
  "createdAt": "2024-04-02T10:30:00"
}
```

---

### 8.2 Aplicar Baneo Temporal

**Endpoint:** `POST /moderator/sanctions/temp-ban`

**Headers:** `Authorization: Bearer {token}` (Moderador/Admin)

**Request:**
```json
{
  "userId": 5,
  "reason": "Spam repetido",
  "durationDays": 7
}
```

**Response (201 Created):**
```json
{
  "id": 2,
  "userId": 5,
  "type": "TEMP_BAN",
  "reason": "Spam repetido",
  "expiresAt": "2024-04-09T10:30:00",
  "status": "ACTIVE"
}
```

---

### 8.3 Listar Sanciones de Usuario

**Endpoint:** `GET /moderator/sanctions/{userId}`

**Headers:** `Authorization: Bearer {token}` (Moderador/Admin)

**Response (200 OK):**
```json
{
  "userId": 5,
  "sanctions": [
    {
      "id": 1,
      "type": "WARNING",
      "reason": "Comportamiento inapropiado",
      "status": "ACTIVE"
    }
  ],
  "total": 1
}
```

---

### 8.4 Ver Cola de Moderación

**Endpoint:** `GET /moderator/queue`

**Headers:** `Authorization: Bearer {token}` (Moderador/Admin)

**Response (200 OK):**
```json
{
  "pendingReports": 5,
  "hiddenComments": 2,
  "activeSanctions": 3,
  "reports": [],
  "comments": []
}
```

---

## 9. Autorización y Gestión de Roles

### 9.1 Cambiar Rol de Usuario

**Endpoint:** `PUT /admin/users/{id}/role`

**Headers:** `Authorization: Bearer {token}` (Admin)

**Request:**
```json
{
  "newRole": "MODERADOR"
}
```

**Response (200 OK):**
```json
{
  "id": 5,
  "username": "usuario5",
  "role": "MODERADOR"
}
```

---

### 9.2 Cambiar Estado de Usuario

**Endpoint:** `PUT /admin/users/{id}/status`

**Headers:** `Authorization: Bearer {token}` (Admin)

**Request:**
```json
{
  "newStatus": "SUSPENDED"
}
```

**Response (200 OK):**
```json
{
  "id": 5,
  "username": "usuario5",
  "status": "SUSPENDED"
}
```

---

### 9.3 Obtener Permisos del Usuario

**Endpoint:** `GET /admin/users/me/permissions`

**Headers:** `Authorization: Bearer {token}`

**Response (200 OK):**
```json
{
  "userId": 1,
  "role": "ADMINISTRADOR",
  "status": "ACTIVE",
  "permissions": {
    "canManageUsers": true,
    "canModerateContent": true,
    "canPublishStories": true,
    "canViewAnalytics": true
  }
}
```

---

## 10. Gestión de Arcos

### 10.1 Crear Arco

**Endpoint:** `POST /arcs`

**Headers:** `Authorization: Bearer {token}`

**Request:**
```json
{
  "title": "Primer Arco",
  "subtitle": "La Introducción",
  "storyId": 1,
  "positionIndex": 1
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "title": "Primer Arco",
  "storyId": 1,
  "positionIndex": 1,
  "createdAt": "2024-04-02T10:30:00"
}
```

---

### 10.2 Listar Arcos de Historia

**Endpoint:** `GET /arcs/story/{storyId}`

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "title": "Primer Arco",
    "storyId": 1,
    "positionIndex": 1
  }
]
```

---

## 11. Gestión de Volúmenes

### 11.1 Crear Volumen

**Endpoint:** `POST /volumes`

**Headers:** `Authorization: Bearer {token}`

**Request:**
```json
{
  "title": "Volumen 1",
  "storyId": 1,
  "arcId": 1,
  "positionIndex": 1
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "title": "Volumen 1",
  "storyId": 1,
  "positionIndex": 1,
  "createdAt": "2024-04-02T10:30:00"
}
```

---

### 11.2 Listar Volúmenes de Historia

**Endpoint:** `GET /volumes/story/{storyId}`

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "title": "Volumen 1",
    "storyId": 1,
    "positionIndex": 1
  }
]
```

---

## 12. Gestión de Personajes

### 12.1 Crear Personaje

**Endpoint:** `POST /characters`

**Headers:** `Authorization: Bearer {token}`

**Request:**
```json
{
  "name": "Aragorn",
  "description": "Un rey noble",
  "characterRoleName": "Protagonista",
  "profession": "Rey",
  "age": 87,
  "isAlive": true,
  "storyId": 1,
  "imageUrl": "https://example.com/aragorn.jpg"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "name": "Aragorn",
  "characterRoleName": "Protagonista",
  "storyId": 1,
  "createdAt": "2024-04-02T10:30:00"
}
```

---

### 12.2 Listar Personajes de Historia

**Endpoint:** `GET /characters/story/{storyId}`

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Aragorn",
    "characterRoleName": "Protagonista",
    "storyId": 1
  }
]
```

---

## 13. Gestión de Habilidades

### 13.1 Crear Habilidad

**Endpoint:** `POST /skills`

**Headers:** `Authorization: Bearer {token}`

**Request:**
```json
{
  "name": "Esgrima",
  "description": "Dominio con la espada",
  "categoryName": "Combate",
  "levelValue": 5,
  "storyId": 1
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "name": "Esgrima",
  "categoryName": "Combate",
  "levelValue": 5,
  "storyId": 1,
  "createdAt": "2024-04-02T10:30:00"
}
```

---

### 13.2 Listar Habilidades de Historia

**Endpoint:** `GET /skills/story/{storyId}`

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Esgrima",
    "categoryName": "Combate",
    "levelValue": 5
  }
]
```

---

## Funcionalidades Faltantes

### No Implementadas Aún

1. **Gestión de Favoritos**
   - Marcar historia como favorita
   - Listar favoritos del usuario
   - Verificar si una historia está en favoritos

2. **Seguimiento de Autores**
   - Seguir/dejar de seguir autor
   - Listar autores seguidos
   - Listar seguidores de un autor

3. **Métricas y Analítica**
   - Registrar visitas a historias/capítulos
   - Dashboard de estadísticas
   - Ranking de historias más vistas

4. **Ideas Narrativas**
   - CRUD completo para ideas de historias
   - Búsqueda y clasificación de ideas

5. **Ítems y Objetos Narrativos**
   - CRUD completo para ítems
   - Filtrado por categoría

6. **Eventos Narrativos**
   - CRUD completo para eventos
   - Filtrado y búsqueda por tipo/etiqueta

7. **Media y Archivos**
   - Subida de archivos
   - Gestión de media asociada a capítulos
   - Descarga segura de archivos

8. **Relación Personaje-Habilidad**
   - Asignar habilidades a personajes
   - Listar habilidades de un personaje
   - Actualizar nivel de habilidad

9. **CRUD Incompleto**
   - Arcos: obtener, actualizar, eliminar, reordenar
   - Volúmenes: obtener, actualizar, eliminar, reordenar
   - Personajes: obtener, actualizar, eliminar, búsqueda
   - Habilidades: obtener, actualizar, eliminar, búsqueda
   - Capítulos: eliminar, archivar, reordenar

10. **Búsqueda Avanzada**
    - Búsqueda global de historias
    - Filtros combinables
    - Búsqueda de personajes, capítulos

11. **Panel de Usuario**
    - Resumen del usuario autenticado
    - Historias creadas
    - Borradores
    - Favoritos
    - Autores seguidos
    - Comentarios recientes
    - Calificaciones emitidas

12. **Panel Administrativo**
    - Resumen general del sistema
    - Estadísticas de usuarios, historias, reportes
    - Actividad reciente
    - Históricos y auditoría

13. **Comunicados Globales**
    - Crear, editar, activar/desactivar
    - Programar fechas de inicio/fin
    - Listar comunicados activos e históricos

14. **Seguridad Adicional**
    - Bloqueo por intentos fallidos de login
    - Invalidación de sesiones activas
    - Cambio de email con validación
    - Historial de cambios de rol/estado

---

## Códigos de Error

| Código | Descripción |
|--------|-------------|
| 200 | OK - Solicitud exitosa |
| 201 | Created - Recurso creado exitosamente |
| 204 | No Content - Solicitud exitosa sin contenido |
| 400 | Bad Request - Datos inválidos |
| 401 | Unauthorized - Token inválido o expirado |
| 403 | Forbidden - Permisos insuficientes |
| 404 | Not Found - Recurso no encontrado |
| 409 | Conflict - Recurso duplicado |
| 500 | Internal Server Error - Error del servidor |

---

## Notas Técnicas

- Todos los timestamps están en formato ISO 8601
- El campo `slugText` se genera automáticamente a partir del título
- Los campos `readingMinutes` y `wordCount` se calculan automáticamente
- Solo el propietario de una historia puede editarla
- Los roles son: USUARIO, MODERADOR, ADMINISTRADOR
- Los estados son: ACTIVE, SUSPENDED, BANNED
- La visibilidad puede ser: public, private
- El estado de publicación puede ser: draft, published
- La visibilidad de comentarios: VISIBLE, HIDDEN, DELETED

---

**Última actualización:** 2024-04-02  
**Versión:** 2.0
