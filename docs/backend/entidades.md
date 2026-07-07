# Entidades

## Descripción General

Las entidades son clases Java anotadas con `@Entity` que representan tablas en la base de datos. Implementan **soft delete** y auditoría automática de fechas.

**Total de Entidades**: 26

## Entidades Principales

### 1. AppUser
**Tabla**: `app_user`  
**Propósito**: Representa un usuario del sistema

```java
@Entity
@Table(name = "app_user")
@SQLDelete(sql = "UPDATE app_user SET deleted = true, deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter @Setter
public class AppUser {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false, unique = true, length = 100)
    private String loginName;
    
    @Column(nullable = false, unique = true, length = 255)
    private String emailAddress;
    
    @Column(length = 255)
    private String pendingEmailAddress;
    
    @Column(nullable = false, length = 255)
    private String passwordHash;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AccessLevel accessLevel = AccessLevel.user;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AccountState accountState = AccountState.pending_verification;
    
    private String displayName;
    private String bioText;
    private String avatarUrl;
    private LocalDateTime lastLoginAt;
    private LocalDateTime emailVerifiedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private Boolean deleted = false;
    
    @PrePersist
    public void prePersist() { /* Auditoría automática */ }
    
    @PreUpdate
    public void preUpdate() { /* Auditoría automática */ }
}
```

**Campos Clave**:
- `loginName`: Nombre único de login
- `emailAddress`: Email único
- `passwordHash`: Contraseña codificada con BCrypt
- `accessLevel`: USER, MODERATOR, ADMIN
- `accountState`: pending_verification, active, suspended, banned

### 2. Story
**Tabla**: `story`  
**Propósito**: Historia principal del sistema

```java
@Entity
@Table(name = "story")
@SQLDelete(sql = "UPDATE story SET deleted = true, deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter @Setter
public class Story {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "owner_user_id", nullable = false)
    private Integer ownerUserId;
    
    @Column(nullable = false, length = 255)
    private String title;
    
    @Column(unique = true, length = 255)
    private String slugText;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private String coverImageUrl;
    private String visibilityState;
    private String publicationState;
    private Boolean allowFeedback = true;
    private Boolean allowScores = true;
    private LocalDate startedOn;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime archivedAt;
    private Boolean deleted = false;
    private LocalDateTime deletedAt;
}
```

### 3. Chapter
**Tabla**: `chapter`  
**Propósito**: Capítulo de una historia

**Propiedades**:
- `storyId`: Referencia a Story
- `volumeId`: Referencia a Volume (opcional)
- `arcId`: Referencia a Arc (opcional)
- `title`: Título del capítulo
- `contentText`: Contenido del capítulo
- `sequenceNumber`: Número de secuencia
- `publicationState`: draft, published
- `publishedAt`: Fecha de publicación

### 4. Volume
**Tabla**: `volume`  
**Propósito**: Volumen dentro de una historia

**Propiedades**:
- `storyId`: Referencia a Story
- `title`: Título del volumen
- `description`: Descripción
- `sequenceNumber`: Número de volumen

### 5. Arc
**Tabla**: `arc`  
**Propósito**: Arco narrativo dentro de una historia

**Propiedades**:
- `storyId`: Referencia a Story
- `title`: Título del arco
- `description`: Descripción
- `sequenceNumber`: Número de arco

## Entidades de Contenido

### 6. StoryCharacter
**Tabla**: `story_character`  
**Propósito**: Personaje dentro de una historia

**Propiedades**:
- `storyId`: Referencia a Story
- `characterName`: Nombre del personaje
- `characterDescription`: Descripción
- `physicalDescription`: Descripción física
- `personalityTraits`: Rasgos de personalidad

### 7. Skill
**Tabla**: `skill`  
**Propósito**: Habilidad o poder de personaje

**Propiedades**:
- `storyId`: Referencia a Story
- `skillName`: Nombre de habilidad
- `skillDescription`: Descripción

### 8. CharacterSkill
**Tabla**: `character_skill`  
**Propósito**: Asignación de habilidad a personaje

**Propiedades**:
- `storyCharacterId`: Referencia a StoryCharacter
- `skillId`: Referencia a Skill
- `proficiencyLevel`: Nivel de dominio

### 9. StoryEvent
**Tabla**: `story_event`  
**Propósito**: Evento importante en la historia

**Propiedades**:
- `storyId`: Referencia a Story
- `chapterId`: Referencia a Chapter
- `eventTitle`: Título del evento
- `eventDescription`: Descripción
- `eventDate`: Fecha del evento

### 10. Item
**Tabla**: `item`  
**Propósito**: Objeto o ítem dentro de la historia

**Propiedades**:
- `storyId`: Referencia a Story
- `itemName`: Nombre del ítem
- `itemDescription`: Descripción
- `itemType`: Tipo de ítem

### 11. Media
**Tabla**: `media`  
**Propósito**: Multimedia (imágenes, audio, etc.)

**Propiedades**:
- `chapterId`: Referencia a Chapter
- `mediaUrl`: URL del archivo
- `mediaType`: Tipo de media (image, audio, video)
- `fileSize`: Tamaño en bytes
- `uploadedBy`: ID del usuario que subió

## Entidades de Interacción

### 12. StoryComment
**Tabla**: `story_comment`  
**Propósito**: Comentario en una historia

**Propiedades**:
- `storyId`: Referencia a Story
- `chapterId`: Referencia a Chapter (opcional)
- `commenterUserId`: Referencia a AppUser
- `parentCommentId`: Referencia a StoryComment (para replies)
- `contentText`: Contenido del comentario
- `isModerated`: Si fue moderado
- `createdAt`, `updatedAt`

### 13. StoryRating
**Tabla**: `story_rating`  
**Propósito**: Calificación de una historia

**Propiedades**:
- `storyId`: Referencia a Story
- `raterUserId`: Referencia a AppUser
- `ratingValue`: Calificación (1-5 típicamente)
- Constraint UNIQUE: (storyId, raterUserId)

### 14. StoryFavorite
**Tabla**: `story_favorite`  
**Propósito**: Historia marcada como favorita

**Propiedades**:
- `storyId`: Referencia a Story
- `userId`: Referencia a AppUser
- Constraint UNIQUE: (storyId, userId)

### 15. StoryViewLog
**Tabla**: `story_view_log`  
**Propósito**: Log de visualizaciones de historia

**Propiedades**:
- `storyId`: Referencia a Story
- `userId`: Referencia a AppUser
- `viewedAt`: Fecha y hora

### 16. UserFollow
**Tabla**: `user_follow`  
**Propósito**: Seguimiento entre usuarios

**Propiedades**:
- `followerUserId`: Referencia a AppUser (quien sigue)
- `followedUserId`: Referencia a AppUser (a quien sigue)
- Constraint UNIQUE: (followerUserId, followedUserId)

## Entidades de Seguridad

### 17. UserSession
**Tabla**: `user_session`  
**Propósito**: Sesión activa de usuario

**Propiedades**:
- `userId`: Referencia a AppUser
- `sessionIdentifier`: ID único de sesión
- `refreshTokenHash`: Hash del refresh token
- `expiresAt`: Fecha de expiración
- `revokedAt`: Fecha de revocación (logout)
- `ipAddress`: IP de la sesión
- `userAgentText`: User agent del navegador

### 18. EmailVerificationToken
**Tabla**: `email_verification_token`  
**Propósito**: Token para verificar email

**Propiedades**:
- `userId`: Referencia a AppUser
- `tokenHash`: Hash del token
- `expiresAt`: Fecha de expiración
- `verifiedAt`: Fecha de verificación

### 19. PasswordResetToken
**Tabla**: `password_reset_token`  
**Propósito**: Token para reset de contraseña

**Propiedades**:
- `userId`: Referencia a AppUser
- `tokenHash`: Hash del token
- `expiresAt`: Fecha de expiración
- `usedAt`: Fecha de uso

## Entidades de Auditoría y Reporte

### 20. AuditLog
**Tabla**: `audit_log`  
**Propósito**: Log de cambios en el sistema

**Propiedades**:
- `userId`: Referencia a AppUser
- `entityType`: Tipo de entidad modificada
- `entityId`: ID de la entidad
- `action`: Acción realizada (CREATE, UPDATE, DELETE)
- `changes`: JSON con cambios
- `createdAt`: Fecha del cambio

### 21. ContentReport
**Tabla**: `content_report`  
**Propósito**: Reporte de contenido inapropiado

**Propiedades**:
- `reporterUserId`: Referencia a AppUser
- `storyId`: Referencia a Story (opcional)
- `commentId`: Referencia a StoryComment (opcional)
- `reportReason`: Motivo del reporte
- `reportDescription`: Descripción detallada
- `reportStatus`: pending, under_review, resolved, rejected
- `createdAt`: Fecha del reporte

### 22. UserSanction
**Tabla**: `user_sanction`  
**Propósito**: Sanción aplicada a usuario

**Propiedades**:
- `userId`: Referencia a AppUser
- `sanctionType`: warning, suspension, permanent_ban
- `reason`: Motivo de la sanción
- `appliedAt`: Fecha de aplicación
- `expiresAt`: Fecha de expiración (para suspensiones)
- `appliedByUserId`: Administrador que aplicó

## Entidades Auxiliares

### 23. GlobalNotice
**Tabla**: `global_notice`  
**Propósito**: Noticia global del sistema

**Propiedades**:
- `title`: Título de la noticia
- `content`: Contenido
- `isActive`: Si está activa
- `publishedAt`: Fecha de publicación

### 24. Idea
**Tabla**: `idea`  
**Propósito**: Idea de historia guardada

**Propiedades**:
- `userId`: Referencia a AppUser
- `title`: Título de la idea
- `description`: Descripción
- `createdAt`: Fecha de creación

### 25. UserChangeHistory
**Tabla**: `user_change_history`  
**Propósito**: Historial de cambios en usuario

**Propiedades**:
- `userId`: Referencia a AppUser
- `changeType`: Tipo de cambio
- `oldValue`: Valor anterior
- `newValue`: Valor nuevo
- `changedAt`: Fecha del cambio

### 26. BaseToken
**Clase Base**  
**Propósito**: Clase abstracta base para tokens

```java
public class BaseToken {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false)
    private Integer userId;
    
    private LocalDateTime createdAt;
}
```

## Patrones de Implementación

### Soft Delete

Todas las entidades principales implementan soft delete:

```java
@SQLDelete(sql = "UPDATE tabla SET deleted = true, deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted = false")
```

### Auditoría de Fechas

```java
@PrePersist
public void prePersist() {
    if (this.deleted == null) {
        this.deleted = false;
    }
    LocalDateTime now = LocalDateTime.now();
    this.createdAt = now;
    this.updatedAt = now;
}

@PreUpdate
public void preUpdate() {
    this.updatedAt = LocalDateTime.now();
}
```

## Enumeraciones Relacionadas

### AccessLevel
```java
public enum AccessLevel {
    user,      // Usuario normal
    moderator, // Moderador
    admin      // Administrador
}
```

### AccountState
```java
public enum AccountState {
    pending_verification, // Pendiente verificación
    active,               // Activa
    suspended,            // Suspendida
    banned                // Bloqueada
}
```

## Relaciones Clave

| Relación | Tipo | Notas |
|---|---|---|
| AppUser → Story | 1:N | Un usuario puede tener muchas historias |
| Story → Chapter | 1:N | Una historia puede tener muchos capítulos |
| Story → StoryComment | 1:N | Una historia puede recibir muchos comentarios |
| Story → StoryRating | 1:N | Una historia puede recibir muchas calificaciones |
| AppUser → StoryComment | 1:N | Un usuario puede hacer muchos comentarios |
| AppUser → UserFollow | N:M | Usuario puede seguir/ser seguido por múltiples usuarios |

