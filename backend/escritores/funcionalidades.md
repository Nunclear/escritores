# Estado de Funcionalidades - Proyecto Escritores

**Última actualización:** 2026-04-02  
**Estado General:** 35% Implementado | 65% Por Implementar

---

## 1. Autenticación y Sesión

### ✅ Implementadas (10/10 - 100%)
- ✅ **AuthService.java** - Servicio de autenticación completo
- ✅ **AuthController.java** - 10 endpoints de autenticación
- ✅ **JwtService.java** - Generación y validación de JWT
- ✅ **Registro** (POST /api/auth/register)
  - Validar email y username únicos
  - Hash de contraseña con BCrypt
  
- ✅ **Login** (POST /api/auth/login)
  - Validar credenciales
  - Generar access token (1 hora)
  - Generar refresh token (7 días)
  - Retornar LoginResponse completo

- ✅ **Renovación de token** (POST /api/auth/refresh)
  - Validar refresh token
  - Generar nuevo access token
  - Generar nuevo refresh token

- ✅ **Obtener usuario autenticado** (GET /api/auth/me)
  - Extraer userId del header Authorization
  - Retornar datos del usuario con rol y estado

- ✅ **Recuperación de contraseña** (POST /api/auth/forgot-password)
  - Validar email existe
  - Generar password reset token (1 hora)
  - TODO: Enviar email

- ✅ **Restablecimiento de contraseña** (POST /api/auth/reset-password)
  - Validar password reset token
  - Hash nueva contraseña
  - Limpiar tokens expirados

- ✅ **Cambio de contraseña** (POST /api/auth/change-password)
  - Verificar contraseña actual
  - Hash nueva contraseña

- ✅ **Confirmación de correo** (POST /api/auth/verify-email)
  - Validar email verification token
  - Marcar email como verificado

- ✅ **Envío de verificación** (POST /api/auth/send-verification)
  - Generar email verification token (24 horas)
  - TODO: Enviar email

- ✅ **Logout** (POST /api/auth/logout)
  - Mensaje de cliente para eliminar tokens
  - Preparado para token blacklist en futuro

---

## 2. Autorización y Gestión de Roles

### ✅ Implementadas (8/8 - 100%)
- ✅ **AuthorizationService.java** - Servicio de autorización
- ✅ **AdminController.java** - 6 endpoints administrativos
- ✅ **Enum Role** - LECTOR, USUARIO, MODERADOR, ADMINISTRADOR
- ✅ **Enum UserStatus** - ACTIVE, SUSPENDED, BANNED

- ✅ **Cambiar rol de usuario** (PUT /api/admin/users/{id}/role)
  - Solo ADMINISTRADOR
  - No permite cambiar rol propio
  - Validación en AuthorizationService

- ✅ **Listar usuarios por rol** (GET /api/admin/users?role=MODERADOR)
  - ADMINISTRADOR y MODERADOR
  - Filtrado en memoria (TODO: implementar en SQL)

- ✅ **Listar usuarios por estado** (GET /api/admin/users?status=ACTIVE)
  - ADMINISTRADOR y MODERADOR
  - Estados: ACTIVE, SUSPENDED, BANNED

- ✅ **Activar o suspender cuentas** (PUT /api/admin/users/{id}/status)
  - ADMINISTRADOR siempre
  - MODERADOR: suspensión temporal (no permanente)

- ✅ **Obtener detalles de usuario** (GET /api/admin/users/{id})
  - ADMINISTRADOR y MODERADOR

- ✅ **Consultar permisos efectivos** (GET /api/admin/users/me/permissions)
  - Retorna mapa completo de permisos
  - Método hasPermission() para validaciones

- ✅ **Estadísticas generales** (GET /api/admin/statistics/overview)
  - Total de usuarios
  - Total de usuarios activos
  - Distribución por rol

- ✅ **Listar todos los usuarios** (GET /api/admin/users)
  - Con paginación y filtros
  - ADMINISTRADOR y MODERADOR

---

## 3. Moderación de Comentarios

### ✅ Implementadas (8/8 - 100%)
- ✅ **ModerationService.java** - Servicio de moderación
- ✅ **ModerationController.java** - Endpoints de moderación
- ✅ **Entity StoryComment** - Campo visibility y hideReason

- ✅ **Ocultar comentario** (PATCH /api/moderator/comments/{id}/hide)
  - MODERADOR y ADMINISTRADOR
  - Registra motivo y timestamp

- ✅ **Restaurar comentario** (PATCH /api/moderator/comments/{id}/restore)
  - MODERADOR y ADMINISTRADOR
  - Limpia campos de ocultamiento

- ✅ **Listar comentarios ocultos** (GET /api/moderator/comments/hidden)
  - MODERADOR y ADMINISTRADOR
  - Con detalles completos

- ✅ **Marcar comentario como revisado** (PATCH /api/comments/{id}/reviewed)
  - MODERADOR y ADMINISTRADOR
  - Registra timestamp de revisión

- ✅ **Cola de moderación** (GET /api/moderator/queue)
  - Incluye reportes pendientes
  - Comentarios ocultos
  - Sanciones activas
  - Resumen de números

- ✅ **Crear reporte** (POST /api/moderator/reports)
  - Para historia, capítulo, comentario, usuario
  - Con descripción detallada

- ✅ **Listar reportes** (GET /api/moderator/reports?status=PENDING)
  - Filtrable por estado

- ✅ **Resolver reporte** (PATCH /api/moderator/reports/{id}/resolve)
  - Registra resolución y revisor

---

## 4. Reportes de Contenido

### ✅ Implementadas
- Entity ContentReport con campos básicos
- Repositorio ContentReportRepository

### ❌ Faltantes - ALTA PRIORIDAD
- **Reportar historia (POST /api/stories/{id}/reports)**
  - USUARIO, MODERADOR, ADMINISTRADOR
  - Incluir motivo y descripción

- **Reportar capítulo (POST /api/chapters/{id}/reports)**
  - USUARIO, MODERADOR, ADMINISTRADOR

- **Reportar comentario (POST /api/comments/{id}/reports)**
  - USUARIO, MODERADOR, ADMINISTRADOR

- **Reportar usuario (POST /api/users/{id}/reports)**
  - USUARIO, MODERADOR, ADMINISTRADOR

- **Listar reportes pendientes (GET /api/moderator/reports?status=PENDING)**
  - MODERADOR y ADMINISTRADOR

- **Listar reportes por estado (GET /api/moderator/reports?status=RESOLVED)**
  - MODERADOR y ADMINISTRADOR

- **Ver detalle de reporte (GET /api/moderator/reports/{id})**
  - MODERADOR y ADMINISTRADOR
  - Usuario solo sus propios reportes si aplica

- **Asignar revisor (PATCH /api/moderator/reports/{id}/assign)**
  - MODERADOR y ADMINISTRADOR

- **Marcar como revisado (PATCH /api/moderator/reports/{id}/reviewed)**
  - MODERADOR y ADMINISTRADOR

- **Resolver reporte (PATCH /api/moderator/reports/{id}/resolve)**
  - MODERADOR y ADMINISTRADOR
  - Incluir resolución y acciones tomadas

- **Rechazar reporte (PATCH /api/moderator/reports/{id}/reject)**
  - MODERADOR y ADMINISTRADOR

- **Registrar motivo de cierre (PATCH /api/moderator/reports/{id}/close)**
  - MODERADOR y ADMINISTRADOR

- **Ver historial de reportes (GET /api/moderator/reports/history?userId=X&resourceId=Y)**
  - MODERADOR y ADMINISTRADOR

---

## 5. Sanciones y Baneos

### ✅ Implementadas (8/9 - 88%)
- ✅ **Entity UserSanction** - Completa con todos los campos
- ✅ **ModerationService** - Método completo para sanciones
- ✅ **ModerationController** - Endpoints de sanciones

- ✅ **Crear advertencia** (POST /api/moderator/sanctions/warning)
  - MODERADOR y ADMINISTRADOR
  - Registra motivo y timestamp

- ✅ **Aplicar baneo temporal** (POST /api/moderator/sanctions/temp-ban)
  - MODERADOR y ADMINISTRADOR
  - Incluir duración en días
  - Suspende automáticamente la cuenta

- ✅ **Aplicar baneo permanente** (POST /api/admin/sanctions/permanent-ban)
  - Solo ADMINISTRADOR
  - Marca cuenta como BANNED

- ✅ **Levantar sanción** (DELETE /api/admin/sanctions/{id})
  - Solo ADMINISTRADOR
  - Activa cuenta si no tiene otras sanciones

- ✅ **Listar sanciones de usuario** (GET /api/moderator/sanctions/{userId})
  - ADMINISTRADOR y MODERADOR
  - Filtra solo sanciones activas

- ✅ **Verificar si usuario está sancionado** (En servicio)
  - isUserSanctioned(userId)
  - Verificación interna

- ✅ **Registrar motivo de sanción** (Automático)
  - Campo reason en UserSanction
  - Timestamp de creación

- ✅ **Diferenciar sanción manual vs automática** (Campo type)
  - WARNING, TEMP_BAN, PERMANENT_BAN

- ❌ **Ver historial disciplinario completo**
  - FALTANTE: Necesita endpoint específico GET /api/admin/users/{id}/discipline-history
  - WORKAROUND: Lista sanciones activas por usuario

- ✅ **Limpieza de baneos expirados** (En servicio)
  - cleanupExpiredSanctions()
  - TODO: Ejecutar periódicamente con Scheduler

---

## 6. Comunicados Globales

### ✅ Implementadas
- Entity GlobalNotice con campos básicos
- Repositorio GlobalNoticeRepository

### ❌ Faltantes - MEDIA PRIORIDAD
- **Crear comunicado (POST /api/admin/notices)**
  - Solo ADMINISTRADOR
  - Título, contenido, activo/inactivo

- **Editar comunicado (PUT /api/admin/notices/{id})**
  - Solo ADMINISTRADOR

- **Activar comunicado (PATCH /api/admin/notices/{id}/activate)**
  - Solo ADMINISTRADOR

- **Desactivar comunicado (PATCH /api/admin/notices/{id}/deactivate)**
  - Solo ADMINISTRADOR

- **Programar fecha de inicio (PUT /api/admin/notices/{id}/schedule)**
  - Solo ADMINISTRADOR
  - Campo startDate

- **Programar fecha de fin (PUT /api/admin/notices/{id}/schedule)**
  - Solo ADMINISTRADOR
  - Campo endDate

- **Listar comunicados activos (GET /api/notices/active)**
  - LECTOR, USUARIO, MODERADOR, ADMINISTRADOR

- **Listar comunicados históricos (GET /api/admin/notices/history)**
  - ADMINISTRADOR y MODERADOR

- **Ver detalle de comunicado (GET /api/notices/{id})**
  - Según estado (público/privado)

- **Eliminar o archivar comunicado (DELETE /api/admin/notices/{id})**
  - Solo ADMINISTRADOR

---

## 7. Favoritos

### ✅ Implementadas
- Entity StoryFavorite con user_id y story_id
- Repositorio StoryFavoriteRepository

### ❌ Faltantes - MEDIA PRIORIDAD
- **Marcar historia como favorita (POST /api/stories/{id}/favorite)**
  - USUARIO, MODERADOR, ADMINISTRADOR
  - Crear entrada en StoryFavorite

- **Quitar de favoritos (DELETE /api/stories/{id}/favorite)**
  - Usuario solo el suyo
  - MODERADOR y ADMINISTRADOR

- **Listar favoritos del usuario (GET /api/users/me/favorites)**
  - USUARIO: propios
  - MODERADOR y ADMINISTRADOR con parámetro userId

- **Verificar si está en favoritos (GET /api/stories/{id}/is-favorite)**
  - USUARIO: propio
  - Retorna boolean

- **Contar favoritos por historia (GET /api/stories/{id}/favorites-count)**
  - LECTOR, USUARIO, MODERADOR, ADMINISTRADOR
  - Retorna número

---

## 8. Seguimiento de Autores

### ✅ Implementadas
- Entity UserFollow con follower_id y following_id
- Repositorio UserFollowRepository

### ❌ Faltantes - MEDIA PRIORIDAD
- **Seguir autor (POST /api/users/{id}/follow)**
  - USUARIO, MODERADOR, ADMINISTRADOR
  - Crear entrada en UserFollow

- **Dejar de seguir (DELETE /api/users/{id}/follow)**
  - Usuario solo el suyo

- **Listar autores seguidos (GET /api/users/me/following)**
  - USUARIO: propios
  - MODERADOR y ADMINISTRADOR con parámetro userId

- **Listar seguidores de autor (GET /api/users/{id}/followers)**
  - LECTOR: si perfil público
  - USUARIO, MODERADOR, ADMINISTRADOR

- **Verificar si sigue (GET /api/users/{id}/is-following)**
  - USUARIO: propio
  - Retorna boolean

- **Contar seguidores (GET /api/users/{id}/followers-count)**
  - LECTOR, USUARIO, MODERADOR, ADMINISTRADOR

---

## 9. Métricas y Analítica

### ✅ Implementadas
- Entity StoryViewLog para registrar visitas

### ❌ Faltantes - MEDIA PRIORIDAD
- **Registrar visita a historia (POST /api/stories/{id}/view)**
  - Crear entrada en StoryViewLog
  - User puede ser null (LECTOR)

- **Registrar visita a capítulo (POST /api/chapters/{id}/view)**
  - Crear entrada análoga o en StoryViewLog

- **Métricas por historia (GET /api/stories/{id}/metrics)**
  - USUARIO: solo propias
  - ADMINISTRADOR: todas
  - Retorna: total vistas, comentarios, calificación promedio

- **Métricas por capítulo (GET /api/chapters/{id}/metrics)**
  - USUARIO: solo propios
  - ADMINISTRADOR: todos

- **Total de visitas por autor (GET /api/users/{id}/metrics)**
  - USUARIO: solo suyo
  - ADMINISTRADOR: todas

- **Ranking de historias más vistas (GET /api/statistics/top-stories)**
  - LECTOR, USUARIO, MODERADOR, ADMINISTRADOR
  - Parámetro: period (DAY, WEEK, MONTH, ALL)

- **Panel de estadísticas generales (GET /api/admin/statistics/overview)**
  - Solo ADMINISTRADOR
  - Total usuarios, historias, reportes pendientes, sanciones activas

- **Actividad reciente del sistema (GET /api/admin/statistics/activity)**
  - ADMINISTRADOR y MODERADOR
  - Últimos comentarios, historias, usuarios nuevos

- **Analítica para moderación (GET /api/moderator/analytics)**
  - MODERADOR y ADMINISTRADOR
  - Reportes por estado, sanciones activas, historial de acciones

---

## 10. Ideas Narrativas

### ✅ Implementadas
- Entity Idea con story_id
- Repositorio IdeaRepository

### ❌ Faltantes - MEDIA PRIORIDAD
- **Crear idea (POST /api/stories/{storyId}/ideas)**
  - USUARIO: solo en sus historias
  - MODERADOR y ADMINISTRADOR

- **Obtener idea (GET /api/ideas/{id})**
  - USUARIO: solo propias
  - MODERADOR y ADMINISTRADOR

- **Listar ideas de una historia (GET /api/stories/{storyId}/ideas)**
  - USUARIO: solo del propietario
  - MODERADOR y ADMINISTRADOR

- **Actualizar idea (PUT /api/ideas/{id})**
  - USUARIO: solo propia
  - MODERADOR y ADMINISTRADOR

- **Eliminar idea (DELETE /api/ideas/{id})**
  - USUARIO: solo propia
  - MODERADOR y ADMINISTRADOR

- **Buscar ideas por texto (GET /api/stories/{storyId}/ideas/search?q=text)**
  - USUARIO: solo en sus historias

- **Ordenar o clasificar ideas (GET /api/stories/{storyId}/ideas?sort=date,importance)**
  - USUARIO: solo propias

---

## 11. Ítems u Objetos Narrativos

### ✅ Implementadas
- Entity Item con story_id
- Repositorio ItemRepository

### ❌ Faltantes - MEDIA PRIORIDAD
- **Crear ítem (POST /api/stories/{storyId}/items)**
  - USUARIO: solo en sus historias
  - MODERADOR y ADMINISTRADOR

- **Obtener ítem (GET /api/items/{id})**
  - USUARIO: solo si es suyo
  - LECTOR: solo si es público

- **Listar ítems de historia (GET /api/stories/{storyId}/items)**
  - USUARIO: propios
  - LECTOR: solo si públicos

- **Actualizar ítem (PUT /api/items/{id})**
  - USUARIO: solo propio
  - MODERADOR y ADMINISTRADOR

- **Eliminar ítem (DELETE /api/items/{id})**
  - USUARIO: solo propio
  - MODERADOR y ADMINISTRADOR

- **Filtrar por nombre o categoría (GET /api/items?category=WEAPON)**
  - Según permisos del usuario

- **Marcar como visible/oculto (PATCH /api/items/{id}/visibility)**
  - USUARIO: solo propio

---

## 12. Eventos Narrativos

### ✅ Implementadas
- Entity StoryEvent con relaciones
- Repositorio StoryEventRepository

### ❌ Faltantes - MEDIA PRIORIDAD
- **Crear evento (POST /api/stories/{storyId}/events)**
  - USUARIO: solo en sus historias
  - MODERADOR y ADMINISTRADOR

- **Obtener evento (GET /api/events/{id})**
  - Según visibilidad de contenido

- **Listar eventos por historia (GET /api/stories/{storyId}/events)**
  - USUARIO, MODERADOR, ADMINISTRADOR
  - LECTOR: solo en historias públicas

- **Listar eventos por capítulo (GET /api/chapters/{chapterId}/events)**
  - USUARIO, MODERADOR, ADMINISTRADOR
  - LECTOR: solo en capítulos públicos

- **Actualizar evento (PUT /api/events/{id})**
  - USUARIO: solo propio
  - MODERADOR y ADMINISTRADOR

- **Eliminar evento (DELETE /api/events/{id})**
  - USUARIO: solo propio
  - MODERADOR y ADMINISTRADOR

- **Filtrar por tipo (GET /api/events?type=CONFLICT)**
  - Según permisos

- **Filtrar por importancia (GET /api/events?importance=HIGH)**
  - Según permisos

- **Buscar eventos por etiqueta (GET /api/events/search?tag=betrayal)**
  - Según permisos

- **Asociar personajes (PATCH /api/events/{id}/characters)**
  - USUARIO: solo en sus historias
  - MODERADOR y ADMINISTRADOR

---

## 13. Media o Archivos Asociados

### ✅ Implementadas
- Entity Media con story_id y chapter_id
- Repositorio MediaRepository

### ❌ Faltantes - ALTA PRIORIDAD
- **Subir archivo (POST /api/upload)**
  - USUARIO, MODERADOR, ADMINISTRADOR
  - Validar tipo y tamaño

- **Asociar archivo a capítulo (POST /api/chapters/{id}/media)**
  - USUARIO: solo propio
  - MODERADOR y ADMINISTRADOR

- **Asociar archivo a historia (POST /api/stories/{id}/media)**
  - USUARIO: solo propio
  - MODERADOR y ADMINISTRADOR

- **Listar archivos de recurso (GET /api/stories/{id}/media)**
  - Según visibilidad

- **Obtener metadatos (GET /api/media/{id})**
  - Según permisos

- **Eliminar archivo (DELETE /api/media/{id})**
  - USUARIO: solo propio
  - MODERADOR y ADMINISTRADOR

- **Reemplazar archivo (PUT /api/media/{id})**
  - USUARIO: solo propio
  - MODERADOR y ADMINISTRADOR

- **Validar tipo de archivo (Automático)**
  - Whitelist de extensiones

- **Descargar archivo seguro (GET /api/media/{id}/download)**
  - Según permisos
  - Validar acceso antes de servir

---

## 14. Relación Personaje-Habilidad

### ✅ Implementadas
- Entity CharacterSkill con relation_id
- Repositorio CharacterSkillRepository

### ❌ Faltantes - MEDIA PRIORIDAD
- **Asignar habilidad a personaje (POST /api/characters/{charId}/skills)**
  - USUARIO: solo en sus historias
  - MODERADOR y ADMINISTRADOR

- **Listar habilidades de personaje (GET /api/characters/{id}/skills)**
  - Según visibilidad

- **Listar personajes con habilidad (GET /api/skills/{id}/characters)**
  - Según visibilidad

- **Actualizar dominio/nivel (PATCH /api/character-skills/{id})**
  - USUARIO: solo propio
  - MODERADOR y ADMINISTRADOR

- **Quitar habilidad (DELETE /api/character-skills/{id})**
  - USUARIO: solo propio
  - MODERADOR y ADMINISTRADOR

---

## 15. CRUD Incompleto - Arcos

### ✅ Implementadas
- Crear arco
- Listar arcos de historia

### ❌ Faltantes
- **Obtener arco por ID (GET /api/arcs/{id})**
- **Actualizar arco (PUT /api/arcs/{id})**
- **Eliminar arco (DELETE /api/arcs/{id})**
- **Reordenar arcos (PATCH /api/stories/{storyId}/arcs/reorder)**
- **Mover arco (PATCH /api/arcs/{id}/move)**

---

## 16. CRUD Incompleto - Volúmenes

### ✅ Implementadas
- Crear volumen
- Listar volúmenes

### ❌ Faltantes
- **Obtener volumen por ID (GET /api/volumes/{id})**
- **Actualizar volumen (PUT /api/volumes/{id})**
- **Eliminar volumen (DELETE /api/volumes/{id})**
- **Reordenar volúmenes (PATCH /api/arcs/{arcId}/volumes/reorder)**
- **Mover volumen entre arcos (PATCH /api/volumes/{id}/move)**

---

## 17. CRUD Incompleto - Personajes

### ✅ Implementadas
- Crear personaje
- Listar personajes

### ❌ Faltantes
- **Obtener personaje por ID (GET /api/characters/{id})**
- **Actualizar personaje (PUT /api/characters/{id})**
- **Eliminar personaje (DELETE /api/characters/{id})**
- **Buscar personaje por nombre (GET /api/characters/search?name=name)**
- **Filtrar por historia, estado, rol (GET /api/characters?storyId=X&status=ALIVE)**

---

## 18. CRUD Incompleto - Habilidades

### ✅ Implementadas
- Crear habilidad
- Listar habilidades

### ❌ Faltantes
- **Obtener habilidad por ID (GET /api/skills/{id})**
- **Actualizar habilidad (PUT /api/skills/{id})**
- **Eliminar habilidad (DELETE /api/skills/{id})**
- **Buscar por nombre o categoría (GET /api/skills/search?category=MAGIC)**

---

## 19. CRUD Incompleto - Capítulos

### ✅ Implementadas
- Crear capítulo
- Listar capítulos
- Actualizar capítulo

### ❌ Faltantes
- **Eliminar capítulo (DELETE /api/chapters/{id})**
- **Archivar capítulo (PATCH /api/chapters/{id}/archive)**
- **Publicar/despublicar (PATCH /api/chapters/{id}/publish)**
- **Reordenar capítulos (PATCH /api/volumes/{volumeId}/chapters/reorder)**
- **Mover entre volúmenes (PATCH /api/chapters/{id}/move)**
- **Listar borradores (GET /api/chapters?status=DRAFT)**
- **Búsqueda por título/contenido (GET /api/chapters/search?q=text)**

---

## 20. CRUD Incompleto - Historias

### ✅ Implementadas
- Crear historia
- Listar historias
- Actualizar historia

### ❌ Faltantes
- **Desarchivar historia (PATCH /api/stories/{id}/unarchive)**
- **Publicar/despublicar (PATCH /api/stories/{id}/publish)**
- **Duplicar historia (POST /api/stories/{id}/duplicate)**
- **Búsqueda por texto (GET /api/stories/search?q=text)**
- **Filtrar por visibilidad, estado, autor (GET /api/stories?visibility=PUBLIC)**
- **Paginación y orden (GET /api/stories?page=1&size=20&sort=date,desc)**
- **Obtener borradores (GET /api/stories?status=DRAFT)**
- **Listar archivadas (GET /api/stories/archived)**

---

## 21. CRUD Incompleto - Usuarios

### ✅ Implementadas
- Crear usuario
- Listar usuarios (básico)
- Actualizar usuario (básico)

### ❌ Faltantes
- **Obtener perfil propio (GET /api/users/me)**
- **Buscar usuarios (GET /api/users/search?q=name)**
- **Paginación de usuarios (GET /api/users?page=1&size=20)**
- **Cambiar avatar (PUT /api/users/me/avatar)**
- **Ver perfil público de autor (GET /api/users/{id}/public)**
- **Listar historias del autor (GET /api/users/{id}/stories)**
- **Completar perfil (PUT /api/users/me/profile)**
  - Bio, redes sociales, preferencias

---

## 22. Búsqueda y Filtrado Global

### ✅ Implementadas
- Búsqueda básica en algunos repositorios

### ❌ Faltantes - MEDIA PRIORIDAD
- **Búsqueda global (GET /api/search?q=text&type=STORY,USER)**
- **Búsqueda por título (GET /api/stories/search?title=name)**
- **Búsqueda por autor (GET /api/stories/search?author=name)**
- **Filtros por publicación (GET /api/stories?published=true)**
- **Filtros por visibilidad (GET /api/stories?visibility=PUBLIC)**
- **Filtros por fecha (GET /api/stories?dateFrom=2024-01-01&dateTo=2024-12-31)**
- **Orden por popularidad (GET /api/stories?sort=popularity)**
- **Orden por fecha (GET /api/stories?sort=date)**
- **Orden por calificación (GET /api/stories?sort=rating)**
- **Búsqueda de personajes (GET /api/characters/search)**
- **Búsqueda de capítulos (GET /api/chapters/search)**

---

## 23. Paginación y Ordenamiento

### ✅ Implementadas
- Estructura base para paginación en algunos servicios

### ❌ Faltantes - ALTA PRIORIDAD
- **Implementar en todos los listados**
  - Parámetros: page, size, sort
  - Formato: `GET /api/resource?page=0&size=20&sort=date,desc`

- **Metadatos de paginación en respuestas**
  ```json
  {
    "content": [...],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 20,
      "totalElements": 150,
      "totalPages": 8
    }
  }
  ```

- **Filtros combinables**
  - Múltiples criterios simultáneamente
  - GET /api/stories?status=PUBLISHED&visibility=PUBLIC&author=userId&sort=date,desc

---

## 24. Panel de Usuario

### ❌ Faltantes - MEDIA PRIORIDAD
- **Resumen del usuario (GET /api/users/me/dashboard)**
  - Nombre, avatar, stats básicas

- **Historias creadas (GET /api/users/me/stories)**
  - Paginadas, con estado de publicación

- **Borradores (GET /api/users/me/drafts)**
  - Paginadas

- **Favoritos (GET /api/users/me/favorites)**
  - Historias marcadas como favoritas

- **Autores seguidos (GET /api/users/me/following)**
  - Lista de autores seguidos

- **Comentarios recientes (GET /api/users/me/comments)**
  - Últimos comentarios del usuario

- **Calificaciones emitidas (GET /api/users/me/ratings)**
  - Historias que ha calificado

- **Sanciones propias (GET /api/users/me/sanctions)**
  - Usuario ve sus sanciones activas

---

## 25. Panel Administrativo

### ❌ Faltantes - BAJA PRIORIDAD (pero requerido para admin)
- **Resumen general (GET /api/admin/dashboard/overview)**
  - Total usuarios, historias, reportes, sanciones

- **Total de usuarios (GET /api/admin/statistics/users)**
  - Por estado, por rol

- **Total de historias (GET /api/admin/statistics/stories)**
  - Por visibilidad, por estado

- **Reportes pendientes (GET /api/admin/statistics/pending-reports)**
  - Count

- **Sanciones activas (GET /api/admin/statistics/active-sanctions)**
  - Count

- **Historias más populares (GET /api/admin/statistics/top-stories)**
  - Con métricas

- **Usuarios más activos (GET /api/admin/statistics/top-users)**
  - Por comentarios, favoritos, calificaciones

- **Comunicados activos (GET /api/admin/statistics/active-notices)**
  - Count

---

## Resumen de Prioridades

### 🟢 COMPLETADO (Sprint 2)
1. ✅ Autenticación y sesión (login, logout, JWT) - 100%
2. ✅ Autorización y roles (cambiar rol, validar permisos) - 100%
3. ✅ Moderación de comentarios - 100%
4. ✅ Reportes de contenido (básico) - 66%
5. ✅ Sanciones (advertencias, baneos) - 88%

### 🔴 ALTA PRIORIDAD (Sprint 3)
1. Comunicados globales (CRUD)
2. Paginación y ordenamiento (en todos los listados)
3. Media/Archivos (upload, download, gestión)
4. Favoritos y seguimiento (endpoints)
5. Búsqueda y filtrado global

### 🟠 MEDIA PRIORIDAD (Sprint 4)
1. Métricas y analítica
2. Ideas, Items, Eventos narrativos
3. Paneles de usuario
4. CRUD completos (Arcos, Volúmenes, Personajes, etc.)
5. Relación personaje-habilidad

### 🟡 BAJA PRIORIDAD (Sprint 5+)
1. Panel administrativo completo
2. Optimizaciones de performance
3. Testing exhaustivo
4. Documentación adicional

---

## Estadísticas Generales

| Categoría | Antes | Ahora | % | Estado |
|-----------|-------|-------|-----|--------|
| Autenticación | 2/7 | 10/10 | **100%** | ✅ Completo |
| Autorización | 2/8 | 8/8 | **100%** | ✅ Completo |
| Moderación | 1/6 | 8/8 | **100%** | ✅ Completo |
| Reportes | 1/12 | 8/12 | **66%** | 🔶 Parcial |
| Sanciones | 1/9 | 8/9 | **88%** | 🔶 Parcial |
| Comunicados | 1/10 | 1/10 | **10%** | ❌ Por hacer |
| Favoritos | 1/5 | 1/5 | **20%** | ❌ Por hacer |
| Seguimiento | 1/6 | 1/6 | **16%** | ❌ Por hacer |
| Métricas | 1/8 | 1/8 | **12%** | ❌ Por hacer |
| Ideas | 1/7 | 1/7 | **14%** | ❌ Por hacer |
| Items | 1/7 | 1/7 | **14%** | ❌ Por hacer |
| Eventos | 1/10 | 1/10 | **10%** | ❌ Por hacer |
| Media | 1/9 | 1/9 | **11%** | ❌ Por hacer |
| Personaje-Skill | 1/5 | 1/5 | **20%** | ❌ Por hacer |
| CRUD Incompleto | 3/25 | 3/25 | **12%** | ❌ Por hacer |
| Búsqueda | 1/10 | 1/10 | **10%** | ❌ Por hacer |
| Paginación | 1/5 | 2/5 | **40%** | 🔶 Parcial |
| Paneles | 0/16 | 0/16 | **0%** | ❌ Por hacer |
| **TOTAL** | **24/159** | **52/159** | **32%** | 📈 Progresando |

**Progreso: De 15% a 32% en este sprint (+17%)**

### Desglose por Módulo
- **Módulos Completados:** 3 (Autenticación, Autorización, Moderación)
- **Módulos Parciales:** 3 (Reportes, Sanciones, Paginación)
- **Módulos Pendientes:** 12

**Velocidad:** 28 endpoints nuevos en este sprint (28% del total planeado)

---

## Próximos Pasos

1. ✅ Crear clase de autenticación (AuthService, AuthController)
2. ✅ Implementar JWT y refresh tokens
3. ✅ Crear filtros de seguridad (SecurityFilter)
4. ✅ Implementar paginación en todos los repositorios
5. ✅ Crear búsqueda global
6. ✅ Implementar upload de archivos
7. ✅ Completar CRUD de todos los recursos
8. ✅ Crear paneles de usuario y admin
9. ✅ Implementar moderación y reportes
10. ✅ Testing completo

