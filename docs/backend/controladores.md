# Controladores

## Descripción General

Los controladores son clases anotadas con `@RestController` que manejan solicitudes HTTP, delegando la lógica de negocio a los servicios y devolviendo respuestas en formato JSON.

**Total de Controladores**: 15+

## Patrón Base

```java
@RestController
@RequestMapping("/recurso")  // Ruta base
@RequiredArgsConstructor     // Inyecta servicios
public class RecursoController {
    
    private final RecursoService recursoService;
    
    // CREATE - POST
    @PostMapping
    @PreAuthorize("hasAnyRole('USER','MODERATOR','ADMIN')")
    public ResponseEntity<CreateResponse> crear(
        @Valid @RequestBody CreateRequest request
    ) {
        CreateResponse response = recursoService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    // READ - GET uno
    @GetMapping("/{id}")
    public ResponseEntity<DetailResponse> obtener(@PathVariable Integer id) {
        DetailResponse response = recursoService.obtenerPorId(id);
        return ResponseEntity.ok(response);
    }
    
    // READ - GET todos (paginado)
    @GetMapping
    public ResponseEntity<Page<ListResponse>> listar(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(required = false) String sort
    ) {
        Page<ListResponse> response = recursoService.listar(page, size, sort);
        return ResponseEntity.ok(response);
    }
    
    // UPDATE - PUT
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','MODERATOR','ADMIN')")
    public ResponseEntity<UpdateResponse> actualizar(
        @PathVariable Integer id,
        @Valid @RequestBody UpdateRequest request
    ) {
        UpdateResponse response = recursoService.actualizar(id, request);
        return ResponseEntity.ok(response);
    }
    
    // DELETE
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','MODERATOR','ADMIN')")
    public ResponseEntity<MessageResponse> eliminar(@PathVariable Integer id) {
        MessageResponse response = recursoService.eliminar(id);
        return ResponseEntity.ok(response);
    }
}
```

## Controladores Principales

### AuthController

**Ruta base**: `/auth`

```java
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    private final HttpServletRequest httpRequest;
    
    // Registro
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
        @Valid @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(authService.register(request));
    }
    
    // Login
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
        @Valid @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(
            authService.login(request, httpRequest)
        );
    }
    
    // Refresh token
    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refresh(
        @Valid @RequestBody RefreshTokenRequest request
    ) {
        return ResponseEntity.ok(authService.refresh(request));
    }
    
    // Logout
    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageResponse> logout(
        @Valid @RequestBody LogoutRequest request
    ) {
        return ResponseEntity.ok(authService.logout(request));
    }
    
    // Obtener usuario actual
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CurrentUserResponse> getCurrentUser() {
        return ResponseEntity.ok(authService.me());
    }
    
    // Forgot password
    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(
        @Valid @RequestBody ForgotPasswordRequest request
    ) {
        return ResponseEntity.ok(authService.forgotPassword(request));
    }
    
    // Reset password
    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(
        @Valid @RequestBody ResetPasswordRequest request
    ) {
        return ResponseEntity.ok(authService.resetPassword(request));
    }
    
    // Verify email
    @PostMapping("/verify-email")
    public ResponseEntity<MessageResponse> verifyEmail(
        @Valid @RequestBody VerifyEmailRequest request
    ) {
        return ResponseEntity.ok(authService.verifyEmail(request));
    }
}
```

### StoryController

**Ruta base**: `/stories`

```java
@RestController
@RequestMapping("/stories")
@RequiredArgsConstructor
public class StoryController {
    
    private final StoryService storyService;
    
    // Crear historia
    @PostMapping
    @PreAuthorize("hasAnyRole('USER','MODERATOR','ADMIN')")
    public ResponseEntity<CreateStoryResponse> createStory(
        @Valid @RequestBody CreateStoryRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(storyService.createStory(request));
    }
    
    // Obtener historia por ID
    @GetMapping("/{id}")
    public ResponseEntity<StoryDetailResponse> getStoryById(
        @PathVariable Integer id
    ) {
        return ResponseEntity.ok(storyService.getStoryById(id));
    }
    
    // Obtener historia por slug
    @GetMapping("/slug/{slug}")
    public ResponseEntity<StorySlugResponse> getStoryBySlug(
        @PathVariable String slug
    ) {
        return ResponseEntity.ok(storyService.getStoryBySlug(slug));
    }
    
    // Listar historias públicas
    @GetMapping
    public ResponseEntity<PageResponse<StoryListItemResponse>> listPublicStories(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(required = false) String sort
    ) {
        return ResponseEntity.ok(
            storyService.listPublicStories(page, size, sort)
        );
    }
    
    // Buscar historias
    @GetMapping("/search")
    public ResponseEntity<PageResponse<StoryListItemResponse>> searchStories(
        @RequestParam(required = false) String q,
        @RequestParam(required = false) String visibilityState,
        @RequestParam(required = false) String publicationState,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(required = false) String sort
    ) {
        return ResponseEntity.ok(
            storyService.searchStories(q, visibilityState, publicationState, page, size, sort)
        );
    }
    
    // Obtener historias de un usuario
    @GetMapping("/user/{userId}")
    public ResponseEntity<PageResponse<UserStorySummaryResponse>> getStoriesByUser(
        @PathVariable Integer userId,
        @RequestParam(defaultValue = "false") boolean includeDrafts,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(
            storyService.getStoriesByUser(userId, includeDrafts, page, size)
        );
    }
    
    // Actualizar historia
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','MODERATOR','ADMIN')")
    public ResponseEntity<UpdateStoryResponse> updateStory(
        @PathVariable Integer id,
        @Valid @RequestBody UpdateStoryRequest request
    ) {
        return ResponseEntity.ok(storyService.updateStory(id, request));
    }
    
    // Publicar historia
    @PostMapping("/{id}/publish")
    @PreAuthorize("hasAnyRole('USER','MODERATOR','ADMIN')")
    public ResponseEntity<StoryPublicationResponse> publishStory(
        @PathVariable Integer id
    ) {
        return ResponseEntity.ok(storyService.publishStory(id));
    }
    
    // Archivar historia
    @PostMapping("/{id}/archive")
    @PreAuthorize("hasAnyRole('USER','MODERATOR','ADMIN')")
    public ResponseEntity<StoryArchiveResponse> archiveStory(
        @PathVariable Integer id
    ) {
        return ResponseEntity.ok(storyService.archiveStory(id));
    }
    
    // Eliminar historia
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','MODERATOR','ADMIN')")
    public ResponseEntity<MessageResponse> deleteStory(
        @PathVariable Integer id
    ) {
        return ResponseEntity.ok(storyService.deleteStory(id));
    }
}
```

### ChapterController

**Ruta base**: `/chapters`

```java
@RestController
@RequestMapping("/chapters")
@RequiredArgsConstructor
public class ChapterController {
    
    private final ChapterService chapterService;
    
    @PostMapping
    @PreAuthorize("hasAnyRole('USER','MODERATOR','ADMIN')")
    public ResponseEntity<CreateChapterResponse> createChapter(
        @Valid @RequestBody CreateChapterRequest request
    ) { /* ... */ }
    
    @GetMapping("/{id}")
    public ResponseEntity<ChapterDetailResponse> getChapterById(
        @PathVariable Integer id
    ) { /* ... */ }
    
    @GetMapping("/story/{storyId}")
    public ResponseEntity<PageResponse<ChapterListItemResponse>> getChaptersByStory(
        @PathVariable Integer storyId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) { /* ... */ }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','MODERATOR','ADMIN')")
    public ResponseEntity<UpdateChapterResponse> updateChapter(
        @PathVariable Integer id,
        @Valid @RequestBody UpdateChapterRequest request
    ) { /* ... */ }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','MODERATOR','ADMIN')")
    public ResponseEntity<MessageResponse> deleteChapter(
        @PathVariable Integer id
    ) { /* ... */ }
}
```

## Anotaciones de Spring MVC

### Mapeo de Rutas

```java
@RestController              // Controlador REST
@RequestMapping("/api")      // Ruta base
public class MyController {
    
    @GetMapping              // GET /api
    @GetMapping("/{id}")     // GET /api/{id}
    @GetMapping("/search")   // GET /api/search
    
    @PostMapping             // POST /api
    @PostMapping("/action")  // POST /api/action
    
    @PutMapping("/{id}")     // PUT /api/{id}
    @PatchMapping("/{id}")   // PATCH /api/{id}
    
    @DeleteMapping("/{id}")  // DELETE /api/{id}
}
```

### Parámetros

```java
@RestController
@RequestMapping("/users")
public class UserController {
    
    // Path variable
    @GetMapping("/{id}")
    public User getUser(@PathVariable Integer id) { }
    
    // Request parameter
    @GetMapping
    public List<User> search(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(required = false) String name
    ) { }
    
    // Request body
    @PostMapping
    public User create(@Valid @RequestBody CreateUserRequest request) { }
    
    // Header
    @GetMapping("/token")
    public String getToken(
        @RequestHeader("Authorization") String auth
    ) { }
    
    // Pageable (desde data.domain)
    @GetMapping
    public Page<User> list(Pageable pageable) {
        // pageable.getPageNumber()
        // pageable.getPageSize()
        // pageable.getSort()
    }
}
```

### Respuestas

```java
@RestController
public class ResponseController {
    
    // ResponseEntity con status
    @PostMapping
    public ResponseEntity<CreateResponse> create(
        @RequestBody CreateRequest request
    ) {
        CreateResponse response = new CreateResponse(...);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    // ResponseEntity con OK (200)
    @GetMapping("/{id}")
    public ResponseEntity<DetailResponse> getDetail(
        @PathVariable Integer id
    ) {
        return ResponseEntity.ok(new DetailResponse(...));
    }
    
    // ResponseEntity sin cuerpo (204)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        return ResponseEntity.noContent().build();
    }
    
    // ResponseEntity con headers
    @GetMapping
    public ResponseEntity<List<Item>> list() {
        List<Item> items = getItems();
        return ResponseEntity.ok()
            .header("X-Total-Count", String.valueOf(items.size()))
            .body(items);
    }
}
```

## Autorización

```java
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    
    // Autenticado
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<AdminData> getData() { }
    
    // Rol específico
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> create(
        @RequestBody CreateRequest request
    ) { }
    
    // Múltiples roles
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
    public ResponseEntity<MessageResponse> delete(
        @PathVariable Integer id
    ) { }
    
    // Expresión Spring EL
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') and #id > 0")
    public ResponseEntity<AdminData> getById(
        @PathVariable Integer id
    ) { }
    
    // Acceso anónimo
    @GetMapping("/public")
    @PreAuthorize("permitAll()")
    public List<PublicData> getPublicData() { }
}
```

## Validación

```java
@RestController
@RequestMapping("/items")
public class ItemController {
    
    // @Valid valida el DTO
    @PostMapping
    public ResponseEntity<ItemResponse> create(
        @Valid @RequestBody CreateItemRequest request
    ) {
        // Si hay errores, GlobalExceptionHandler los maneja
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(itemService.create(request));
    }
}

// DTO con validaciones
public record CreateItemRequest(
    @NotBlank(message = "El nombre es requerido")
    @Size(min = 3, max = 100)
    String name,
    
    @Email(message = "Email inválido")
    String email,
    
    @Min(value = 1)
    @Max(value = 100)
    Integer quantity
) { }
```

## Testing de Controladores

```java
@WebMvcTest(StoryController.class)
@DisplayName("Story Controller Tests")
class StoryControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private StoryService storyService;
    
    @Test
    void testGetStoryById() throws Exception {
        // Arrange
        StoryDetailResponse response = new StoryDetailResponse(...);
        when(storyService.getStoryById(1))
            .thenReturn(response);
        
        // Act & Assert
        mockMvc.perform(get("/stories/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Test Story"));
    }
    
    @Test
    @WithMockUser
    void testCreateStory() throws Exception {
        mockMvc.perform(post("/stories")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"title\":\"New Story\"}"))
            .andExpect(status().isCreated());
    }
}
```

## Lista de Controladores

| Controlador | Ruta | Responsabilidad |
|---|---|---|
| AuthController | `/auth` | Autenticación |
| StoryController | `/stories` | Historias |
| ChapterController | `/chapters` | Capítulos |
| CommentController | `/comments` | Comentarios |
| RatingController | `/ratings` | Calificaciones |
| FavoriteController | `/favorites` | Favoritos |
| FollowController | `/follows` | Seguimientos |
| CharacterController | `/characters` | Personajes |
| SkillController | `/skills` | Habilidades |
| EventController | `/events` | Eventos |
| ItemController | `/items` | Ítems |
| MediaController | `/media` | Media |
| DashboardController | `/dashboard` | Dashboard usuario |
| AdminDashboardController | `/admin/dashboard` | Dashboard admin |
| MetricsController | `/metrics` | Métricas |

