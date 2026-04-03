# Quick Start - Proyecto Escritores

## 🚀 Comenzar en 5 Minutos

### 1. Compilar el Proyecto
```bash
mvn clean compile
```

**Resultado esperado:**
```
[INFO] BUILD SUCCESS
```

### 2. Ejecutar la Aplicación
```bash
mvn spring-boot:run
```

**Output esperado:**
```
Started EscritoresApplication in X.XXX seconds
```

### 3. Probar un Endpoint
```bash
# Registro de usuario
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@test.com",
    "username": "testuser",
    "password": "Test123!"
  }'
```

---

## 📋 Checklist de Correcciones Aplicadas

- [x] Enum `Role.java` creado
- [x] Enum `UserStatus.java` creado
- [x] Anotación `@Default` agregada a 3 campos en `AppUser.java`
- [x] Importaciones de `UserStatus` agregadas (5 archivos)
- [x] Referencias a `AppUser.UserStatus` corregidas (9 cambios)
- [x] `AppUserRepository` actualizado con tipos y métodos correctos
- [x] Sin errores de compilación

---

## 🔧 Archivos Corregidos

**Creados:**
- `src/main/java/com/nunclear/escritores/entity/Role.java`
- `src/main/java/com/nunclear/escritores/entity/UserStatus.java`

**Modificados:**
- `AppUser.java` - @Default + imports
- `AppUserRepository.java` - Tipo Long + métodos
- `AuthService.java` - UserStatus imports + referencias
- `AuthorizationService.java` - UserStatus imports + referencias
- `ModerationService.java` - UserStatus referencias
- `AdminController.java` - UserStatus imports + referencias

---

## 📚 Documentación

| Archivo | Propósito |
|---------|-----------|
| `ERRORES_CORREGIDOS.md` | Detalles técnicos de cada error |
| `VALIDACION_FINAL.md` | Checklist de validación |
| `RESUMEN_CORRECCIONES.md` | Resumen visual completo |
| `funcionalidades.md` | Estado de features implementadas |
| `docapi.md` | Documentación de API endpoints |

---

## ✅ Validaciones Rápidas

```bash
# Verificar compilación
mvn clean compile

# Verificar tests
mvn clean test

# Verificar imports de Role
grep -r "import.*Role" src/main/java/ | wc -l

# Verificar imports de UserStatus
grep -r "import.*UserStatus" src/main/java/ | wc -l

# Verificar @Default
grep -r "@Default" src/main/java/
```

---

## 🎯 Próximos Pasos

### Opción A: Continuar Desarrollo
```bash
mvn spring-boot:run
# Aplicación disponible en http://localhost:8080
```

### Opción B: Ejecutar Tests
```bash
mvn clean test
```

### Opción C: Crear JAR para Deployment
```bash
mvn clean package -DskipTests
java -jar target/escritores-0.0.1-SNAPSHOT.jar
```

---

## 🐛 Si Encuentras Errores

### Error: "cannot find symbol - class Role"
**Solución:** Verificar que `Role.java` existe en `src/main/java/com/nunclear/escritores/entity/`

### Error: "cannot find symbol - class UserStatus"
**Solución:** Verificar que `UserStatus.java` existe en `src/main/java/com/nunclear/escritores/entity/`

### Error: "@Builder will ignore"
**Solución:** Verificar que `AppUser.java` tiene `@Default` en los 3 campos

### Error de compilación general
```bash
# Limpiar todo y recompilar
mvn clean compile -U
```

---

## 📱 Endpoints Disponibles

### Autenticación
- `POST /api/auth/register` - Registrar usuario
- `POST /api/auth/login` - Iniciar sesión
- `POST /api/auth/refresh` - Renovar token

### Administración
- `GET /api/admin/users` - Listar usuarios
- `PUT /api/admin/users/{id}/role` - Cambiar rol
- `PUT /api/admin/users/{id}/status` - Cambiar estado

### Moderación
- `PATCH /api/moderator/comments/{id}/hide` - Ocultar comentario
- `GET /api/moderator/queue` - Cola de moderación

---

## 💡 Tips

1. **Mayor información:** Consulta `README.md` para descripción completa
2. **Rutas exactas:** Ver `docapi.md` para todos los endpoints
3. **Instalación detallada:** Ver `SETUP.md` para configuración
4. **Estado features:** Ver `funcionalidades.md` para progreso

---

## 🆘 Soporte

Si necesitas ayuda:
1. Consulta los archivos de documentación
2. Revisa `ERRORES_CORREGIDOS.md` para errores conocidos
3. Verifica que Maven y Java 17+ están instalados

```bash
java -version
mvn -version
```

---

**Estado:** ✅ Listo para compilar y ejecutar  
**Última actualización:** 2 Abril 2026
