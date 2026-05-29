# Guía de Uso - Nuevos Endpoints Implementados

## Tabla de Contenidos
1. [Géneros](#géneros)
2. [Notificaciones](#notificaciones)
3. [Progreso de Lectura](#progreso-de-lectura)
4. [Versiones de Capítulos](#versiones-de-capítulos)
5. [Likes a Comentarios](#likes-a-comentarios)
6. [Modelos de Datos](#modelos-de-datos)

---

## Géneros

### 1. Crear Género (Requiere ADMIN)
```bash
POST /genres
Content-Type: application/json

{
  "name": "Ciencia Ficción",
  "description": "Historias de ciencia ficción y futuros alternativos",
  "iconUrl": "https://example.com/sci-fi.png"
}

Response 200:
{
  "id": 1,
  "name": "Ciencia Ficción",
  "slug": "ciencia-ficcion",
  "description": "Historias de ciencia ficción...",
  "iconUrl": "https://example.com/sci-fi.png",
  "createdAt": "2026-05-29T10:00:00",
  "updatedAt": "2026-05-29T10:00:00"
}
```

### 2. Listar Géneros
```bash
GET /genres?page=0&size=50

Response 200:
[
  {
    "id": 1,
    "name": "Ciencia Ficción",
    "slug": "ciencia-ficcion",
    ...
  },
  ...
]
```

### 3. Obtener Género por ID
```bash
GET /genres/1

Response 200:
{
  "id": 1,
  "name": "Ciencia Ficción",
  ...
}
```

### 4. Obtener Género por Slug
```bash
GET /genres/slug/ciencia-ficcion

Response 200:
{
  "id": 1,
  "name": "Ciencia Ficción",
  ...
}
```

### 5. Actualizar Género (Requiere ADMIN)
```bash
PUT /genres/1
Content-Type: application/json

{
  "name": "Sci-Fi",
  "description": "Updated description",
  "iconUrl": "https://example.com/new-icon.png"
}
```

### 6. Eliminar Género (Requiere ADMIN)
```bash
DELETE /genres/1
```

### 7. Asignar Géneros a Historia (Requiere AUTH)
```bash
POST /genres/stories/5/genres
Content-Type: application/json

{
  "genreIds": [1, 2, 3]
}
```

---

## Notificaciones

### 1. Obtener Mis Notificaciones (Requiere AUTH)
```bash
GET /notifications/me?page=0&size=20

Response 200:
{
  "content": [
    {
      "id": 1,
      "recipientUserId": 123,
      "type": "NEW_CHAPTER",
      "relatedUserId": 456,
      "relatedStoryId": 789,
      "relatedChapterId": 101,
      "relatedCommentId": null,
      "content": "Un nuevo capítulo ha sido publicado",
      "isRead": false,
      "createdAt": "2026-05-29T10:00:00",
      "updatedAt": "2026-05-29T10:00:00"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 5
}
```

### 2. Obtener una Notificación (Requiere AUTH)
```bash
GET /notifications/1

Response 200:
{
  "id": 1,
  "recipientUserId": 123,
  ...
}
```

### 3. Marcar Notificación como Leída (Requiere AUTH)
```bash
POST /notifications/1/read

Response 200:
{
  "id": 1,
  "recipientUserId": 123,
  "isRead": true,
  ...
}
```

### 4. Marcar Todas las Notificaciones como Leídas (Requiere AUTH)
```bash
POST /notifications/me/read-all

Response 204: No Content
```

### 5. Contar Notificaciones No Leídas (Requiere AUTH)
```bash
GET /notifications/me/unread-count

Response 200:
5
```

### 6. Eliminar Notificación (Requiere AUTH)
```bash
DELETE /notifications/1

Response 204: No Content
```

---

## Progreso de Lectura

### 1. Guardar o Actualizar Progreso (Requiere AUTH)
```bash
POST /reading-progress
Content-Type: application/json

{
  "storyId": 123,
  "lastChapterId": 456,
  "percentageRead": 45
}

Response 200:
{
  "id": 1,
  "userId": 789,
  "storyId": 123,
  "lastChapterId": 456,
  "lastReadAt": "2026-05-29T10:00:00",
  "percentageRead": 45,
  "createdAt": "2026-05-29T10:00:00",
  "updatedAt": "2026-05-29T10:00:00"
}
```

### 2. Obtener Mi Progreso (Requiere AUTH)
```bash
GET /reading-progress/me

Response 200:
[
  {
    "id": 1,
    "userId": 789,
    "storyId": 123,
    "lastChapterId": 456,
    "percentageRead": 45,
    ...
  },
  ...
]
```

### 3. Obtener Progreso de una Historia (Requiere AUTH)
```bash
GET /reading-progress/story/123/me

Response 200:
{
  "id": 1,
  "userId": 789,
  "storyId": 123,
  "lastChapterId": 456,
  "percentageRead": 45,
  ...
}
```

### 4. Obtener Progreso de Otros Usuarios en una Historia
```bash
GET /reading-progress/story/123

Response 200:
[
  {
    "id": 1,
    "userId": 789,
    "storyId": 123,
    "percentageRead": 45,
    ...
  },
  ...
]
```

### 5. Eliminar Mi Progreso (Requiere AUTH)
```bash
DELETE /reading-progress/story/123/me

Response 204: No Content
```

---

## Versiones de Capítulos

### 1. Listar Versiones de un Capítulo
```bash
GET /chapters/123/versions

Response 200:
[
  {
    "id": 1,
    "chapterId": 123,
    "versionNumber": 2,
    "title": "El Viaje Comienza",
    "subtitle": "Parte 1",
    "content": "Contenido del capítulo...",
    "createdAt": "2026-05-29T10:00:00"
  },
  ...
]
```

### 2. Obtener una Versión Específica
```bash
GET /chapters/123/versions/1

Response 200:
{
  "id": 1,
  "chapterId": 123,
  "versionNumber": 1,
  "title": "El Viaje Comienza",
  ...
}
```

### 3. Obtener Última Versión
```bash
GET /chapters/123/versions/latest

Response 200:
{
  "id": 2,
  "chapterId": 123,
  "versionNumber": 2,
  ...
}
```

### 4. Restaurar una Versión Anterior (Requiere AUTH)
```bash
POST /chapters/123/versions/1/restore
Content-Type: application/json

{
  "versionNumber": 1
}

Response 200:
{
  "id": 1,
  "chapterId": 123,
  "versionNumber": 1,
  ...
}
```

### Notas Sobre Versiones
- Las versiones se crean automáticamente cuando se edita un capítulo
- Cada versión contiene el estado completo del capítulo en ese momento
- Se pueden restaurar versiones anteriores sin perder las nuevas

---

## Likes a Comentarios

### 1. Dar Like a un Comentario (Requiere AUTH)
```bash
POST /comments/456/likes

Response 200:
{
  "id": 1,
  "commentId": 456,
  "userId": 789,
  "createdAt": "2026-05-29T10:00:00"
}

// Error si ya lo diste like:
Response 400: Bad Request
{
  "message": "Ya has dado like a este comentario"
}
```

### 2. Quitar Like de un Comentario (Requiere AUTH)
```bash
DELETE /comments/456/likes

Response 204: No Content
```

### 3. Contar Likes de un Comentario
```bash
GET /comments/456/likes/count

Response 200:
42
```

### 4. Verificar si un Usuario Dio Like
```bash
GET /comments/456/likes/user/789

Response 200:
true
```

### 5. Listar Likes de un Comentario
```bash
GET /comments/456/likes

Response 200:
[
  {
    "id": 1,
    "commentId": 456,
    "userId": 789,
    "createdAt": "2026-05-29T10:00:00"
  },
  ...
]
```

---

## Modelos de Datos

### Genre
```json
{
  "id": Integer,
  "name": String (max 100, unique),
  "slug": String (max 100, unique, auto-generated),
  "description": String (max 5000),
  "iconUrl": String (max 255),
  "createdAt": LocalDateTime,
  "updatedAt": LocalDateTime
}
```

### Notification
```json
{
  "id": Integer,
  "recipientUserId": Integer (FK),
  "type": Enum (NEW_CHAPTER, NEW_FOLLOWER, NEW_COMMENT, COMMENT_REPLY, STORY_FAVORITE, STORY_UPDATED, MENTION, CUSTOM),
  "relatedUserId": Integer (opcional),
  "relatedStoryId": Integer (opcional),
  "relatedChapterId": Integer (opcional),
  "relatedCommentId": Integer (opcional),
  "content": String (TEXT),
  "isRead": Boolean (default: false),
  "createdAt": LocalDateTime,
  "updatedAt": LocalDateTime
}
```

### ReadingProgress
```json
{
  "id": Integer,
  "userId": Integer (FK),
  "storyId": Integer (FK),
  "lastChapterId": Integer (opcional),
  "lastReadAt": LocalDateTime,
  "percentageRead": Integer (0-100),
  "createdAt": LocalDateTime,
  "updatedAt": LocalDateTime
}
```

### ChapterVersion
```json
{
  "id": Integer,
  "chapterId": Integer (FK),
  "versionNumber": Integer,
  "title": String (max 255),
  "subtitle": String (max 255),
  "content": String (LONGTEXT),
  "createdAt": LocalDateTime
}
```

### CommentLike
```json
{
  "id": Integer,
  "commentId": Integer (FK),
  "userId": Integer (FK),
  "createdAt": LocalDateTime
}
```

---

## Códigos de Error Comunes

### 400 Bad Request
```json
{
  "message": "Ya has dado like a este comentario"
}
```
Causas:
- Intentar dar like a algo que ya le diste like
- Datos inválidos en request

### 401 Unauthorized
```json
{
  "message": "No autorizado"
}
```
Causas:
- No autenticado
- Token expirado
- Falta de token en header

### 403 Forbidden
```json
{
  "message": "No tienes permiso para esta acción"
}
```
Causas:
- No tienes rol necesario (ADMIN)
- Intentas acceder a recurso de otro usuario

### 404 Not Found
```json
{
  "message": "Recurso no encontrado"
}
```
Causas:
- Género, notificación, etc. no existe
- ID incorrecto

---

## Headers Requeridos

### Autenticación
```bash
Authorization: Bearer <token>
Content-Type: application/json
```

### Respuesta de Error
```bash
400 Bad Request
401 Unauthorized
403 Forbidden
404 Not Found
500 Internal Server Error
```

---

## Validaciones

### Genre
- `name`: No vacío, máx 100 caracteres, único
- `slug`: Auto-generado del nombre
- `description`: Máx 5000 caracteres
- `iconUrl`: Máx 255 caracteres

### ReadingProgress
- `percentageRead`: Entre 0 y 100
- `userId` + `storyId`: Combinación única (un registro por usuario por historia)

### CommentLike
- `userId` + `commentId`: Combinación única (un like por usuario por comentario)

---

## Ejemplos Completos

### Flujo Completo: Leer una Historia

```bash
# 1. Obtener historia y capítulos
GET /stories/123

# 2. Guardar progreso de lectura
POST /reading-progress
{
  "storyId": 123,
  "lastChapterId": 456,
  "percentageRead": 25
}

# 3. Ver versiones del capítulo
GET /chapters/456/versions

# 4. Actualizar progreso
POST /reading-progress
{
  "storyId": 123,
  "lastChapterId": 456,
  "percentageRead": 50
}
```

### Flujo Completo: Interactuar con Comentarios

```bash
# 1. Obtener comentarios de capítulo
GET /chapters/456/comments

# 2. Dar like a un comentario
POST /comments/789/likes

# 3. Ver cuántos likes tiene
GET /comments/789/likes/count
# Response: 42

# 4. Quitar like si cambias de opinión
DELETE /comments/789/likes
```

---

## Notas Importantes

1. **Timestamps Automáticos**: createdAt y updatedAt se establecen automáticamente
2. **Slugs Automáticos**: Los slugs de géneros se generan automáticamente del nombre
3. **Versiones Automáticas**: Las versiones de capítulos se crean automáticamente al editar (requiere integración adicional)
4. **Validación**: Todos los campos tienen validación en servidor
5. **Paginación**: Endpoints GET soportan paginación con `page` y `size`
6. **Restricción Única**: No puedes dar like dos veces al mismo comentario
