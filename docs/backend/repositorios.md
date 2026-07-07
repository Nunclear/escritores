# Repositorios

## Descripción General

Los repositorios son interfaces que heredan de `JpaRepository<Entity, ID>` de Spring Data JPA, proporcionando operaciones CRUD automáticas y permitiendo consultas personalizadas.

**Total de Repositorios**: 25+

## Patrón Base

```java
@Repository
public interface MiRepository extends JpaRepository<MiEntidad, Integer> {
    
    // Métodos derivados automáticamente por nombre
    Optional<MiEntidad> findById(Integer id);
    List<MiEntidad> findAll();
    
    // Consultas personalizadas derivadas
    List<MiEntidad> findByUsuarioIdOrderByCreatedAtDesc(Integer usuarioId);
    Optional<MiEntidad> findByUniqueField(String field);
    
    // Consultas personalizadas con @Query
    @Query("SELECT m FROM MiEntidad m WHERE m.status = 'active'")
    List<MiEntidad> findAllActive();
}
```

## Repositorios Principales

### AppUserRepository

**Tabla**: `app_user`

```java
@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Integer> {
    
    // Búsqueda por login
    Optional<AppUser> findByLoginNameIgnoreCase(String loginName);
    
    // Búsqueda por email
    Optional<AppUser> findByEmailAddressIgnoreCase(String emailAddress);
    
    // Búsqueda por login O email
    Optional<AppUser> findByLoginNameIgnoreCaseOrEmailAddressIgnoreCase(
        String loginName, String emailAddress
    );
    
    // Verificar existencia
    boolean existsByLoginNameIgnoreCase(String loginName);
    boolean existsByEmailAddressIgnoreCase(String emailAddress);
    
    // Búsqueda por estado de cuenta
    List<AppUser> findByAccountStateOrderByCreatedAtDesc(AccountState state);
    
    // Búsqueda por nivel de acceso
    List<AppUser> findByAccessLevelOrderByCreatedAtDesc(AccessLevel level);
}
```

**Métodos derivados clave**:
- `findByLoginNameIgnoreCase()`: Case-insensitive por login
- `findByEmailAddressIgnoreCase()`: Case-insensitive por email
- `existsByLoginNameIgnoreCase()`: Verificar existencia sin crear instancia

### StoryRepository

**Tabla**: `story`

```java
@Repository
public interface StoryRepository extends JpaRepository<Story, Integer> {
    
    // Por propietario
    List<Story> findByOwnerUserIdOrderByCreatedAtDesc(Integer ownerUserId);
    Page<Story> findByOwnerUserId(Integer ownerUserId, Pageable pageable);
    
    // Por slug
    Optional<Story> findBySlugTextIgnoreCase(String slugText);
    boolean existsBySlugTextIgnoreCase(String slugText);
    
    // Por estado de publicación
    List<Story> findByPublicationStateOrderByCreatedAtDesc(String publicationState);
    Page<Story> findByPublicationState(String publicationState, Pageable pageable);
    
    // Por visibilidad
    List<Story> findByVisibilityStateOrderByCreatedAtDesc(String visibilityState);
    
    // Por propietario Y estado de publicación
    List<Story> findByOwnerUserIdAndPublicationStateOrderByCreatedAtDesc(
        Integer ownerUserId, String publicationState
    );
    
    // Búsqueda con LIKE
    @Query("SELECT s FROM Story s WHERE s.title LIKE %:searchTerm% " +
           "OR s.description LIKE %:searchTerm% ORDER BY s.createdAt DESC")
    List<Story> searchByTitleOrDescription(
        @Param("searchTerm") String searchTerm, Pageable pageable
    );
    
    // Publicitadas
    @Query("SELECT s FROM Story s WHERE s.publicationState = 'published' " +
           "AND s.visibilityState = 'public' ORDER BY s.publishedAt DESC")
    Page<Story> findPublishedPublic(Pageable pageable);
}
```

**Métodos derivados clave**:
- Retornan `Optional<T>` para UNO o CERO resultados
- Retornan `List<T>` para múltiples resultados
- Retornan `Page<T>` para paginación
- `OrderBy` al final para ordenamiento

### ChapterRepository

**Tabla**: `chapter`

```java
@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Integer> {
    
    // Por historia
    List<Chapter> findByStoryIdOrderBySequenceNumberAsc(Integer storyId);
    Page<Chapter> findByStoryId(Integer storyId, Pageable pageable);
    
    // Por volumen
    List<Chapter> findByVolumeIdOrderBySequenceNumberAsc(Integer volumeId);
    
    // Por arco
    List<Chapter> findByArcIdOrderBySequenceNumberAsc(Integer arcId);
    
    // Capítulos publicados
    List<Chapter> findByStoryIdAndPublicationStateOrderBySequenceNumberAsc(
        Integer storyId, String publicationState
    );
    
    // Contar capítulos
    int countByStoryId(Integer storyId);
    int countByStoryIdAndPublicationState(Integer storyId, String state);
}
```

### StoryCommentRepository

**Tabla**: `story_comment`

```java
@Repository
public interface StoryCommentRepository extends JpaRepository<StoryComment, Integer> {
    
    // Por historia
    List<StoryComment> findByStoryIdOrderByCreatedAtDesc(Integer storyId);
    Page<StoryComment> findByStoryId(Integer storyId, Pageable pageable);
    
    // Por capítulo
    List<StoryComment> findByChapterIdOrderByCreatedAtDesc(Integer chapterId);
    
    // Por usuario (comentarios que hizo)
    List<StoryComment> findByCommenterUserIdOrderByCreatedAtDesc(Integer userId);
    
    // Respuestas a comentario
    List<StoryComment> findByParentCommentIdOrderByCreatedAtDesc(Integer parentId);
    
    // Comentarios no moderados
    @Query("SELECT sc FROM StoryComment sc WHERE sc.isModerated = false " +
           "ORDER BY sc.createdAt ASC")
    List<StoryComment> findUnmoderatedComments(Pageable pageable);
}
```

### StoryRatingRepository

**Tabla**: `story_rating`

```java
@Repository
public interface StoryRatingRepository extends JpaRepository<StoryRating, Integer> {
    
    // Calificaciones de una historia
    List<StoryRating> findByStoryId(Integer storyId);
    
    // Calificaciones de un usuario
    List<StoryRating> findByRaterUserId(Integer userId);
    
    // Calificación específica
    Optional<StoryRating> findByStoryIdAndRaterUserId(Integer storyId, Integer userId);
    
    // Calificación promedio
    @Query("SELECT AVG(r.ratingValue) FROM StoryRating r WHERE r.storyId = :storyId")
    Double findAverageRating(@Param("storyId") Integer storyId);
    
    // Contar calificaciones
    long countByStoryId(Integer storyId);
}
```

### StoryFavoriteRepository

**Tabla**: `story_favorite`

```java
@Repository
public interface StoryFavoriteRepository extends JpaRepository<StoryFavorite, Integer> {
    
    // Favoritos de un usuario
    List<StoryFavorite> findByUserId(Integer userId);
    Page<StoryFavorite> findByUserId(Integer userId, Pageable pageable);
    
    // Verificar si es favorito
    boolean existsByStoryIdAndUserId(Integer storyId, Integer userId);
    Optional<StoryFavorite> findByStoryIdAndUserId(Integer storyId, Integer userId);
    
    // Contar favoritos de historia
    long countByStoryId(Integer storyId);
}
```

### UserFollowRepository

**Tabla**: `user_follow`

```java
@Repository
public interface UserFollowRepository extends JpaRepository<UserFollow, Integer> {
    
    // Usuarios que sigue
    List<UserFollow> findByFollowerUserIdOrderByCreatedAtDesc(Integer followerId);
    Page<UserFollow> findByFollowerUserId(Integer followerId, Pageable pageable);
    
    // Seguidores de usuario
    List<UserFollow> findByFollowedUserIdOrderByCreatedAtDesc(Integer followedId);
    Page<UserFollow> findByFollowedUserId(Integer followedId, Pageable pageable);
    
    // Verificar seguimiento
    boolean existsByFollowerUserIdAndFollowedUserId(Integer followerId, Integer followedId);
    Optional<UserFollow> findByFollowerUserIdAndFollowedUserId(Integer followerId, Integer followedId);
    
    // Contar
    long countByFollowerUserId(Integer followerId);
    long countByFollowedUserId(Integer followedId);
}
```

## Operaciones CRUD Automáticas

Heredadas de `JpaRepository<T, ID>`:

```java
// CREATE
entity save(entity);
Iterable<entity> saveAll(Iterable<entity>);

// READ
Optional<entity> findById(ID);
Iterable<entity> findAll();
Iterable<entity> findAllById(Iterable<ID>);
boolean existsById(ID);
long count();

// UPDATE
// Usar save() con ID existente

// DELETE
void deleteById(ID);
void delete(entity);
void deleteAll(Iterable<entity>);
void deleteAll();
```

## Consultas Personalizadas con @Query

### Sintaxis JPQL

```java
@Repository
public interface CustomRepository extends JpaRepository<Entity, Integer> {
    
    // Consulta simple
    @Query("SELECT e FROM Entity e WHERE e.status = :status")
    List<Entity> findByStatus(@Param("status") String status);
    
    // Consulta con JOIN
    @Query("SELECT s FROM Story s JOIN Chapter c ON s.id = c.storyId " +
           "WHERE c.publicationState = 'published'")
    List<Story> findStoriesWithPublishedChapters();
    
    // Agregaciones
    @Query("SELECT COUNT(s) FROM Story s WHERE s.ownerUserId = :userId")
    long countStoriesByUser(@Param("userId") Integer userId);
    
    // Proyecciones
    @Query("SELECT NEW com.nunclear.escritores.dto.response.StoryListItemResponse(" +
           "s.id, s.title, s.slugText) FROM Story s")
    List<StoryListItemResponse> findAllProjected();
    
    // Eliminaciones
    @Modifying
    @Transactional
    @Query("DELETE FROM Story s WHERE s.ownerUserId = :userId")
    void deleteByUser(@Param("userId") Integer userId);
    
    // Actualizaciones
    @Modifying
    @Transactional
    @Query("UPDATE Story s SET s.visibilityState = 'private' " +
           "WHERE s.ownerUserId = :userId")
    int updateVisibility(@Param("userId") Integer userId);
}
```

## Métodos Derivados (Query Methods)

Spring Data JPA genera consultas automáticamente según el nombre del método:

```java
// Sintaxis: findBy<Property><Operator>
public interface ExampleRepository extends JpaRepository<Example, Integer> {
    
    // Operadores básicos
    findBy<Property>()                              // Igual
    findBy<Property>IsNull()                        // IS NULL
    findBy<Property>IsNotNull()                     // IS NOT NULL
    
    // Comparación
    findBy<Property>GreaterThan(value)              // >
    findBy<Property>GreaterThanEqual(value)         // >=
    findBy<Property>LessThan(value)                 // <
    findBy<Property>LessThanEqual(value)            // <=
    
    // Strings
    findBy<Property>Like(value)                     // LIKE
    findBy<Property>NotLike(value)                  // NOT LIKE
    findBy<Property>StartingWith(value)             // LIKE value%
    findBy<Property>EndingWith(value)               // LIKE %value
    findBy<Property>Containing(value)               // LIKE %value%
    
    // Booleanos
    findBy<Property>True()                          // = true
    findBy<Property>False()                         // = false
    
    // Lógica
    findBy<Prop1>And<Prop2>()                       // AND
    findBy<Prop1>Or<Prop2>()                        // OR
    findBy<Property>Not()                           // NOT
    
    // Ordenamiento
    findBy<Property>OrderBy<OtherProp>Asc()         // ORDER BY ASC
    findBy<OtherProperty>Desc()                     // ORDER BY DESC
    
    // Ejemplos reales:
    List<AppUser> findByAccessLevelOrderByCreatedAtDesc(AccessLevel level);
    Page<Story> findByOwnerUserIdAndPublicationState(Integer userId, String state, Pageable p);
    Optional<AppUser> findByEmailAddressIgnoreCase(String email);
    List<Chapter> findByStoryIdOrderBySequenceNumberAsc(Integer storyId);
}
```

## Paginación y Ordenamiento

```java
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

// En servicio o controlador:
Pageable pageable = PageRequest.of(0, 20, Sort.by("createdAt").descending());
Page<Story> page = storyRepository.findAll(pageable);

// Propiedades de Page<T>:
page.getContent();          // List<T>
page.getTotalElements();    // long - Total de elementos
page.getTotalPages();       // int - Total de páginas
page.getNumber();           // int - Página actual (0-based)
page.getSize();             // int - Tamaño de página
page.hasNext();             // boolean
page.hasPrevious();         // boolean
page.getNumberOfElements(); // int - Elementos en esta página
```

## Transaccionalidad

```java
@Repository
public interface MyRepository extends JpaRepository<Entity, Integer> {
    
    // Con @Modifying para UPDATE/DELETE
    @Modifying
    @Transactional
    @Query("UPDATE Entity e SET e.status = :status WHERE e.id = :id")
    void updateStatus(@Param("id") Integer id, @Param("status") String status);
}
```

## Testing de Repositorios

```java
@DataJpaTest
class StoryRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private StoryRepository repository;
    
    @Test
    public void testFindBySlug() {
        Story story = new Story();
        story.setTitle("Test");
        story.setSlugText("test-slug");
        entityManager.persistAndFlush(story);
        
        Optional<Story> found = repository.findBySlugTextIgnoreCase("test-slug");
        assertTrue(found.isPresent());
        assertEquals("Test", found.get().getTitle());
    }
}
```

## Mejores Prácticas

1. **Usar Optional en single results**:
   ```java
   Optional<User> findByEmail(String email);  // ✅ Correcto
   User findByEmail(String email);            // ❌ Puede ser nulo
   ```

2. **Nombres descriptivos**:
   ```java
   findByOwnerUserIdAndPublicationStateOrderByCreatedAtDesc()  // ✅ Claro
   getStories()                                                // ❌ Vago
   ```

3. **Usar Pageable para grandes datasets**:
   ```java
   Page<Story> findByOwnerId(Integer id, Pageable pageable);  // ✅ Paginado
   List<Story> findByOwnerId(Integer id);                     // ❌ Puede ser muy grande
   ```

4. **Usar proyecciones para DTO**:
   ```java
   @Query("SELECT NEW com.example.StoryDTO(...) FROM Story s")  // ✅ Eficiente
   List<Story> findAll();                                        // ❌ Carga todo
   ```

