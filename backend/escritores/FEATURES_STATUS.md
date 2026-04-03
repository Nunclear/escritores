# Estado de Funcionalidades - Escritores

Documento que rastrea el progreso de implementación de todas las funcionalidades especificadas.

## 1. Autenticación y Sesión ✅

| Funcionalidad | Estado | Endpoint |
|---------------|--------|----------|
| Registro de usuario | ✅ Implementado | `POST /auth/register` |
| Inicio de sesión | ✅ Implementado | `POST /auth/login` |
| Renovación de token | ✅ Implementado | `POST /auth/refresh` |
| Obtener usuario autenticado | ✅ Implementado | `GET /auth/me` |
| Recuperación de contraseña | ✅ Implementado | `POST /auth/forgot-password` |
| Restablecimiento de contraseña | ✅ Implementado | `POST /auth/reset-password` |
| Cambio de contraseña | ✅ Implementado | `POST /auth/change-password` |
| Verificación de email | ✅ Implementado | `POST /auth/verify-email` |
| Envío de verificación | ✅ Implementado | `POST /auth/send-verification` |
| Cierre de sesión | ✅ Implementado | `POST /auth/logout` |
| Invalidación de sesiones | ❌ No implementado | - |
| Bloqueo por intentos fallidos | ❌ No implementado | - |

---

## 2. Autorización y Gestión de Roles ⚠️

| Funcionalidad | Estado | Endpoint |
|---------------|--------|----------|
| Cambiar rol de usuario | ✅ Implementado | `PUT /admin/users/{id}/role` |
| Cambiar estado de usuario | ✅ Implementado | `PUT /admin/users/{id}/status` |
| Obtener permisos del usuario | ✅ Implementado | `GET /admin/users/me/permissions` |
| Listar usuarios por rol | ⚠️ Parcial | `GET /users?role=...` |
| Listar usuarios por estado | ⚠️ Parcial | `GET /users?status=...` |
| Ver historial de cambios | ❌ No implementado | - |
| Revertir sanciones | ❌ No implementado | - |

---

## 3. Moderación de Comentarios ✅

| Funcionalidad | Estado | Endpoint |
|---------------|--------|----------|
| Ocultar comentario | ✅ Implementado | `PATCH /moderator/comments/{id}/hide` |
| Restaurar comentario | ✅ Implementado | `PATCH /moderator/comments/{id}/restore` |
| Listar comentarios ocultos | ✅ Implementado | `GET /moderator/comments/hidden` |
| Marcar como revisado | ✅ Implementado | Incluido en resolve |
| Cola de moderación | ✅ Implementado | `GET /moderator/queue` |

---

## 4. Reportes de Contenido ✅

| Funcionalidad | Estado | Endpoint |
|---------------|--------|----------|
| Reportar historia | ✅ Implementado | `POST /moderator/reports` |
| Reportar capítulo | ✅ Implementado | `POST /moderator/reports` |
| Reportar comentario | ✅ Implementado | `POST /moderator/reports` |
| Reportar usuario | ⚠️ Parcial | `POST /moderator/reports` |
| Listar reportes pendientes | ✅ Implementado | `GET /moderator/reports` |
| Listar reportes por estado | ✅ Implementado | `GET /moderator/reports?status=...` |
| Ver detalle de reporte | ✅ Implementado | Incluido en lista |
| Resolver reporte | ✅ Implementado | `PATCH /moderator/reports/{id}/resolve` |
| Rechazar reporte | ✅ Implementado | `PATCH /moderator/reports/{id}/reject` |
| Historial de reportes | ❌ No implementado | - |

---

## 5. Sanciones y Baneos ✅

| Funcionalidad | Estado | Endpoint |
|---------------|--------|----------|
| Crear advertencia | ✅ Implementado | `POST /moderator/sanctions/warning` |
| Aplicar baneo temporal | ✅ Implementado | `POST /moderator/sanctions/temp-ban` |
| Aplicar baneo permanente | ❌ No implementado | - |
| Levantar sanción | ❌ No implementado | - |
| Listar sanciones de usuario | ✅ Implementado | `GET /moderator/sanctions/{userId}` |
| Historial disciplinario | ❌ No implementado | - |

---

## 6. Comunicados Globales ❌

| Funcionalidad | Estado | Endpoint |
|---------------|--------|----------|
| Crear comunicado | ❌ No implementado | - |
| Editar comunicado | ❌ No implementado | - |
| Activar/desactivar | ❌ No implementado | - |
| Programar fechas | ❌ No implementado | - |
| Listar comunicados | ❌ No implementado | - |

---

## 7. Favoritos ❌

| Funcionalidad | Estado | Endpoint |
|---------------|--------|----------|
| Marcar como favorita | ❌ No implementado | - |
| Quitar de favoritos | ❌ No implementado | - |
| Listar favoritos | ❌ No implementado | - |
| Verificar si es favorita | ❌ No implementado | - |
| Contar favoritos | ❌ No implementado | - |

---

## 8. Seguimiento de Autores ❌

| Funcionalidad | Estado | Endpoint |
|---------------|--------|----------|
| Seguir autor | ❌ No implementado | - |
| Dejar de seguir | ❌ No implementado | - |
| Listar autores seguidos | ❌ No implementado | - |
| Listar seguidores | ❌ No implementado | - |
| Verificar si sigue | ❌ No implementado | - |

---

## 9. Métricas y Analítica ❌

| Funcionalidad | Estado | Endpoint |
|---------------|--------|----------|
| Registrar visita | ❌ No implementado | - |
| Métricas por historia | ❌ No implementado | - |
| Métricas por capítulo | ❌ No implementado | - |
| Total de visitas | ❌ No implementado | - |
| Ranking de historias | ❌ No implementado | - |
| Panel de estadísticas | ❌ No implementado | - |

---

## 10. Ideas Narrativas ❌

| Funcionalidad | Estado | Endpoint |
|---------------|--------|----------|
| Crear idea | ❌ No implementado | - |
| Obtener idea | ❌ No implementado | - |
| Listar ideas | ❌ No implementado | - |
| Actualizar idea | ❌ No implementado | - |
| Eliminar idea | ❌ No implementado | - |
| Búsqueda | ❌ No implementado | - |

---

## 11. Ítems Narrativos ❌

| Funcionalidad | Estado | Endpoint |
|---------------|--------|----------|
| Crear ítem | ❌ No implementado | - |
| Obtener ítem | ❌ No implementado | - |
| Listar ítems | ❌ No implementado | - |
| Actualizar ítem | ❌ No implementado | - |
| Eliminar ítem | ❌ No implementado | - |

---

## 12. Eventos Narrativos ❌

| Funcionalidad | Estado | Endpoint |
|---------------|--------|----------|
| Crear evento | ❌ No implementado | - |
| Obtener evento | ❌ No implementado | - |
| Listar eventos | ❌ No implementado | - |
| Actualizar evento | ❌ No implementado | - |
| Eliminar evento | ❌ No implementado | - |
| Filtrar por tipo | ❌ No implementado | - |

---

## 13. Media y Archivos ❌

| Funcionalidad | Estado | Endpoint |
|---------------|--------|----------|
| Subir archivo | ❌ No implementado | - |
| Asociar a capítulo | ❌ No implementado | - |
| Listar archivos | ❌ No implementado | - |
| Descargar archivo | ❌ No implementado | - |
| Eliminar archivo | ❌ No implementado | - |

---

## 14. Relación Personaje-Habilidad ❌

| Funcionalidad | Estado | Endpoint |
|---------------|--------|----------|
| Asignar habilidad | ❌ No implementado | - |
| Listar habilidades de personaje | ❌ No implementado | - |
| Actualizar dominio | ❌ No implementado | - |
| Quitar habilidad | ❌ No implementado | - |

---

## 15. CRUD Incompleto en Módulos Existentes ⚠️

### Arcos
| Funcionalidad | Estado | Endpoint |
|---------------|--------|----------|
| Crear | ✅ Implementado | `POST /arcs` |
| Obtener por ID | ❌ No implementado | - |
| Listar | ✅ Implementado | `GET /arcs/story/{storyId}` |
| Actualizar | ❌ No implementado | - |
| Eliminar | ❌ No implementado | - |
| Reordenar | ❌ No implementado | - |

### Volúmenes
| Funcionalidad | Estado | Endpoint |
|---------------|--------|----------|
| Crear | ✅ Implementado | `POST /volumes` |
| Obtener por ID | ❌ No implementado | - |
| Listar | ✅ Implementado | `GET /volumes/story/{storyId}` |
| Actualizar | ❌ No implementado | - |
| Eliminar | ❌ No implementado | - |

### Personajes
| Funcionalidad | Estado | Endpoint |
|---------------|--------|----------|
| Crear | ✅ Implementado | `POST /characters` |
| Obtener por ID | ❌ No implementado | - |
| Listar | ✅ Implementado | `GET /characters/story/{storyId}` |
| Actualizar | ❌ No implementado | - |
| Eliminar | ❌ No implementado | - |
| Búsqueda | ❌ No implementado | - |

### Habilidades
| Funcionalidad | Estado | Endpoint |
|---------------|--------|----------|
| Crear | ✅ Implementado | `POST /skills` |
| Obtener por ID | ❌ No implementado | - |
| Listar | ✅ Implementado | `GET /skills/story/{storyId}` |
| Actualizar | ❌ No implementado | - |
| Eliminar | ❌ No implementado | - |

### Capítulos
| Funcionalidad | Estado | Endpoint |
|---------------|--------|----------|
| Crear | ✅ Implementado | `POST /chapters` |
| Obtener | ✅ Implementado | `GET /chapters/{id}` |
| Listar | ✅ Implementado | `GET /chapters/story/{storyId}` |
| Listar publicados | ✅ Implementado | `GET /chapters/story/{storyId}/published` |
| Actualizar | ✅ Implementado | `PUT /chapters/{id}` |
| Eliminar | ❌ No implementado | - |
| Archivar | ❌ No implementado | - |
| Reordenar | ❌ No implementado | - |

### Historias
| Funcionalidad | Estado | Endpoint |
|---------------|--------|----------|
| Crear | ✅ Implementado | `POST /stories` |
| Obtener | ✅ Implementado | `GET /stories/{id}` |
| Listar | ✅ Implementado | `GET /stories` |
| Listar por usuario | ✅ Implementado | `GET /stories/user/{userId}` |
| Listar por visibilidad | ✅ Implementado | `GET /stories/visibility/{visibility}` |
| Actualizar | ✅ Implementado | `PUT /stories/{id}` |
| Archivar | ✅ Implementado | `POST /stories/{id}/archive` |
| Eliminar | ✅ Implementado | `DELETE /stories/{id}` |
| Desarchivar | ❌ No implementado | - |
| Duplicar | ❌ No implementado | - |
| Búsqueda avanzada | ❌ No implementado | - |

---

## 16. Búsqueda y Filtrado ❌

| Funcionalidad | Estado | Endpoint |
|---------------|--------|----------|
| Búsqueda global | ❌ No implementado | - |
| Búsqueda por título | ❌ No implementado | - |
| Búsqueda por autor | ❌ No implementado | - |
| Filtros por estado | ⚠️ Parcial | `GET /stories/visibility/{visibility}` |
| Búsqueda de personajes | ❌ No implementado | - |
| Búsqueda de capítulos | ❌ No implementado | - |

---

## 17. Paginación y Ordenamiento ⚠️

| Funcionalidad | Estado | Detalles |
|---------------|--------|----------|
| Paginación en usuarios | ⚠️ Parcial | `?page=0&size=20` |
| Paginación en historias | ❌ No implementado | - |
| Ordenamiento | ❌ No implementado | - |
| Filtros combinables | ❌ No implementado | - |

---

## 18. Panel de Usuario ❌

| Funcionalidad | Estado | Endpoint |
|---------------|--------|----------|
| Resumen del usuario | ❌ No implementado | - |
| Historias creadas | ⚠️ Parcial | `GET /stories/user/{userId}` |
| Borradores | ❌ No implementado | - |
| Favoritos | ❌ No implementado | - |
| Autores seguidos | ❌ No implementado | - |
| Comentarios recientes | ❌ No implementado | - |
| Calificaciones | ❌ No implementado | - |

---

## 19. Panel Administrativo ❌

| Funcionalidad | Estado | Endpoint |
|---------------|--------|----------|
| Resumen general | ❌ No implementado | - |
| Estadísticas | ❌ No implementado | - |
| Actividad reciente | ❌ No implementado | - |
| Históricos | ❌ No implementado | - |

---

## Resumen de Progreso

```
Total de Funcionalidades: 121
✅ Implementadas: 36 (29.8%)
⚠️ Parcialmente Implementadas: 10 (8.3%)
❌ No Implementadas: 75 (61.9%)
```

### Por Categoría

| Categoría | Implementado | Parcial | Pendiente | Total |
|-----------|--------------|---------|-----------|-------|
| Autenticación | 9 | 0 | 3 | 12 |
| Autorización | 3 | 2 | 2 | 7 |
| Moderación | 2 | 3 | 0 | 5 |
| Reportes | 6 | 1 | 2 | 9 |
| Sanciones | 3 | 0 | 2 | 5 |
| Comunicados | 0 | 0 | 5 | 5 |
| Favoritos | 0 | 0 | 5 | 5 |
| Seguidores | 0 | 0 | 5 | 5 |
| Métricas | 0 | 0 | 7 | 7 |
| Ideas | 0 | 0 | 6 | 6 |
| Ítems | 0 | 0 | 5 | 5 |
| Eventos | 0 | 0 | 7 | 7 |
| Media | 0 | 0 | 5 | 5 |
| Personaje-Habilidad | 0 | 0 | 4 | 4 |
| CRUD Incompleto | 16 | 4 | 20 | 40 |
| Búsqueda | 0 | 0 | 6 | 6 |
| Paginación | 1 | 1 | 2 | 4 |
| Paneles | 1 | 1 | 4 | 6 |

---

## Prioridades Sugeridas

### Fase 1 (Alto Impacto, Corto Plazo)
1. ✅ Completar CRUD de módulos existentes (Arcos, Volúmenes, Personajes, Habilidades)
2. ✅ Implementar búsqueda y filtrado básico
3. ✅ Agregar paginación a listados
4. ✅ Comunicados Globales (simple pero importante)
5. ✅ Favoritos (mejora experiencia de usuario)

### Fase 2 (Funcionalidades Narrativas)
1. Ideas Narrativas
2. Ítems Narrativos
3. Eventos Narrativos
4. Relación Personaje-Habilidad
5. Media y Archivos

### Fase 3 (Analítica y Paneles)
1. Métricas y Visitas
2. Seguimiento de Autores
3. Panel de Usuario
4. Panel Administrativo
5. Históriales y Auditoría

---

**Documento generado:** 2024-04-02  
**Versión:** 1.0
