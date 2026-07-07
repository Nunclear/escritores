# Estructura del Proyecto

## Árbol de Directorios

```
escritores/
├── .mvn/                          # Configuración de Maven Wrapper
│   └── wrapper/
│       └── maven-wrapper.properties
│
├── src/
│   ├── main/
│   │   ├── java/com/nunclear/escritores/
│   │   │   ├── audit/
│   │   │   │   └── DatabaseAuditAspect.java
│   │   │   │
│   │   │   ├── config/
│   │   │   │   ├── CorsConfig.java
│   │   │   │   ├── OpenApiConfig.java
│   │   │   │   └── SecurityConfig.java
│   │   │   │
│   │   │   ├── controller/          (15+ controladores)
│   │   │   │   ├── AdminDashboardController.java
│   │   │   │   ├── AdminUserController.java
│   │   │   │   ├── ArcController.java
│   │   │   │   ├── AuditLogController.java
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── ChapterController.java
│   │   │   │   ├── CharacterController.java
│   │   │   │   ├── CharacterSkillController.java
│   │   │   │   ├── CommentController.java
│   │   │   │   ├── CommentModerationController.java
│   │   │   │   ├── DashboardController.java
│   │   │   │   ├── EventController.java
│   │   │   │   ├── FavoriteController.java
│   │   │   │   ├── FollowController.java
│   │   │   │   ├── GlobalNoticeController.java
│   │   │   │   ├── IdeaController.java
│   │   │   │   ├── ItemController.java
│   │   │   │   ├── MediaController.java
│   │   │   │   ├── MetricsController.java
│   │   │   │   ├── RatingController.java
│   │   │   │   ├── ReportController.java
│   │   │   │   ├── SanctionController.java
│   │   │   │   ├── SkillController.java
│   │   │   │   └── StoryController.java
│   │   │   │
│   │   │   ├── dto/
│   │   │   │   ├── request/         (40+ DTOs de entrada)
│   │   │   │   └── response/        (50+ DTOs de salida)
│   │   │   │
│   │   │   ├── entity/              (26 entidades)
│   │   │   │   ├── AppUser.java
│   │   │   │   ├── Arc.java
│   │   │   │   ├── AuditLog.java
│   │   │   │   ├── Auditable.java
│   │   │   │   ├── BaseToken.java
│   │   │   │   ├── Chapter.java
│   │   │   │   ├── CharacterSkill.java
│   │   │   │   ├── ContentReport.java
│   │   │   │   ├── EmailVerificationToken.java
│   │   │   │   ├── GlobalNotice.java
│   │   │   │   ├── Idea.java
│   │   │   │   ├── Item.java
│   │   │   │   ├── Media.java
│   │   │   │   ├── PasswordResetToken.java
│   │   │   │   ├── Skill.java
│   │   │   │   ├── Story.java
│   │   │   │   ├── StoryCharacter.java
│   │   │   │   ├── StoryComment.java
│   │   │   │   ├── StoryEvent.java
│   │   │   │   ├── StoryFavorite.java
│   │   │   │   ├── StoryRating.java
│   │   │   │   ├── StoryViewLog.java
│   │   │   │   ├── UserChangeHistory.java
│   │   │   │   ├── UserFollow.java
│   │   │   │   ├── UserSanction.java
│   │   │   │   ├── UserSession.java
│   │   │   │   └── Volume.java
│   │   │   │
│   │   │   ├── enums/
│   │   │   │   ├── AccessLevel.java
│   │   │   │   └── AccountState.java
│   │   │   │
│   │   │   ├── exception/
│   │   │   │   ├── BadRequestException.java
│   │   │   │   ├── ConflictException.java
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   └── UnauthorizedException.java
│   │   │   │
│   │   │   ├── repository/          (25+ repositorios)
│   │   │   │   ├── AppUserRepository.java
│   │   │   │   ├── ArcRepository.java
│   │   │   │   ├── AuditLogRepository.java
│   │   │   │   ├── ChapterRepository.java
│   │   │   │   ├── CharacterSkillRepository.java
│   │   │   │   ├── ContentReportRepository.java
│   │   │   │   ├── EmailVerificationTokenRepository.java
│   │   │   │   ├── FavoriteStoryRepository.java
│   │   │   │   ├── GlobalNoticeRepository.java
│   │   │   │   ├── IdeaRepository.java
│   │   │   │   ├── ItemRepository.java
│   │   │   │   ├── MediaRepository.java
│   │   │   │   ├── PasswordResetTokenRepository.java
│   │   │   │   ├── SkillRepository.java
│   │   │   │   ├── StoryCharacterRepository.java
│   │   │   │   ├── StoryCommentRepository.java
│   │   │   │   ├── StoryEventRepository.java
│   │   │   │   ├── StoryFavoriteRepository.java
│   │   │   │   ├── StoryRatingRepository.java
│   │   │   │   ├── StoryRepository.java
│   │   │   │   ├── StoryViewLogRepository.java
│   │   │   │   ├── UserChangeHistoryRepository.java
│   │   │   │   ├── UserFollowRepository.java
│   │   │   │   ├── UserSanctionRepository.java
│   │   │   │   ├── UserSessionRepository.java
│   │   │   │   └── VolumeRepository.java
│   │   │   │
│   │   │   ├── security/
│   │   │   │   ├── CustomUserDetails.java
│   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   └── JwtService.java
│   │   │   │
│   │   │   ├── service/             (25+ servicios)
│   │   │   │   ├── AdminUserService.java
│   │   │   │   ├── ArcService.java
│   │   │   │   ├── AuditLogService.java
│   │   │   │   ├── AuthService.java
│   │   │   │   ├── ChapterService.java
│   │   │   │   ├── CharacterService.java
│   │   │   │   ├── CharacterSkillService.java
│   │   │   │   ├── CommentModerationService.java
│   │   │   │   ├── CommentService.java
│   │   │   │   ├── DashboardService.java
│   │   │   │   ├── EventService.java
│   │   │   │   ├── FavoriteService.java
│   │   │   │   ├── FollowService.java
│   │   │   │   ├── GlobalNoticeService.java
│   │   │   │   ├── IdeaService.java
│   │   │   │   ├── ItemService.java
│   │   │   │   ├── MediaService.java
│   │   │   │   ├── MetricsService.java
│   │   │   │   ├── RatingService.java
│   │   │   │   ├── ReportService.java
│   │   │   │   ├── SanctionService.java
│   │   │   │   ├── SkillService.java
│   │   │   │   ├── StoryService.java
│   │   │   │   ├── UserService.java
│   │   │   │   └── VolumeService.java
│   │   │   │
│   │   │   ├── util/                (Utilidades y helpers)
│   │   │   │
│   │   │   └── EscritoresApplication.java
│   │   │
│   │   └── resources/
│   │       ├── application.properties
│   │       └── (otros recursos)
│   │
│   └── test/
│       └── java/                   (Pruebas unitarias e integración)
│
├── pom.xml                         # Configuración de Maven y dependencias
├── README.md                       # Documentación general del proyecto
└── .gitignore                      # Archivos ignorados por Git
```

## Descripción de Directorios Principales

### `audit/`
Contiene aspectos AOP para auditoría automática.

- **DatabaseAuditAspect.java**: Intercepta operaciones de base de datos y registra cambios

### `config/`
Configuraciones de Spring Boot.

- **SecurityConfig.java**: Configuración de Spring Security, filtros JWT, autorización
- **CorsConfig.java**: Configuración de CORS para permitir solicitudes desde cliente web
- **OpenApiConfig.java**: Configuración de Swagger/OpenAPI para documentación interactiva

### `controller/`
Controladores REST que manejan solicitudes HTTP.

**Total**: 15+ controladores

- **AuthController**: Autenticación, registro, verificación de email, reset de contraseña
- **StoryController**: CRUD de historias, publicación, búsqueda
- **ChapterController**: Gestión de capítulos de historias
- **CommentController**: Gestión de comentarios
- **RatingController**: Calificaciones de historias
- Y más...

### `dto/`
Data Transfer Objects para entrada/salida de datos.

- **request/**: DTOs de entrada (validados con `@Valid`)
  - CreateStoryRequest, UpdateStoryRequest, etc.
- **response/**: DTOs de salida
  - StoryDetailResponse, CreateStoryResponse, etc.

### `entity/`
Entidades JPA mapeadas a tablas de base de datos.

**Total**: 26 entidades

Principales:
- **AppUser**: Usuario de la aplicación
- **Story**: Historia principal
- **Chapter**: Capítulo de una historia
- **StoryComment**: Comentarios en historias
- Y más...

### `enums/`
Enumeraciones para valores fijos.

- **AccessLevel**: user, moderator, admin
- **AccountState**: pending_verification, active, suspended, banned

### `exception/`
Excepciones personalizadas.

- **BadRequestException**: Para solicitudes inválidas
- **UnauthorizedException**: Para falta de autenticación
- **ConflictException**: Para conflictos de datos
- **ResourceNotFoundException**: Para recursos no encontrados
- **GlobalExceptionHandler**: Maneja excepciones globalmente

### `repository/`
Interfaces de repositorio para acceso a datos.

**Total**: 25+ repositorios

Hereda de `JpaRepository<Entity, ID>` de Spring Data JPA.

### `security/`
Componentes de seguridad.

- **JwtService**: Generación y validación de JWT
- **JwtAuthenticationFilter**: Filtro que valida tokens JWT
- **CustomUserDetails**: Detalles del usuario autenticado

### `service/`
Servicios que implementan lógica de negocio.

**Total**: 25+ servicios

Cada servicio:
- Inyecta repositorios necesarios
- Implementa transaccionalidad
- Contiene métodos reutilizables

### `util/`
Utilidades y helpers.

Contiene funciones auxiliares comunes.

## Estadísticas del Proyecto

| Métrica | Valor |
|---|---|
| Archivos Java | ~310 |
| Entidades | 26 |
| Servicios | 25+ |
| Repositorios | 25+ |
| Controladores | 15+ |
| DTOs (Request) | 40+ |
| DTOs (Response) | 50+ |
| Directorio Base | `src/main/java/com/nunclear/escritores/` |

## Convenciones de Nombramiento

### Entidades
- Siguen nombre de tabla en CamelCase
- Prefijo según tipo (UserSession, StoryComment)
- Ejemplo: `Story`, `Chapter`, `AppUser`

### Servicios
- Sufijo `Service`
- Nombre plural o singular según contexto
- Ejemplo: `StoryService`, `UserService`

### Repositorios
- Sufijo `Repository`
- Nombre de entidad + Repository
- Ejemplo: `StoryRepository`, `AppUserRepository`

### Controladores
- Sufijo `Controller`
- Nombre del recurso + Controller
- Ejemplo: `StoryController`, `UserController`

### DTOs
- Request: `<Acción><Entidad>Request`
- Response: `<Entidad><Tipo>Response` o `<Acción><Entidad>Response`
- Ejemplo: `CreateStoryRequest`, `StoryDetailResponse`

### Excepciones
- Sufijo `Exception`
- Nombre descriptivo
- Ejemplo: `ResourceNotFoundException`, `UnauthorizedException`

## Dependencias Principales

Ver `pom.xml` para la lista completa. Resumen:

| Dependencia | Propósito |
|---|---|
| spring-boot-starter-web | Web y REST |
| spring-boot-starter-data-jpa | Acceso a datos |
| spring-boot-starter-security | Autenticación y autorización |
| spring-boot-starter-validation | Validación de entrada |
| jjwt | JWT tokens |
| mysql-connector-j | Driver MySQL |
| lombok | Generación de código |
| springdoc-openapi | Documentación Swagger |
| jacoco-maven-plugin | Cobertura de pruebas |

