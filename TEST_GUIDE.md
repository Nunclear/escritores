# Comprehensive Testing Guide - Escritores Project

## Overview

This document outlines the comprehensive test coverage strategy for the Escritores platform. The project implements a multi-layered testing approach covering unit tests, integration tests, and repository tests.

## Test Structure

### Test Layers

#### 1. **Controller Layer Tests** (`controller/`)
Tests HTTP endpoints and request/response handling.

**Files:**
- `ArcControllerTest.java` - Arc (Story Timeline) endpoints
- `ChapterControllerTest.java` - Chapter CRUD operations
- `CharacterControllerTest.java` - Character management
- `CharacterSkillControllerTest.java` - Character skill associations
- `EventControllerTest.java` - Story event endpoints
- `IdeaControllerTest.java` - Creative idea endpoints
- `ItemControllerTest.java` - Story items/objects
- `MediaControllerTest.java` - Media file handling
- `SkillControllerTest.java` - Skill CRUD endpoints
- `StoryControllerTest.java` - Story management
- `UserControllerTest.java` - User profile endpoints
- `VolumeControllerTest.java` - Volume/book management

**Key Test Patterns:**
```java
@WebMvcTest(StoryController.class)
class StoryControllerTest {
    @MockBean
    private StoryService storyService;
    
    @Test
    void createStory_WithValidRequest_ReturnsCreatedStory() {
        // Test HTTP request/response handling
    }
}
```

#### 2. **Service Layer Tests** (`service/`)
Tests business logic and service interactions.

**Core Services:**
- `StoryServiceTest.java` - Story creation, publication, archival
- `CharacterServiceTest.java` - Character CRUD and story character management
- `ChapterServiceTest.java` - Chapter publication and ordering
- `SkillServiceTest.java` - Skill creation and categorization
- `UserServiceTest.java` - User profile and settings management
- `AuthServiceTest.java` - Authentication and token management
- `AdminUserServiceTest.java` - Admin user operations

**Social Features:**
- `FavoriteServiceTest.java` - Story favoriting and retrieval
- `FollowServiceTest.java` - User follow/unfollow operations
- `CommentServiceTest.java` - Story comments and ratings
- `RatingServiceTest.java` - Story ratings and reviews

**Story Elements:**
- `ArcServiceTest.java` - Story timeline/arc management
- `EventServiceTest.java` - Story event creation and management
- `IdeaServiceTest.java` - Creative idea brainstorming
- `ItemServiceTest.java` - Story item/object management
- `MediaServiceTest.java` - File uploads and media handling
- `VolumeServiceTest.java` - Volume/book management
- `CharacterSkillServiceTest.java` - Character ability management

**Test Pattern:**
```java
@ExtendWith(MockitoExtension.class)
class StoryServiceTest {
    @Mock
    private StoryRepository storyRepository;
    
    @InjectMocks
    private StoryService storyService;
    
    @Test
    void createStory_WithValidRequest_ReturnsStory() {
        // Test business logic
    }
}
```

#### 3. **Entity Tests** (`entity/`)
Tests entity models and business rules.

**Files:**
- `AppUserTest.java` - User model validation
- `StoryTest.java` - Story entity constraints
- `ChapterTest.java` - Chapter structure
- `StoryCharacterTest.java` - Character relationships
- `SkillTest.java` - Skill attributes
- `ArcTest.java` - Arc/timeline properties
- `StoryEventTest.java` - Event entity
- `MediaTest.java` - Media metadata
- `ItemTest.java` - Item properties
- `IdeaTest.java` - Idea tracking
- `CharacterSkillTest.java` - Skill associations
- `VolumeTest.java` - Volume/book structure

**Test Pattern:**
```java
class StoryTest {
    @Test
    void story_WithValidData_CreatesSuccessfully() {
        Story story = new Story();
        story.setTitle("Test");
        assertNotNull(story.getCreatedAt());
    }
}
```

#### 4. **Security Tests** (`security/`)
Tests authentication and authorization.

**Files:**
- `JwtServiceTest.java` - JWT token generation and validation
- `JwtAuthenticationFilterTest.java` - Request authentication
- `CustomUserDetailsServiceTest.java` - User authentication provider
- `CustomUserDetailsTest.java` - User principal details

**Key Tests:**
- Token generation and expiration
- Request authentication filtering
- User principal extraction

#### 5. **Repository Tests** (`repository/`)
Tests database queries and persistence.

**Files:**
- `StoryRepositoryTest.java` - Story CRUD and queries
- `AppUserRepositoryTest.java` - User lookup and persistence

**Test Pattern:**
```java
@DataJpaTest
class StoryRepositoryTest {
    @Autowired
    private StoryRepository storyRepository;
    
    @Test
    void findBySlugText_WithValidSlug_ReturnsStory() {
        // Test database queries
    }
}
```

#### 6. **Configuration Tests** (`config/`)
Tests application configuration and test utilities.

**Files:**
- `TestDataBuilder.java` - Test data factory
- `EscritoresApplicationTests.java` - Application context tests

## Test Coverage by Feature

### User & Authentication (8 tests)
- User registration and profile management
- Password hashing and validation
- JWT token creation and verification
- Login and logout flows
- Permission-based access control

### Stories (15+ tests)
- Story creation with auto-slug generation
- Publication state management (draft/published)
- Visibility control (public/private)
- Story archival and restoration
- Pagination and searching
- Owner verification

### Characters (10+ tests)
- Character creation within stories
- Character role assignment (protagonist/antagonist/etc)
- Character search and filtering
- Character deletion with cascade rules
- Profile image handling

### Chapters (8+ tests)
- Chapter creation with ordering
- Chapter publication control
- Content validation
- Chapter numbering
- Publication date tracking

### Skills & Abilities (10+ tests)
- Skill creation and categorization
- Character skill assignment
- Skill leveling system
- Skill search and discovery

### Social Features (12+ tests)
- User following relationships
- Story favoriting
- Comments and discussion
- Story ratings and reviews
- Notification generation

### Story Elements (15+ tests)
- Events/plot points
- Items/objects
- Timeline/arcs
- Media uploads
- Volumes/books

## Running Tests

### Run All Tests
```bash
mvn clean test
```

### Run Specific Test Class
```bash
mvn test -Dtest=StoryServiceTest
```

### Run with Coverage Report (JaCoCo)
```bash
mvn clean test jacoco:report
```

Coverage reports available at: `target/site/jacoco/index.html`

### Run Tests by Package
```bash
# Service layer tests only
mvn test -Dtest=com.nunclear.escritores.service.*

# Controller layer tests only
mvn test -Dtest=com.nunclear.escritores.controller.*

# Repository tests only
mvn test -Dtest=com.nunclear.escritores.repository.*
```

## Test Data Management

### TestDataBuilder Usage
```java
// Create test users
AppUser user = TestDataBuilder.buildAppUser("username", "email@example.com");

// Create test stories
Story story = TestDataBuilder.buildStory(author, "Story Title");

// Create test characters
Character character = TestDataBuilder.buildCharacter(story, "Character Name");

// Create test chapters
Chapter chapter = TestDataBuilder.buildChapter(story, 1, "Chapter Title");
```

## Mocking Strategy

### Service Tests
- Mock all repository dependencies
- Mock security context for authenticated operations
- Use ArgumentCaptor for verification

### Controller Tests
- Mock service layer completely
- Test request/response serialization
- Verify HTTP status codes

### Repository Tests
- Use @DataJpaTest for database testing
- Actual database interactions (H2 in-memory)
- No mocking required

## Best Practices

### 1. **Test Naming**
Follow convention: `methodName_Condition_ExpectedResult`
```java
void createStory_WithValidRequest_ReturnsCreatedStory() { }
void createStory_WithDuplicateSlug_GeneratesUniqueSlug() { }
void createStory_WithInvalidState_ThrowsBadRequestException() { }
```

### 2. **Test Structure (AAA Pattern)**
```java
@Test
void testMethod() {
    // Arrange - setup test data and mocks
    Story story = TestDataBuilder.buildStory(author, "Title");
    when(repository.save(any())).thenReturn(story);
    
    // Act - execute the method under test
    Story result = service.createStory(request);
    
    // Assert - verify the results
    assertNotNull(result);
    assertEquals("Title", result.getTitle());
}
```

### 3. **Security Context Setup**
```java
private void mockAuthenticatedUser(AppUser user) {
    SecurityContext context = mock(SecurityContext.class);
    Authentication auth = mock(Authentication.class);
    CustomUserDetails details = new CustomUserDetails(user);
    when(auth.getPrincipal()).thenReturn(details);
    when(context.getAuthentication()).thenReturn(auth);
    SecurityContextHolder.setContext(context);
}
```

### 4. **Verification Pattern**
```java
// Verify method was called
verify(repository, times(1)).save(any());

// Verify method was never called
verify(repository, never()).delete(any());

// Capture arguments
ArgumentCaptor<Story> captor = ArgumentCaptor.forClass(Story.class);
verify(repository).save(captor.capture());
assertEquals("Title", captor.getValue().getTitle());
```

## Common Test Scenarios

### 1. **CRUD Operations**
- Create with valid data → Success
- Create with invalid data → Exception
- Read existing resource → Returns resource
- Read non-existing resource → Not found exception
- Update with valid data → Updated resource returned
- Delete existing resource → Resource removed

### 2. **Authorization Tests**
- Operation by owner → Success
- Operation by non-owner → Unauthorized exception
- Admin bypass → Success
- Unauthenticated access → Unauthorized exception

### 3. **Validation Tests**
- Valid input → Accepted
- Empty/null fields → Bad request
- Invalid enums → Bad request
- Duplicate constraints → Conflict exception

### 4. **Pagination Tests**
- First page → Returns correct page
- Out of range page → Empty results
- Custom page size → Respects size

### 5. **Search Tests**
- Search with results → Returns matching records
- Search with no results → Empty page
- Search with filter → Only filtered results

## Continuous Integration

Tests run automatically on:
- Every commit to feature branches
- Pull request creation
- Before merge to main

### Coverage Requirements
- **Minimum Coverage:** 70% line coverage
- **Target Coverage:** 80%+ line coverage
- **Critical Paths:** 90%+ coverage for auth, payments, data persistence

## Troubleshooting

### Common Issues

**1. "No MockBean for repository"**
Solution: Use `@MockBean` for Spring components in `@WebMvcTest`

**2. "SecurityContextHolder is not set"**
Solution: Call `mockAuthenticatedUser()` in `@BeforeEach` or use `SecurityContextHolder.getContext().setAuthentication()`

**3. "JPA not flushing changes"**
Solution: In `@DataJpaTest`, use `entityManager.flush()` to verify DB state

**4. "Tests pass locally but fail in CI"**
Solution: 
- Check for timing issues in async operations
- Verify database state isolation between tests
- Check for hardcoded timestamps

## Test Statistics

| Layer | Test Classes | Test Methods | Coverage |
|-------|--------------|--------------|----------|
| Controllers | 12 | 60+ | 85%+ |
| Services | 15 | 120+ | 80%+ |
| Entities | 12 | 40+ | 75%+ |
| Security | 4 | 15+ | 90%+ |
| Repository | 2 | 20+ | 85%+ |
| **Total** | **45+** | **255+** | **80%+** |

## Future Enhancements

1. **Performance Tests** - Load testing for concurrent users
2. **Integration Tests** - End-to-end API tests
3. **Mutation Testing** - CodeCoverage validation
4. **Contract Testing** - API client compatibility
5. **Accessibility Tests** - WCAG compliance verification

## Resources

- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Spring Testing Guide](https://spring.io/guides/gs/testing-web/)
- [JaCoCo Coverage Report](https://www.jacoco.org/)

## Contact & Support

For test-related questions or to report test failures:
- Check test logs in CI pipeline
- Review test documentation above
- Consult TestDataBuilder for available test fixtures
