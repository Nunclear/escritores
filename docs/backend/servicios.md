# Servicios

## Descripción General

Los servicios implementan la lógica de negocio de la aplicación, encapsulando operaciones complejas que pueden involucrar múltiples repositorios y transformaciones de datos.

**Total de Servicios**: 25+

## Patrón Base

```java
@Service
@RequiredArgsConstructor  // Lombok inyecta en constructor
@Transactional            // Todas las operaciones transaccionales
@Slf4j                    // Logger
public class MiService {
    
    // Inyecciones
    private final MiRepository repository;
    private final OtroRepository otroRepository;
    
    // Método de creación
    public MiDtoResponse crear(MiDtoRequest request) {
        // Validar entrada
        if (repository.existsByUnique(request.unique())) {
            throw new ConflictException("Ya existe");
        }
        
        // Crear entidad
        MiEntidad entidad = new MiEntidad();
        entidad.setPropiedad(request.propiedad());
        
        // Guardar
        MiEntidad guardada = repository.save(entidad);
        
        // Retornar DTO
        return new MiDtoResponse(guardada.getId(), guardada.getPropiedad());
    }
    
    // Método de lectura
    public MiDtoResponse obtenerPorId(Integer id) {
        MiEntidad entidad = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("No encontrado"));
        return mapToDto(entidad);
    }
    
    // Método de actualización
    @Transactional
    public MiDtoResponse actualizar(Integer id, MiDtoRequest request) {
        MiEntidad entidad = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("No encontrado"));
        
        entidad.setPropiedad(request.propiedad());
        MiEntidad actualizada = repository.save(entidad);
        
        return mapToDto(actualizada);
    }
    
    // Método de eliminación (soft delete)
    @Transactional
    public void eliminar(Integer id) {
        repository.deleteById(id);  // Soft delete automático
    }
    
    // Mapeo DTO
    private MiDtoResponse mapToDto(MiEntidad entidad) {
        return new MiDtoResponse(entidad.getId(), entidad.getPropiedad());
    }
}
```

## Servicios Principales

### AuthService

**Ubicación**: `service/AuthService.java`

**Responsabilidades**:
- Registro de usuarios
- Login/autenticación
- Refresh tokens
- Logout
- Reset de contraseña
- Verificación de email

**Métodos clave**:

```java
public RegisterResponse register(RegisterRequest request)
public LoginResponse login(LoginRequest request, HttpServletRequest httpRequest)
public RefreshResponse refresh(RefreshTokenRequest request)
public MessageResponse logout(LogoutRequest request)
public MessageResponse forgotPassword(ForgotPasswordRequest request)
public MessageResponse resetPassword(ResetPasswordRequest request)
public MessageResponse verifyEmail(VerifyEmailRequest request)
public CurrentUserResponse me()
```

### StoryService

**Ubicación**: `service/StoryService.java`

**Responsabilidades**:
- Crear historias
- Editar historias
- Publicar/despublicar
- Archivar/restaurar
- Buscar historias
- Listar historias

**Métodos clave**:

```java
public CreateStoryResponse createStory(CreateStoryRequest request)
public StoryDetailResponse getStoryById(Integer id)
public StorySlugResponse getStoryBySlug(String slug)
public PageResponse<StoryListItemResponse> listPublicStories(int page, int size, String sort)
public PageResponse<StoryListItemResponse> searchStories(String q, String visibility, String publication, int page, int size, String sort)
public UpdateStoryResponse updateStory(Integer id, UpdateStoryRequest request)
public StoryPublicationResponse publishStory(Integer id)
public StoryPublicationResponse unpublishStory(Integer id)
public StoryArchiveResponse archiveStory(Integer id)
public DuplicateStoryResponse duplicateStory(Integer id, DuplicateStoryRequest request)
```

### ChapterService

**Ubicación**: `service/ChapterService.java`

**Responsabilidades**:
- Crear capítulos
- Editar capítulos
- Publicar capítulos
- Eliminar capítulos
- Reordenar capítulos

```java
public CreateChapterResponse createChapter(CreateChapterRequest request)
public ChapterDetailResponse getChapterById(Integer id)
public PageResponse<ChapterListItemResponse> listChaptersByStory(Integer storyId, int page, int size)
public PageResponse<ChapterListItemResponse> listPublishedChapters(Integer storyId, int page, int size)
public UpdateChapterResponse updateChapter(Integer id, UpdateChapterRequest request)
public ChapterPublicationResponse publishChapter(Integer id)
public MessageResponse deleteChapter(Integer id)
```

### CommentService

**Ubicación**: `service/CommentService.java`

**Responsabilidades**:
- Crear comentarios
- Editar comentarios
- Eliminar comentarios
- Obtener comentarios por historia
- Manejar replies

```java
public CreateCommentResponse createComment(CreateCommentRequest request)
public CommentDetailResponse getCommentById(Integer id)
public PageResponse<CommentListItemResponse> getCommentsByStory(Integer storyId, int page, int size)
public PageResponse<CommentListItemResponse> getCommentsByChapter(Integer chapterId, int page, int size)
public PageResponse<CommentListItemResponse> getReplies(Integer parentId, int page, int size)
public UpdateCommentResponse updateComment(Integer id, UpdateCommentRequest request)
public MessageResponse deleteComment(Integer id)
```

### RatingService

**Ubicación**: `service/RatingService.java`

**Responsabilidades**:
- Crear/actualizar calificaciones
- Obtener calificación promedio
- Eliminar calificaciones

```java
public CreateRatingResponse createRating(CreateRatingRequest request)
public UpdateRatingResponse updateRating(Integer id, UpdateRatingRequest request)
public RatingAverageResponse getAverageRating(Integer storyId)
public Long getTotalRatingsForStory(Integer storyId)
public MessageResponse deleteRating(Integer id)
```

### FavoriteService

**Ubicación**: `service/FavoriteService.java`

**Responsabilidades**:
- Agregar a favoritos
- Remover de favoritos
- Obtener lista de favoritos
- Contar favoritos

```java
public CreateFavoriteResponse addFavorite(CreateFavoriteRequest request)
public PageResponse<FavoriteListItemResponse> getMyFavorites(int page, int size)
public Long getFavoriteCountForStory(Integer storyId)
public Boolean isFavorite(Integer storyId)
public MessageResponse removeFavorite(Integer favoriteId)
```

### FollowService

**Ubicación**: `service/FollowService.java`

**Responsabilidades**:
- Seguir usuario
- Dejar de seguir
- Obtener seguidores
- Contar seguidores

```java
public CreateFollowResponse followUser(CreateFollowRequest request)
public PageResponse<FollowListItemResponse> getFollowers(Integer userId, int page, int size)
public PageResponse<FollowListItemResponse> getFollowing(Integer userId, int page, int size)
public Long getFollowerCount(Integer userId)
public Boolean isFollowing(Integer followedUserId)
public MessageResponse unfollowUser(Integer followId)
```

### UserService

**Ubicación**: `service/UserService.java`

**Responsabilidades**:
- Obtener perfil de usuario
- Actualizar perfil
- Cambiar contraseña
- Cambiar avatar

```java
public UserProfileResponse getUserProfile(Integer userId)
public UserPublicProfileResponse getPublicProfile(Integer userId)
public UserProfileResponse updateProfile(Integer userId, UpdateUserProfileRequest request)
public UserProfileResponse changeAvatar(Integer userId, ChangeAvatarRequest request)
public MessageResponse changePassword(Integer userId, ChangePasswordRequest request)
```

## Patrón Transaccional

```java
@Service
@RequiredArgsConstructor
public class MiService {
    
    // Transacción por defecto (READ_WRITE)
    @Transactional
    public void metodoCompleto() {
        // Se ejecuta dentro de una transacción
        // Si falla, se hace rollback automático
    }
    
    // Solo lectura (más eficiente)
    @Transactional(readOnly = true)
    public List<MiEntidad> obtenerTodos() {
        return repository.findAll();
    }
    
    // Sin transacción
    public void metodoPublico() {
        // No abre transacción
    }
    
    // Propagación
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void metodoNuevaTransaccion() {
        // Abre nueva transacción incluso si ya hay una activa
    }
    
    // Aislamiento
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void metodoAislado() {
        // Lee solo datos confirmados
    }
    
    // Timeout
    @Transactional(timeout = 30)  // 30 segundos
    public void metodoLargo() {
        // Lanza excepción si tarda más de 30s
    }
}
```

## Patrón de Validación

```java
@Service
@RequiredArgsConstructor
public class StoryService {
    
    private final StoryRepository storyRepository;
    private final AppUserRepository userRepository;
    
    public CreateStoryResponse createStory(CreateStoryRequest request) {
        // 1. Validación de entrada
        if (request.title() == null || request.title().isBlank()) {
            throw new BadRequestException("Título requerido");
        }
        
        // 2. Validación de unicidad
        if (storyRepository.existsBySlugTextIgnoreCase(request.slugText())) {
            throw new ConflictException("El slug ya existe");
        }
        
        // 3. Validación de dependencias
        AppUser user = userRepository.findById(getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        
        if (user.getAccountState() == AccountState.suspended) {
            throw new UnauthorizedException("Cuenta suspendida");
        }
        
        // 4. Crear entidad
        Story story = new Story();
        story.setOwnerUserId(user.getId());
        story.setTitle(request.title());
        
        // 5. Guardar
        Story saved = storyRepository.save(story);
        
        // 6. Retornar DTO
        return new CreateStoryResponse(saved.getId(), saved.getTitle());
    }
}
```

## Manejo de Excepciones

```java
@Service
@RequiredArgsConstructor
public class MiService {
    
    private final MiRepository repository;
    
    public MiDtoResponse obtenerPorId(Integer id) {
        // Opción 1: Lanzar excepción si no existe
        MiEntidad entidad = repository.findById(id)
            .orElseThrow(() -> 
                new ResourceNotFoundException("Recurso no encontrado con ID: " + id)
            );
        
        return mapToDto(entidad);
    }
    
    public void crearConValidacion(CreateRequest request) {
        // Opción 2: Validar y lanzar excepción
        if (repository.existsByUniqueField(request.uniqueField())) {
            throw new ConflictException("El campo única ya existe");
        }
        
        // Opción 3: Usar Optional
        repository.findByUniqueField(request.uniqueField()).ifPresent(found -> {
            throw new ConflictException("Ya existe");
        });
    }
}
```

## Uso en Controladores

```java
@RestController
@RequestMapping("/stories")
@RequiredArgsConstructor
public class StoryController {
    
    private final StoryService storyService;
    
    @PostMapping
    @PreAuthorize("hasAnyRole('USER','MODERATOR','ADMIN')")
    public ResponseEntity<CreateStoryResponse> createStory(
        @Valid @RequestBody CreateStoryRequest request
    ) {
        CreateStoryResponse response = storyService.createStory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<StoryDetailResponse> getStoryById(@PathVariable Integer id) {
        StoryDetailResponse response = storyService.getStoryById(id);
        return ResponseEntity.ok(response);
    }
}
```

## Testing de Servicios

```java
@ExtendWith(MockitoExtension.class)
class StoryServiceTest {
    
    @Mock
    private StoryRepository storyRepository;
    
    @InjectMocks
    private StoryService storyService;
    
    @Test
    void testCreateStory() {
        // Arrange
        CreateStoryRequest request = new CreateStoryRequest("Test Story", "Description");
        Story story = new Story();
        story.setId(1);
        story.setTitle("Test Story");
        
        when(storyRepository.save(any(Story.class))).thenReturn(story);
        
        // Act
        CreateStoryResponse response = storyService.createStory(request);
        
        // Assert
        assertNotNull(response);
        assertEquals(1, response.id());
        verify(storyRepository).save(any(Story.class));
    }
    
    @Test
    void testGetStoryNotFound() {
        // Arrange
        when(storyRepository.findById(999)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            storyService.getStoryById(999);
        });
    }
}
```

## Inyección de Dependencias

```java
@Service
@RequiredArgsConstructor  // Lombok crea constructor con dependencias
public class MiService {
    
    // Inyección automática
    private final MiRepository repository;
    private final OtroService otroService;
    private final ApplicationContext applicationContext;
    
    // Valores de configuración
    @Value("${app.jwt.access-expiration-seconds}")
    private long accessExpirationSeconds;
    
    // Inyección condicional
    @Autowired(required = false)
    private EmailService emailService;  // Opcional
    
    public void usar() {
        if (emailService != null) {
            emailService.enviar("email@test.com", "Mensaje");
        }
    }
}
```

