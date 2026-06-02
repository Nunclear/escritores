# Testing Configuration & Setup Guide

## Prerequisites

- Java 17+
- Maven 3.8+
- Spring Boot 3.x
- JUnit 5
- Mockito 5.x
- Spring Test

## Maven Dependencies

All testing dependencies are configured in `pom.xml`:

```xml
<!-- JUnit 5 -->
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-api</artifactId>
    <scope>test</scope>
</dependency>

<!-- Mockito -->
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <scope>test</scope>
</dependency>

<!-- Spring Test -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>

<!-- JaCoCo Code Coverage -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.10</version>
    <executions>
        <execution>
            <id>default-prepare-agent</id>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

## Test Configuration Files

### 1. Application Properties for Tests

Create `application-test.yml` or `application-test.properties`:

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password:
  
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create-drop
  
  h2:
    console:
      enabled: true
  
  jwt:
    secret: test-secret-key-for-testing
    expiration: 86400000
```

### 2. Test Profile Activation

Tests automatically run with the `test` profile. This is configured in Maven:

```bash
mvn test -Dspring.profiles.active=test
```

## Test Database Configuration

### H2 In-Memory Database Setup

The test environment uses H2 in-memory database for fast, isolated test execution:

```java
@DataJpaTest
class StoryRepositoryTest {
    // Automatically uses H2 in-memory database
    // Each test gets a fresh database instance
}
```

### Database State Isolation

Each `@DataJpaTest` class:
- Creates a fresh H2 database
- Runs schema creation
- Executes the test
- Drops all data (automatic rollback)

This ensures tests don't interfere with each other.

## JUnit 5 Configuration

### Test Annotations

```java
// Basic test class
@ExtendWith(MockitoExtension.class)
class ServiceTest { }

// Web layer tests
@WebMvcTest(Controller.class)
class ControllerTest { }

// Database tests
@DataJpaTest
class RepositoryTest { }

// Full Spring context
@SpringBootTest
class IntegrationTest { }

// Display name
@DisplayName("Service Unit Tests")
class ServiceTest { }

// Nested test organization
@Nested
@DisplayName("When creating resource")
class CreationTests { }
```

### Test Method Annotations

```java
// Test method
@Test
void testMethod() { }

// Repeating test
@RepeatedTest(3)
void repeatedTest() { }

// Parameterized test
@ParameterizedTest
@ValueSource(strings = { "hello", "world" })
void parameterizedTest(String arg) { }

// Disabled test
@Disabled("Not ready yet")
void disabledTest() { }

// Display name
@Test
@DisplayName("Creates user successfully")
void createUserTest() { }
```

## Mockito Configuration

### Mock Annotations

```java
@ExtendWith(MockitoExtension.class)
class ServiceTest {
    @Mock
    private Repository repository;
    
    @InjectMocks
    private Service service;
    
    @Captor
    private ArgumentCaptor<Model> captor;
}
```

### Mock Setup Patterns

```java
// Simple mock
when(repository.findById(1)).thenReturn(Optional.of(entity));

// Void method
doNothing().when(service).delete(any());

// Exception
when(repository.save(any())).thenThrow(new RuntimeException());

// Answer
when(repository.save(any())).thenAnswer(invocation -> {
    Model model = invocation.getArgument(0);
    model.setId(1);
    return model;
});

// Spy (partial mocking)
Service realService = spy(new Service());
when(realService.method()).thenReturn(value);
```

### Verification Patterns

```java
// Basic verification
verify(repository, times(1)).save(any());

// Never called
verify(repository, never()).delete(any());

// Argument capture
ArgumentCaptor<Entity> captor = ArgumentCaptor.forClass(Entity.class);
verify(repository).save(captor.capture());
Entity captured = captor.getValue();

// Multiple arguments
verify(service).updateUser(eq(1), any(UpdateRequest.class));

// InOrder verification
InOrder inOrder = inOrder(service1, service2);
inOrder.verify(service1).method1();
inOrder.verify(service2).method2();
```

## Spring Security Test Configuration

### Authenticating Tests

```java
// In service tests
private void mockAuthenticatedUser(AppUser user) {
    SecurityContext context = mock(SecurityContext.class);
    Authentication auth = mock(Authentication.class);
    CustomUserDetails details = new CustomUserDetails(user);
    
    when(auth.getPrincipal()).thenReturn(details);
    when(context.getAuthentication()).thenReturn(auth);
    SecurityContextHolder.setContext(context);
}

// In controller tests
@Test
void testWithAuthentication() throws Exception {
    mockMvc.perform(get("/api/stories")
        .with(user("username").roles("USER")))
        .andExpect(status().isOk());
}
```

### Cleanup

```java
@AfterEach
void tearDown() {
    SecurityContextHolder.clearContext();
}
```

## Test Data Management

### TestDataBuilder Pattern

```java
public class TestDataBuilder {
    public static AppUser buildAppUser(String username, String email) {
        AppUser user = new AppUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash("hashedPassword");
        return user;
    }
}
```

### Usage

```java
@BeforeEach
void setUp() {
    user = TestDataBuilder.buildAppUser("testuser", "test@example.com");
    story = TestDataBuilder.buildStory(user, "Test Story");
}
```

## Coverage Configuration

### JaCoCo Integration

Coverage reports are generated automatically during Maven build:

```bash
mvn clean test jacoco:report
```

View report: `target/site/jacoco/index.html`

### Coverage Rules

Configure minimum coverage in `pom.xml`:

```xml
<execution>
    <id>jacoco-check</id>
    <goals>
        <goal>check</goal>
    </goals>
    <configuration>
        <rules>
            <rule>
                <element>PACKAGE</element>
                <excludes>
                    <exclude>*Test</exclude>
                </excludes>
                <limits>
                    <limit>
                        <counter>LINE</counter>
                        <value>COVEREDRATIO</value>
                        <minimum>0.70</minimum>
                    </limit>
                </limits>
            </rule>
        </rules>
    </configuration>
</execution>
```

## Running Tests

### Command Line

```bash
# All tests
mvn clean test

# Specific test class
mvn test -Dtest=StoryServiceTest

# Specific test method
mvn test -Dtest=StoryServiceTest#createStory_WithValidRequest_ReturnsCreatedStory

# Package level
mvn test -Dtest=com.nunclear.escritores.service.*

# With coverage
mvn clean test jacoco:report

# Fail fast (stop on first failure)
mvn test -DfailIfNoTests=false -Darguments='-DskipITs' failsafe:integration-test

# Parallel execution
mvn test -T 1C  # 1 thread per core
```

### IDE Integration

**IntelliJ IDEA:**
- Right-click test class → Run
- Cmd/Ctrl + Shift + F10 to run focused test
- Cmd/Ctrl + Shift + F9 to run coverage

**VS Code (with Extension Pack for Java):**
- Click "Run Test" or "Run | Debug Test" above test methods
- Run tests from test explorer

**Eclipse:**
- Right-click → Run As → JUnit Test
- Alt + Shift + X → T for focused test

## Best Practices

### 1. Test Independence
```java
@BeforeEach
void setUp() {
    // Fresh data for each test
}

@AfterEach
void tearDown() {
    // Clean up resources
}
```

### 2. Avoid Test Interdependence
```java
// ❌ BAD - Tests depend on order
void testCreateUser() { createUser("test"); }
void testUpdateUser() { updateExistingUser(); }

// ✅ GOOD - Each test is independent
void testCreateUser() { /* complete setup */ }
void testUpdateUser() { /* complete setup */ }
```

### 3. Use Descriptive Names
```java
// ❌ Unclear
void testMethod() { }

// ✅ Clear
void createStory_WithValidRequest_ReturnsCreatedStory() { }
```

### 4. Keep Tests Small
```java
// ❌ Too large
void testComplexScenario() {
    // 50+ lines testing multiple things
}

// ✅ Single responsibility
void testCreateStory_WithValidRequest() { }
void testCreateStory_WithDuplicateSlug() { }
```

### 5. Mock External Dependencies
```java
@Mock
private ExternalService externalService;

@Test
void test() {
    when(externalService.call()).thenReturn(value);
    // Test without calling actual external service
}
```

## Troubleshooting

### Issue: "No qualifying bean of type X found"

**Solution:**
```java
@WebMvcTest(Controller.class)
class ControllerTest {
    @MockBean
    private Service service;  // Use @MockBean for dependencies
}
```

### Issue: "SecurityContextHolder is not set"

**Solution:**
```java
@BeforeEach
void setUp() {
    mockAuthenticatedUser(testUser);
}

@AfterEach
void tearDown() {
    SecurityContextHolder.clearContext();
}
```

### Issue: "JPA repository not flushing"

**Solution:**
```java
@Test
void test(EntityManager em) {
    // Make changes
    em.flush();  // Force DB flush
    // Assert
}
```

### Issue: "Tests pass individually but fail together"

**Causes:**
- Shared state between tests
- Non-isolated database changes
- Order dependency

**Solutions:**
- Use `@BeforeEach` for setup
- Use rollback in `@DataJpaTest`
- Don't depend on test execution order

## CI/CD Integration

### GitHub Actions Example

```yaml
name: Tests

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
      - run: mvn clean test jacoco:report
      - uses: codecov/codecov-action@v3
```

### Jenkins Pipeline Example

```groovy
pipeline {
    stages {
        stage('Test') {
            steps {
                sh 'mvn clean test jacoco:report'
            }
        }
        stage('Coverage') {
            steps {
                publishHTML([
                    reportDir: 'target/site/jacoco',
                    reportFiles: 'index.html'
                ])
            }
        }
    }
}
```

## Performance Considerations

### Test Execution Time

- `@DataJpaTest`: ~100-500ms (database overhead)
- `@WebMvcTest`: ~50-200ms (lightweight)
- `@ExtendWith(MockitoExtension.class)`: ~10-50ms (fastest)

### Optimization Tips

1. Use unit tests (Mockito) before integration tests
2. Run database tests in parallel
3. Use `@WebMvcTest` instead of `@SpringBootTest` when possible
4. Cache expensive test fixtures
5. Use H2 for tests (faster than real database)

## Resources

- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core)
- [Spring Test Guide](https://spring.io/guides/gs/testing-web/)
- [JaCoCo Code Coverage](https://www.jacoco.org/jacoco/trunk/doc/)
- [Spring Security Testing](https://docs.spring.io/spring-security/reference/servlet/test/index.html)
