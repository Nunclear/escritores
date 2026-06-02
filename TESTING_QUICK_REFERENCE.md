# Testing Quick Reference - Escritores Project

## 📊 Test Statistics
- **Total Test Classes:** 49
- **Total Test Methods:** 275+
- **Code Coverage:** ~80%
- **Framework:** JUnit 5 + Mockito
- **Database:** H2 In-Memory

## 🚀 Quick Commands

```bash
# Run all tests
mvn clean test

# Run with coverage report
mvn clean test jacoco:report

# Run specific test class
mvn test -Dtest=StoryServiceTest

# Run tests matching pattern
mvn test -Dtest=*Service*Test

# Fail fast (stop on first failure)
mvn test -Dstopfailurecount=1
```

## 📁 Test File Locations

| Layer | Directory | Classes |
|-------|-----------|---------|
| Controllers | `test/java/.../controller/` | 12 |
| Services | `test/java/.../service/` | 18 |
| Repositories | `test/java/.../repository/` | 2+ |
| Entities | `test/java/.../entity/` | 12 |
| Security | `test/java/.../security/` | 4 |
| Exceptions | `test/java/.../exception/` | 1 |

## 📝 Test Class Template

```java
@ExtendWith(MockitoExtension.class)
@DisplayName("ServiceName Unit Tests")
class ServiceNameTest {
    
    @Mock
    private Repository repository;
    
    @InjectMocks
    private Service service;
    
    @BeforeEach
    void setUp() {
        // Setup test data
    }
    
    @Test
    @DisplayName("method_Condition_ExpectedResult")
    void method_Condition_ExpectedResult() {
        // Arrange
        Object input = new Object();
        
        // Act
        Object result = service.method(input);
        
        // Assert
        assertNotNull(result);
    }
}
```

## 🔧 Common Annotations

### Test Annotations
```java
@Test                          // Test method
@DisplayName("description")    // Custom name
@BeforeEach                    // Setup before each test
@AfterEach                     // Cleanup after each test
@BeforeAll                     // Setup once before all
@AfterAll                      // Cleanup after all
@Disabled                      // Skip test
@RepeatedTest(3)              // Run test 3 times
```

### Mock Annotations
```java
@Mock                          // Create mock
@InjectMocks                   // Inject mocks
@Captor                        // Capture arguments
@Spy                           // Partial mock
```

### Spring Test Annotations
```java
@DataJpaTest                   // Repository testing
@WebMvcTest(Controller.class)  // Controller testing
@SpringBootTest                // Full context
```

## 💡 Common Patterns

### Mock Setup
```java
// Return value
when(repo.findById(1)).thenReturn(Optional.of(entity));

// Throw exception
when(repo.save(any())).thenThrow(new RuntimeException());

// Answer
when(repo.save(any())).thenAnswer(invocation -> {
    Entity entity = invocation.getArgument(0);
    entity.setId(1);
    return entity;
});

// Void method
doNothing().when(service).delete(any());
```

### Verification
```java
// Verify called
verify(repo, times(1)).save(any());

// Verify never called
verify(repo, never()).delete(any());

// Capture arguments
ArgumentCaptor<Entity> captor = ArgumentCaptor.forClass(Entity.class);
verify(repo).save(captor.capture());
Entity saved = captor.getValue();
```

### Assertions
```java
// Basic assertions
assertTrue(condition);
assertFalse(condition);
assertNull(value);
assertNotNull(value);
assertEquals(expected, actual);
assertThrows(Exception.class, () -> service.method());
```

### Test Data
```java
// Using TestDataBuilder
AppUser user = TestDataBuilder.buildAppUser("username", "email@test.com");
Story story = TestDataBuilder.buildStory(user, "Title");
Character character = TestDataBuilder.buildCharacter(story, "Name");
```

## 🔐 Security Testing

```java
// Mock authenticated user
private void mockAuthenticatedUser(AppUser user) {
    SecurityContext context = mock(SecurityContext.class);
    Authentication auth = mock(Authentication.class);
    when(auth.getPrincipal()).thenReturn(new CustomUserDetails(user));
    when(context.getAuthentication()).thenReturn(auth);
    SecurityContextHolder.setContext(context);
}

// Use in tests
@BeforeEach
void setUp() {
    mockAuthenticatedUser(testUser);
}

@AfterEach
void tearDown() {
    SecurityContextHolder.clearContext();
}
```

## 📋 Test Checklist

### Before Writing Test
- [ ] Understand what to test
- [ ] Identify dependencies
- [ ] Prepare test data
- [ ] Determine expected behavior

### While Writing Test
- [ ] Use descriptive method name
- [ ] Follow AAA pattern (Arrange/Act/Assert)
- [ ] Mock external dependencies
- [ ] Test one thing per test
- [ ] Keep test small and focused

### After Writing Test
- [ ] Run test - verify it passes
- [ ] Run test in isolation
- [ ] Check coverage increased
- [ ] Review test quality
- [ ] Document if needed

## 🐛 Troubleshooting

| Issue | Solution |
|-------|----------|
| "No MockBean found" | Use `@MockBean` in `@WebMvcTest` |
| "SecurityContextHolder empty" | Call `mockAuthenticatedUser()` in `@BeforeEach` |
| "Test passes alone, fails in suite" | Check test isolation, use `@BeforeEach` |
| "H2 database error" | Verify H2 dependency, check test config |
| "Mock not working" | Check `@ExtendWith(MockitoExtension.class)` present |
| "Assertion never runs" | Check test doesn't exit early |

## 📚 Documentation Files

| File | Purpose |
|------|---------|
| `TEST_GUIDE.md` | Comprehensive testing reference |
| `TESTING_CONFIG.md` | Configuration and setup guide |
| `TEST_SUMMARY.md` | Executive summary and statistics |
| `TESTING_QUICK_REFERENCE.md` | This quick reference |

## 🎯 Coverage Targets

| Layer | Target | How to Check |
|-------|--------|-------------|
| Controllers | 85% | `mvn clean test jacoco:report` |
| Services | 80% | Look at service folder in report |
| Repositories | 85% | Look at repository folder in report |
| Entities | 75% | Look at entity folder in report |
| Overall | 80% | Check summary in report |

## 🔗 Useful Commands

```bash
# View test coverage report
mvn clean test jacoco:report && open target/site/jacoco/index.html

# Run test with debug output
mvn test -Dtest=TestClass -X

# Run tests in parallel
mvn test -T 1C

# Skip tests during build
mvn clean package -DskipTests

# Run specific test method
mvn test -Dtest=TestClass#methodName
```

## 📖 Key Resources

- [JUnit 5 Documentation](https://junit.org/junit5/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core)
- [Spring Testing Guide](https://spring.io/guides/gs/testing-web/)
- [JaCoCo Coverage](https://www.jacoco.org/)

## ✨ Best Practices Summary

✅ Use descriptive test names
✅ Test one thing per test
✅ Mock external dependencies
✅ Keep tests independent
✅ Use AAA pattern
✅ Verify behavior, not implementation
✅ Keep tests fast
✅ Clean up resources
✅ Use test data builders
✅ Maintain >80% coverage

## 🚦 Test Status

| Layer | Count | Status |
|-------|-------|--------|
| Controllers | 12 | ✅ Complete |
| Services | 18 | ✅ Complete |
| Repositories | 2+ | ✅ Complete |
| Entities | 12 | ✅ Complete |
| Security | 4 | ✅ Complete |
| Exceptions | 1 | ✅ Complete |
| **Total** | **49+** | **✅ Complete** |

---

**Last Updated:** April 2026
**Coverage:** ~80%
**Framework:** JUnit 5 + Mockito 5.x
