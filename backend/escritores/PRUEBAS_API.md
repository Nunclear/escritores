# Guía de Pruebas - API Escritores

**URL Base:** `http://localhost:8080`  
**Fecha:** 2026-04-02

---

## 🔐 Autenticación

### 1. Registrar Usuario

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "usuario@example.com",
    "username": "juanperez",
    "password": "SecurePassword123!"
  }'
```

**Respuesta esperada (201 Created):**
```json
{
  "message": "Usuario registrado exitosamente",
  "user": {
    "id": 1,
    "email": "usuario@example.com",
    "username": "juanperez",
    "role": "USUARIO",
    "status": "ACTIVE",
    "emailVerified": false,
    "createdAt": "2026-04-02T12:00:00"
  }
}
```

### 2. Iniciar Sesión

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "usuario@example.com",
    "password": "SecurePassword123!"
  }'
```

**Respuesta esperada (200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
  "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
  "expiresIn": 3600,
  "user": {
    "id": 1,
    "email": "usuario@example.com",
    "username": "juanperez",
    "role": "USUARIO",
    "status": "ACTIVE",
    "emailVerified": false,
    "createdAt": "2026-04-02T12:00:00"
  }
}
```

**Guardar tokens:**
```bash
# Copiar accessToken y usarlo en las siguientes llamadas
export ACCESS_TOKEN="<token_aqui>"
export REFRESH_TOKEN="<refresh_token_aqui>"
```

### 3. Obtener Usuario Autenticado

```bash
curl -X GET http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer $ACCESS_TOKEN"
```

**Respuesta esperada (200 OK):**
```json
{
  "id": 1,
  "email": "usuario@example.com",
  "username": "juanperez",
  "role": "USUARIO",
  "status": "ACTIVE",
  "emailVerified": false,
  "createdAt": "2026-04-02T12:00:00"
}
```

### 4. Renovar Token

```bash
curl -X POST http://localhost:8080/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "'$REFRESH_TOKEN'"
  }'
```

### 5. Cambiar Contraseña

```bash
curl -X POST http://localhost:8080/api/auth/change-password \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "currentPassword": "SecurePassword123!",
    "newPassword": "NewPassword456!"
  }'
```

### 6. Recuperar Contraseña

```bash
curl -X POST http://localhost:8080/api/auth/forgot-password \
  -H "Content-Type: application/json" \
  -d '{
    "email": "usuario@example.com"
  }'
```

---

## 👨‍💼 Administración

### 7. Listar Todos los Usuarios

```bash
curl -X GET http://localhost:8080/api/admin/users \
  -H "Authorization: Bearer $ACCESS_TOKEN"
```

**Con filtros:**
```bash
# Por rol
curl -X GET "http://localhost:8080/api/admin/users?role=MODERADOR" \
  -H "Authorization: Bearer $ACCESS_TOKEN"

# Por estado
curl -X GET "http://localhost:8080/api/admin/users?status=ACTIVE" \
  -H "Authorization: Bearer $ACCESS_TOKEN"
```

### 8. Obtener Detalles de Usuario

```bash
curl -X GET http://localhost:8080/api/admin/users/2 \
  -H "Authorization: Bearer $ACCESS_TOKEN"
```

### 9. Cambiar Rol de Usuario

```bash
curl -X PUT http://localhost:8080/api/admin/users/2/role \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "role": "MODERADOR"
  }'
```

**Roles válidos:** LECTOR, USUARIO, MODERADOR, ADMINISTRADOR

### 10. Cambiar Estado de Usuario

```bash
curl -X PUT http://localhost:8080/api/admin/users/2/status \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "SUSPENDED"
  }'
```

**Estados válidos:** ACTIVE, SUSPENDED, BANNED

### 11. Ver Mis Permisos

```bash
curl -X GET http://localhost:8080/api/admin/users/me/permissions \
  -H "Authorization: Bearer $ACCESS_TOKEN"
```

### 12. Estadísticas Generales (Admin)

```bash
curl -X GET http://localhost:8080/api/admin/statistics/overview \
  -H "Authorization: Bearer $ACCESS_TOKEN"
```

---

## 🛡️ Moderación

### 13. Crear Reporte

```bash
curl -X POST http://localhost:8080/api/moderator/reports \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "resourceType": "STORY",
    "resourceId": 1,
    "reason": "Contenido inapropiado",
    "description": "La historia contiene violencia gráfica"
  }'
```

**resourceType válidos:** STORY, CHAPTER, COMMENT, USER

### 14. Listar Reportes Pendientes

```bash
curl -X GET http://localhost:8080/api/moderator/reports \
  -H "Authorization: Bearer $ACCESS_TOKEN"

# Con filtro de estado
curl -X GET "http://localhost:8080/api/moderator/reports?status=PENDING" \
  -H "Authorization: Bearer $ACCESS_TOKEN"
```

### 15. Resolver Reporte

```bash
curl -X PATCH http://localhost:8080/api/moderator/reports/1/resolve \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "resolution": "Se removió el contenido inapropiado"
  }'
```

### 16. Rechazar Reporte

```bash
curl -X PATCH http://localhost:8080/api/moderator/reports/1/reject \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "reason": "Reporte sin fundamento"
  }'
```

### 17. Listar Comentarios Ocultos

```bash
curl -X GET http://localhost:8080/api/moderator/comments/hidden \
  -H "Authorization: Bearer $ACCESS_TOKEN"
```

### 18. Ocultar Comentario

```bash
curl -X PATCH http://localhost:8080/api/moderator/comments/1/hide \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "reason": "Lenguaje ofensivo"
  }'
```

### 19. Restaurar Comentario

```bash
curl -X PATCH http://localhost:8080/api/moderator/comments/1/restore \
  -H "Authorization: Bearer $ACCESS_TOKEN"
```

### 20. Ver Cola de Moderación

```bash
curl -X GET http://localhost:8080/api/moderator/queue \
  -H "Authorization: Bearer $ACCESS_TOKEN"
```

---

## ⚠️ Sanciones

### 21. Crear Advertencia

```bash
curl -X POST http://localhost:8080/api/moderator/sanctions/warning \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 2,
    "reason": "Primer incidente - lenguaje inapropiado"
  }'
```

### 22. Aplicar Baneo Temporal

```bash
curl -X POST http://localhost:8080/api/moderator/sanctions/temp-ban \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 2,
    "reason": "Violación de términos de servicio",
    "durationDays": 7
  }'
```

### 23. Listar Sanciones de Usuario

```bash
curl -X GET http://localhost:8080/api/moderator/sanctions/2 \
  -H "Authorization: Bearer $ACCESS_TOKEN"
```

---

## 🧪 Flujo Completo de Prueba

### Escenario: Crear cuenta y obtener datos

```bash
#!/bin/bash

# 1. Registrar nuevo usuario
USER_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "username": "testuser",
    "password": "TestPass123!"
  }')

echo "Registro: $USER_RESPONSE"

# 2. Login
LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "TestPass123!"
  }')

echo "Login: $LOGIN_RESPONSE"

# Extraer token (requiere jq)
TOKEN=$(echo $LOGIN_RESPONSE | jq -r '.accessToken')
echo "Token: $TOKEN"

# 3. Obtener usuario autenticado
curl -s -X GET http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer $TOKEN" | jq '.'

# 4. Ver permisos
curl -s -X GET http://localhost:8080/api/admin/users/me/permissions \
  -H "Authorization: Bearer $TOKEN" | jq '.'
```

---

## ⚙️ Códigos de Error Esperados

| Código | Escenario | Respuesta |
|--------|-----------|----------|
| **200** | Éxito | Datos solicitados |
| **201** | Creado | Nuevo recurso |
| **400** | Bad Request | `{"error": "Email y password son requeridos"}` |
| **401** | Unauthorized | `{"error": "Token no proporcionado"}` |
| **403** | Forbidden | `{"error": "Solo moderadores pueden..."}` |
| **404** | Not Found | `{"error": "Usuario no encontrado"}` |
| **500** | Server Error | `{"error": "Error interno del servidor"}` |

---

## 🔍 Debugging

### Ver respuesta completa con headers

```bash
curl -i -X GET http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer $ACCESS_TOKEN"
```

### Ver solo headers de respuesta

```bash
curl -I -X GET http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer $ACCESS_TOKEN"
```

### Ver request completo

```bash
curl -v -X GET http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer $ACCESS_TOKEN"
```

### Usar Postman o Insomnia

1. Importar colección desde `docapi.md`
2. Configurar variable de entorno `$ACCESS_TOKEN`
3. Ejecutar requests en orden

---

## 📝 Variables de Entorno Útiles

```bash
# Guardar en ~/.bashrc o ~/.zshrc
export API_BASE="http://localhost:8080"
export API_USER_EMAIL="usuario@example.com"
export API_USER_PASS="SecurePassword123!"

# Alias para login rápido
alias api-login='curl -s -X POST $API_BASE/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"$API_USER_EMAIL\",\"password\":\"$API_USER_PASS\"}" | jq ".accessToken" -r'

# Usar: export ACCESS_TOKEN=$(api-login)
```

---

## 🚀 Performance

### Medir tiempo de respuesta

```bash
curl -w "@curl-format.txt" -o /dev/null -s http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer $ACCESS_TOKEN"
```

**Archivo curl-format.txt:**
```
    time_namelookup:  %{time_namelookup}\n
       time_connect:  %{time_connect}\n
    time_appconnect:  %{time_appconnect}\n
   time_pretransfer:  %{time_pretransfer}\n
      time_redirect:  %{time_redirect}\n
 time_starttransfer:  %{time_starttransfer}\n
                    ----------\n
         time_total:  %{time_total}\n
```

---

## ✅ Checklist de Pruebas

- [ ] Registro con email válido
- [ ] Login con credenciales correctas
- [ ] Login con credenciales incorrectas (debe fallar)
- [ ] Token expira después de 1 hora
- [ ] Refresh token genera nuevo access token
- [ ] Usuario sin token recibe 401
- [ ] Usuario sin permisos recibe 403
- [ ] Admin puede cambiar roles
- [ ] Moderador puede crear reportes
- [ ] Sanciones cambian estado de usuario
- [ ] Comentarios se pueden ocultar/restaurar
- [ ] Queue de moderación muestra datos correctos

---

## 📞 Soporte

Para más información:
- Ver `docapi.md` para referencia completa
- Ver `SETUP.md` para instalación
- Ver `README.md` para overview

**Última actualización:** 2026-04-02

