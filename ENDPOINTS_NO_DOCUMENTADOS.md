# ⚠️ ENDPOINTS IMPLEMENTADOS PERO NO DOCUMENTADOS

**Estado:** Identificados 7 Controllers con endpoints faltantes de documentar  
**Total Estimado:** 8-15 endpoints sin documentar  
**Prioridad:** CRÍTICA  

---

## 📊 LISTA DE CONTROLLERS CON ENDPOINTS NO DOCUMENTADOS

### 1. **ReportController.java**
Controllers implementado pero sin documentación en doc_api.txt

**Endpoints Estimados:**
- GET /api/reports
- GET /api/reports/{id}
- POST /api/reports
- PUT /api/reports/{id}
- DELETE /api/reports/{id}
- GET /api/reports/search
- POST /api/reports/{id}/resolve
- GET /api/reports/pending

**Acción:** Documentar especificaciones técnicas

---

### 2. **SanctionController.java**
Controllers implementado pero sin documentación en doc_api.txt

**Endpoints Estimados:**
- GET /api/sanctions
- GET /api/sanctions/{id}
- POST /api/sanctions
- PUT /api/sanctions/{id}
- DELETE /api/sanctions/{id}
- GET /api/sanctions/{userId}
- POST /api/sanctions/{id}/appeal
- GET /api/sanctions/{id}/history

**Acción:** Documentar especificaciones técnicas

---

### 3. **GlobalNoticeController.java**
Controllers implementado pero sin documentación en doc_api.txt

**Endpoints Estimados:**
- GET /api/notices
- GET /api/notices/{id}
- POST /api/notices (Admin)
- PUT /api/notices/{id} (Admin)
- DELETE /api/notices/{id} (Admin)
- GET /api/notices/active

**Acción:** Documentar especificaciones técnicas

---

### 4. **MediaController.java**
Controllers implementado pero sin documentación en doc_api.txt

**Endpoints Estimados:**
- POST /api/media/upload
- GET /api/media/{id}
- DELETE /api/media/{id}
- POST /api/media/{id}/resize
- GET /api/media/search

**Acción:** Documentar especificaciones técnicas

---

### 5. **MetricsController.java**
Controllers implementado pero sin documentación en doc_api.txt

**Endpoints Estimados:**
- GET /api/metrics/dashboard
- GET /api/metrics/stories
- GET /api/metrics/users
- GET /api/metrics/engagement
- GET /api/metrics/trends

**Acción:** Documentar especificaciones técnicas

---

### 6. **ItemController.java**
Controllers implementado pero sin documentación en doc_api.txt

**Endpoints Estimados:**
- GET /api/items
- GET /api/items/{id}
- POST /api/items
- PUT /api/items/{id}
- DELETE /api/items/{id}

**Acción:** Documentar especificaciones técnicas

---

### 7. **IdeaController.java**
Controllers parcialmente documentado en doc_api.txt

**Endpoints Estimados (completar):**
- GET /api/ideas
- GET /api/ideas/{id}
- POST /api/ideas
- PUT /api/ideas/{id}
- DELETE /api/ideas/{id}
- GET /api/ideas/search
- POST /api/ideas/{id}/approve
- POST /api/ideas/{id}/reject

**Acción:** Completar documentación

---

## 🎯 RESUMEN POR ACCIÓN

| Controller | Status | Acción |
|------------|--------|--------|
| ReportController | ❌ No Documentado | Documentar 8 endpoints |
| SanctionController | ❌ No Documentado | Documentar 8 endpoints |
| GlobalNoticeController | ❌ No Documentado | Documentar 6 endpoints |
| MediaController | ❌ No Documentado | Documentar 5 endpoints |
| MetricsController | ❌ No Documentado | Documentar 5 endpoints |
| ItemController | ❌ No Documentado | Documentar 5 endpoints |
| IdeaController | ⚠️ Parcial | Completar 8 endpoints |
| **TOTAL** | **7 controllers** | **40+ endpoints** |

---

## 📋 CHECKLIST DE DOCUMENTACIÓN

Para cada controller NO documentado, necesitas:

- [ ] **Análisis del código** - Revisar implementación actual
- [ ] **Especificación técnica** - Definir endpoints exactos
- [ ] **Request/Response DTOs** - Documentar estructura
- [ ] **Error codes** - Códigos de error específicos
- [ ] **Permisos/Roles** - Autenticación requerida
- [ ] **Ejemplos cURL** - Casos de uso comunes
- [ ] **Tests** - Validar cobertura
- [ ] **Actualizar doc_api.txt** - Añadir secciones

---

## 🚀 PLAN DE EJECUCIÓN

### Fase 1: Análisis (2-3 días)
1. Leer código fuente de cada controller
2. Identificar endpoints exactos
3. Documentar DTOs y modelos

### Fase 2: Especificación (3-4 días)
4. Crear formato estándar de documentación
5. Escribir especificaciones técnicas
6. Documentar códigos de error

### Fase 3: Documentación (2-3 días)
7. Crear ejemplos cURL
8. Escribir casos de uso
9. Actualizar doc_api.txt

### Fase 4: Validación (1-2 días)
10. Tests de integración
11. Revisión y QA
12. Publicación

**Tiempo total estimado:** 2-3 semanas

---

## 📝 FORMATO ESTÁNDAR PARA DOCUMENTACIÓN

Cuando documentes nuevos endpoints, sigue este formato:

```
## API NAME

### Endpoint 1: [METHOD] /api/path

**Descripción:** Breve descripción de funcionalidad

**Autenticación:** Required/Optional  
**Rol Requerido:** ROLE_USER, ROLE_ADMIN, etc.

**Request:**
```json
{
  "field": "value"
}
```

**Response (200):**
```json
{
  "id": 1,
  "status": "success"
}
```

**Error Codes:**
- 400: Validación fallida
- 401: No autenticado
- 403: No tiene permiso
- 404: No encontrado
- 500: Error servidor

**Ejemplo cURL:**
```bash
curl -X GET http://localhost:8080/api/path \
  -H "Authorization: Bearer TOKEN"
```
```

---

## 🎯 PRIORIDAD POR CONTROLLER

1. **CRÍTICA:** ReportController, SanctionController (gestión de contenido)
2. **ALTA:** GlobalNoticeController, MediaController (funcionalidad core)
3. **MEDIA:** MetricsController, ItemController (soporte)
4. **BAJA:** IdeaController (completar parcial)

---

**Estado:** LISTO PARA DOCUMENTACIÓN  
**Próximo paso:** Comenzar con ReportController
