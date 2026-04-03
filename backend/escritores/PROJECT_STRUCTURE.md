# Estructura del Proyecto - Escritores

## 📁 Árbol de Directorios

```
escritores/
│
├── src/
│   ├── main/
│   │   ├── java/com/nunclear/escritores/
│   │   │   │
│   │   │   ├── controller/          # 🎮 Controladores REST
│   │   │   │   ├── AppUserController.java
│   │   │   │   ├── StoryController.java
│   │   │   │   ├── ChapterController.java
│   │   │   │   ├── StoryCommentController.java
│   │   │   │   ├── StoryRatingController.java
│   │   │   │   ├── ArcController.java
│   │   │   │   ├── VolumeController.java
│   │   │   │   ├── CharacterController.java
│   │   │   │   └── SkillController.java
│   │   │   │
│   │   │   ├── service/             # 🔧 Servicios de Negocio
│   │   │   │   ├── AppUserService.java
│   │   │   │   ├── StoryService.java
│   │   │   │   ├── ChapterService.java
│   │   │   │   ├── StoryCommentService.java
│   │   │   │   └── StoryRatingService.java
│   │   │   │
│   │   │   ├── repository/          # 💾 Acceso a Datos (JPA)
│   │   │   │   ├── AppUserRepository.java
│   │   │   │   ├── StoryRepository.java
│   │   │   │   ├── ArcRepository.java
│   │   │   │   ├── VolumeRepository.java
│   │   │   │   ├── ChapterRepository.java
│   │   │   │   ├── StoryCharacterRepository.java
│   │   │   │   ├── SkillRepository.java
│   │   │   │   ├── CharacterSkillRepository.java
│   │   │   │   ├── StoryEventRepository.java
│   │   │   │   ├── IdeaRepository.java
│   │   │   │   ├── ItemRepository.java
│   │   │   │   ├── MediaRepository.java
│   │   │   │   ├── StoryCommentRepository.java
│   │   │   │   ├── StoryRatingRepository.java
│   │   │   │   ├── ContentReportRepository.java
│   │   │   │   ├── UserSanctionRepository.java
│   │   │   │   ├── UserFollowRepository.java
│   │   │   │   ├── StoryFavoriteRepository.java
│   │   │   │   ├── StoryViewLogRepository.java
│   │   │   │   └── GlobalNoticeRepository.java
│   │   │   │
│   │   │   ├── entity/              # 🗂️ Entidades JPA
│   │   │   │   ├── AppUser.java
│   │   │   │   ├── Story.java
│   │   │   │   ├── Arc.java
│   │   │   │   ├── Volume.java
│   │   │   │   ├── Chapter.java
│   │   │   │   ├── StoryCharacter.java
│   │   │   │   ├── Skill.java
│   │   │   │   ├── CharacterSkill.java
│   │   │   │   ├── StoryEvent.java
│   │   │   │   ├── Idea.java
│   │   │   │   ├── Item.java
│   │   │   │   ├── Media.java
│   │   │   │   ├── StoryComment.java
│   │   │   │   ├── StoryRating.java
│   │   │   │   ├── ContentReport.java
│   │   │   │   ├── UserSanction.java
│   │   │   │   ├── UserFollow.java
│   │   │   │   ├── StoryFavorite.java
│   │   │   │   ├── StoryViewLog.java
│   │   │   │   └── GlobalNotice.java
│   │   │   │
│   │   │   ├── dto/                 # 📦 Data Transfer Objects
│   │   │   │   ├── AppUserDTO.java
│   │   │   │   ├── StoryDTO.java
│   │   │   │   ├── ChapterDTO.java
│   │   │   │   ├── StoryCommentDTO.java
│   │   │   │   └── StoryRatingDTO.java
│   │   │   │
│   │   │   ├── config/              # ⚙️ Configuración
│   │   │   │   └── SecurityConfig.java
│   │   │   │
│   │   │   ├── exception/           # 🚨 Manejo de Excepciones
│   │   │   │   (Preparado para implementación futura)
│   │   │   │
│   │   │   └── EscritoresApplication.java  # 🚀 Clase Principal
│   │   │
│   │   └── resources/               # 📋 Archivos de Recursos
│   │       ├── application.properties
│   │       └── db/
│   │           └── migration/       (Preparado para Flyway/Liquibase)
│   │
│   └── test/                        # 🧪 Tests
│       └── java/com/nunclear/escritores/
│           └── EscritoresApplicationTests.java
│
├── scripts/                         # 📊 Scripts de Base de Datos
│   ├── Base-de-datos-ed8wD.sql      (Schema principal)
│   └── test-data.sql                (Datos de prueba)
│
├── pom.xml                          # 📦 Dependencias Maven
├── README.md                        # 📖 Guía General
├── SETUP.md                         # 🔧 Guía de Instalación
├── docapi.md                        # 📚 Documentación de API
├── PROJECT_STATUS.md                # ✅ Estado del Proyecto
├── PROJECT_STRUCTURE.md             # 📁 Este Archivo
├── .gitignore                       # 🔒 Archivos Ignorados
└── LICENSE                          # ⚖️ Licencia

```

---

## 📊 Componentes Principales

### 1️⃣ Controllers (9 archivos)

**Propósito**: Manejar solicitudes HTTP y retornar respuestas

| Controller | Responsabilidad |
|-----------|-----------------|
| AppUserController | Gestión de usuarios |
| StoryController | Gestión de historias |
| ChapterController | Gestión de capítulos |
| StoryCommentController | Gestión de comentarios |
| StoryRatingController | Gestión de calificaciones |
| ArcController | Gestión de arcos |
| VolumeController | Gestión de volúmenes |
| CharacterController | Gestión de personajes |
| SkillController | Gestión de habilidades |

**Endpoints**: 51+ endpoints REST totales

---

### 2️⃣ Services (5 archivos)

**Propósito**: Contiene la lógica de negocio

| Service | Responsabilidad |
|---------|-----------------|
| AppUserService | Lógica de usuarios (validación, cambio password, etc.) |
| StoryService | Lógica de historias (crear, editar, archivar) |
| ChapterService | Lógica de capítulos (cálculo de lectura, palabras) |
| StoryCommentService | Lógica de comentarios (respuestas anidadas) |
| StoryRatingService | Lógica de calificaciones (promedio, validación) |

---

### 3️⃣ Repositories (20 archivos)

**Propósito**: Acceso a base de datos con Spring Data JPA

Características:
- Métodos CRUD automáticos
- Métodos de búsqueda personalizados
- Filtrado por campos específicos
- Consultas nativas cuando es necesario

---

### 4️⃣ Entities (20 archivos)

**Propósito**: Representación de tablas de base de datos

**Características principales**:
- Anotaciones JPA (@Entity, @Table, @ManyToOne, etc.)
- Validaciones Jakarta Validation (@NotBlank, @Email, etc.)
- Timestamps automáticos (@PrePersist, @PreUpdate)
- Relaciones complejas (OneToMany, ManyToMany, etc.)
- Índices de base de datos

**Tablas**:
```
app_user → stories → {arcs → volumes, chapters}
        ↓
        → comments
        → ratings
        → favorites
        → follows
        
story → {characters → skills}
     → {events}
     → {ideas, items}
```

---

### 5️⃣ DTOs (5 archivos)

**Propósito**: Transferencia de datos entre capas

- No exponen la estructura interna de las entidades
- Pueden omitir campos sensibles (contraseñas)
- Incluyen IDs relacionados para composición

---

### 6️⃣ Config (1 archivo)

**SecurityConfig**:
- BCrypt para hash de contraseñas
- CORS configuración
- Preparado para JWT

---

## 🔄 Flujo de Datos

```
Cliente HTTP
    ↓
Controller (recibe solicitud)
    ↓
Service (procesa lógica)
    ↓
Repository (accede a BD)
    ↓
Entity (mapeo ORM)
    ↓
MySQL Database (persistencia)
```

---

## 📋 Configuración de Propiedades

**application.properties** contiene:

```properties
# Servidor
server.port=8080
server.servlet.context-path=/

# Base de Datos
spring.datasource.url=...
spring.datasource.username=...
spring.datasource.password=...

# Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

# Logging
logging.level.root=INFO
logging.level.com.nunclear.escritores=DEBUG

# Jackson
spring.jackson.serialization.write-dates-as-timestamps=false

# Seguridad
security.jwt.secret=...
security.jwt.expiration=...
```

---

## 🗂️ Tablas de Base de Datos

```
20 Tablas Principales:

├── Usuarios
│   ├── app_user (información de usuario)
│   ├── user_sanction (sanciones)
│   └── user_follow (seguimiento)
│
├── Historias
│   ├── story (información general)
│   ├── arc (arcos narrativos)
│   └── volume (volúmenes)
│
├── Contenido
│   ├── chapter (capítulos)
│   ├── story_character (personajes)
│   ├── skill (habilidades)
│   ├── character_skill (relación)
│   ├── story_event (eventos)
│   ├── idea (ideas)
│   └── item (objetos)
│
├── Multimedia
│   └── media (archivos multimedia)
│
├── Interacción
│   ├── story_comment (comentarios)
│   ├── story_rating (calificaciones)
│   ├── story_favorite (favoritos)
│   └── story_view_log (vistas)
│
├── Moderación
│   ├── content_report (reportes)
│   └── global_notice (anuncios)
```

---

## 🚀 Pasos Iniciales

### 1. Instalación
```bash
git clone <repo>
cd escritores
mvn clean install
```

### 2. Configurar BD
```bash
mysql -u root -p < scripts/Base-de-datos-ed8wD.sql
mysql -u root -p historias_db < scripts/test-data.sql
```

### 3. Ejecutar
```bash
mvn spring-boot:run
```

### 4. Probar
```bash
curl http://localhost:8080/api/stories
```

---

## 🔍 Ubicación Rápida

| Necesito... | Ubicación |
|------------|-----------|
| Crear nuevo endpoint | controller/ |
| Agregar lógica | service/ |
| Nueva tabla | entity/ + repository/ |
| Datos de prueba | scripts/test-data.sql |
| Configuración | application.properties |
| Documentación API | docapi.md |
| Guía instalación | SETUP.md |

---

## 📈 Escalabilidad Futura

### Directorio sugerido para nuevos componentes:

```
├── exception/               # Custom Exceptions
├── security/                # Security Filters, JWT
├── validation/              # Custom Validators
├── mapper/                  # Entity ↔ DTO Mappers
├── util/                    # Utility Classes
├── filter/                  # HTTP Filters
├── aspect/                  # AOP Aspects
└── event/                   # Event Listeners
```

---

## 🔐 Consideraciones de Seguridad

- Las contraseñas se almacenan con BCrypt
- Validaciones en entrada de datos
- CORS configurado
- Preparado para JWT
- Autorización por propietario implementada

---

## 📚 Documentación Inline

Cada archivo Java contiene:
- Comentarios de clase
- Javadoc en métodos públicos
- Anotaciones explicativas
- Ejemplos de uso en controladores

---

## 🔗 Relaciones Principales

```
AppUser (1) ──→ (N) Story
   ├─→ (N) StoryComment
   ├─→ (N) StoryRating
   ├─→ (N) StoryFavorite
   ├─→ (N) UserFollow
   └─→ (N) ContentReport

Story (1) ──→ (N) Chapter
   ├─→ (N) Arc
   ├─→ (N) Volume
   ├─→ (N) StoryCharacter
   ├─→ (N) Skill
   ├─→ (N) StoryEvent
   ├─→ (N) Idea
   └─→ (N) Item

StoryCharacter (1) ──→ (N) CharacterSkill ←─ (1) Skill

Chapter (1) ──→ (N) Media
```

---

## 📦 Dependencias Principales

```xml
<!-- Spring Boot -->
spring-boot-starter-web
spring-boot-starter-data-jpa
spring-boot-starter-security
spring-boot-starter-validation

<!-- MySQL -->
mysql-connector-j

<!-- Lombok -->
lombok

<!-- Testing -->
spring-boot-starter-test
```

---

## 💾 Variables de Entorno Recomendadas

Para producción, configurar:
```
DB_URL=jdbc:mysql://host:3306/db
DB_USER=usuario
DB_PASSWORD=contraseña
JWT_SECRET=secreto_muy_largo
JWT_EXPIRATION=86400000
LOGGING_LEVEL=INFO
```

---

## 🎓 Patrones Usados

- **MVC Pattern**: Model-View-Controller
- **Repository Pattern**: Abstracción de datos
- **Service Pattern**: Lógica de negocio
- **DTO Pattern**: Transferencia de datos
- **Dependency Injection**: Spring DI
- **Lazy Loading**: Relaciones JPA
- **Transactional**: Manejo de transacciones

---

**¡La estructura está lista para escalar!** 🚀

Todos los componentes están organizados de forma profesional y pueden crecer fácilmente con nuevas funcionalidades.
