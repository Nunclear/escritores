# Documentación del Backend - Escritores

Bienvenido a la documentación técnica del backend del sistema **Escritores**.

## 📚 Contenido

Esta documentación está organizada en las siguientes secciones:

### Guía de Inicio Rápido

- **[Inicio](backend/index.md)** - Descripción general del proyecto
- **[Arquitectura](backend/arquitectura.md)** - Visión general de la arquitectura de capas
- **[Estructura](backend/estructura.md)** - Organización del proyecto

### Configuración

- **[Configuración](backend/configuracion.md)** - Variables de entorno y archivos de configuración
- **[Base de Datos](backend/base-datos.md)** - Schema, conexión y estructura de datos

### Componentes Técnicos

- **[Entidades](backend/entidades.md)** - Mapeo ORM y modelos de datos (26 entidades)
- **[Repositorios](backend/repositorios.md)** - Acceso a datos con Spring Data JPA (25+ repositorios)
- **[Servicios](backend/servicios.md)** - Lógica de negocio (25+ servicios)
- **[Controladores](backend/controladores.md)** - Endpoints REST (15+ controladores)

### API REST

- **[Endpoints REST](backend/endpoints.md)** - Documentación completa de todos los endpoints
- **[Ejemplos de Request/Response](backend/endpoints.md#respuestas-de-error)** - Casos de uso reales

### Seguridad

- **[Autenticación y Autorización](backend/seguridad.md)** - JWT, Spring Security, validaciones
- **[Manejo de Excepciones](backend/seguridad.md#manejo-de-excepciones)** - Excepciones personalizadas

### Despliegue

- **[Ejecución Local](backend/ejecucion-local.md)** - Cómo ejecutar en tu máquina
- **[Compilación](backend/despliegue.md#compilación)** - Build del proyecto
- **[Despliegue en Producción](backend/despliegue.md#despliegue-en-producción)** - Opciones de deployment

### Solución de Problemas

- **[Errores Comunes](backend/errores-comunes.md)** - Problemas frecuentes y soluciones

## 🚀 Inicio Rápido

### Para Desarrolladores

1. **Leer primero**:
   - [Arquitectura](backend/arquitectura.md)
   - [Estructura](backend/estructura.md)

2. **Configurar ambiente**:
   - [Ejecución Local](backend/ejecucion-local.md)

3. **Entender componentes**:
   - [Entidades](backend/entidades.md)
   - [Servicios](backend/servicios.md)
   - [Controladores](backend/controladores.md)

### Para Integradores

1. **Consultar API**:
   - [Endpoints REST](backend/endpoints.md)

2. **Entender autenticación**:
   - [Seguridad](backend/seguridad.md)

3. **Probar en local**:
   - [Ejecucion Local](backend/ejecucion-local.md)

### Para DevOps/SRE

1. **Revisar configuración**:
   - [Configuración](backend/configuracion.md)
   - [Base de Datos](backend/base-datos.md)

2. **Compilación y despliegue**:
   - [Despliegue](backend/despliegue.md)

3. **Solución de problemas**:
   - [Errores Comunes](backend/errores-comunes.md)

## 📊 Estadísticas del Proyecto

| Métrica | Valor |
|---|---|
| Archivos Java | ~310 |
| Entidades JPA | 26 |
| Servicios | 25+ |
| Repositorios | 25+ |
| Controladores | 15+ |
| DTOs (Request) | 40+ |
| DTOs (Response) | 50+ |
| Versión Spring Boot | 4.0.6 |
| Java | 21 |

## 🏗️ Archivos de Documentación

```
docs/backend/
├── index.md                  # Página de inicio
├── arquitectura.md           # Arquitectura del sistema
├── estructura.md             # Estructura del proyecto
├── configuracion.md          # Configuración
├── base-datos.md             # Base de datos
├── entidades.md              # Entidades JPA
├── repositorios.md           # Repositorios
├── servicios.md              # Servicios
├── controladores.md          # Controladores
├── endpoints.md              # API REST
├── seguridad.md              # Seguridad
├── ejecucion-local.md        # Ejecución local
├── despliegue.md             # Despliegue
└── errores-comunes.md        # Errores comunes
```

## 💻 Visualizar Documentación

### Opción 1: MkDocs (Recomendado)

```bash
# Instalar MkDocs
pip install mkdocs mkdocs-material pymdown-extensions mermaid2 table-reader

# Servir documentación localmente
mkdocs serve

# Acceder en navegador
http://localhost:8000
```

### Opción 2: Leer directamente

Todos los archivos están en formato Markdown y se pueden leer directamente:

```bash
cat docs/backend/index.md
```

### Opción 3: GitHub

Si el proyecto está en GitHub, la documentación se puede visualizar directamente.

## 🔍 Buscar en Documentación

La documentación incluye un motor de búsqueda que funciona con MkDocs.

### Búsquedas comunes:

- "JWT" - Autenticación con tokens
- "Story" - Historias
- "transactional" - Transacciones
- "Exception" - Manejo de errores
- "Repository" - Acceso a datos
- "Docker" - Contenedorización

## 📝 Convenciones Utilizadas

### Anotaciones

| Símbolo | Significado |
|---|---|
| ✅ | Recomendado / Correcto |
| ❌ | No recomendado / Incorrecto |
| ⚠️ | Advertencia / Cuidado |
| ℹ️ | Información |
| 💡 | Tip / Consejo |

### Secciones

- **Descripción General**: Qué es el componente
- **Métodos Principales**: Funcionalidades clave
- **Ejemplo de Código**: Código de referencia
- **Mejores Prácticas**: Recomendaciones
- **Errores Comunes**: Problemas conocidos

## 🔗 Referencias Externas

### Documentación Oficial

- [Spring Boot Official](https://spring.io/projects/spring-boot)
- [Spring Security](https://spring.io/projects/spring-security)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Hibernate](https://hibernate.org/)
- [MySQL Documentation](https://dev.mysql.com/doc/)
- [Swagger/OpenAPI](https://swagger.io/)

### Herramientas

- [Postman](https://www.postman.com/) - Para probar API
- [IntelliJ IDEA](https://www.jetbrains.com/idea/) - IDE
- [VS Code](https://code.visualstudio.com/) - Editor
- [Docker](https://www.docker.com/) - Contenedores
- [Git](https://git-scm.com/) - Control de versiones

## 🆘 Soporte

### Si tienes problemas:

1. **Consulta Errores Comunes**:
   - [Errores Comunes y Soluciones](backend/errores-comunes.md)

2. **Revisa Configuración**:
   - [Configuración](backend/configuracion.md)
   - [Ejecución Local](backend/ejecucion-local.md)

3. **Crea un Issue**:
   - Abre un issue en GitHub con:
     - Descripción del problema
     - Pasos para reproducir
     - Logs o screenshots
     - Versión de Java

4. **Contacta al equipo**:
   - Email: dev@nunclear.com
   - Slack: #escritores-backend

## 📄 Licencia

Esta documentación y el código del backend están bajo licencia Apache 2.0.

## 🔄 Actualizaciones

Esta documentación se actualiza con cada release del backend.

- **Última actualización**: Julio 2026
- **Versión backend**: 0.0.1-SNAPSHOT
- **Próximas actualizaciones**: Se planean para cada versión stable

## 📚 Temas Relacionados

- [Frontend Documentation](../frontend/README.md) - Documentación del frontend (si existe)
- [DevOps Documentation](../devops/README.md) - Documentación de infraestructura (si existe)
- [API Specification](https://api.escritores.com/swagger-ui.html) - Swagger UI en vivo

---

**¡Gracias por usar la documentación del backend de Escritores!**

Si tienes sugerencias para mejorar esta documentación, no dudes en contactarnos.
