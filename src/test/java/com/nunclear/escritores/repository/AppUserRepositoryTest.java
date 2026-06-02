package com.nunclear.escritores.repository;

import com.nunclear.escritores.config.TestDataBuilder;
import com.nunclear.escritores.entity.AppUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("AppUserRepository Integration Tests")
class AppUserRepositoryTest {

    @Autowired
    private AppUserRepository appUserRepository;

    private AppUser testUser;

    @BeforeEach
    void setUp() {
        testUser = appUserRepository.save(TestDataBuilder.buildAppUser("testuser", "test@example.com"));
    }

    // ============== Find by Email Tests ==============

    @Test
    @DisplayName("findByEmail_WithExistingEmail_ReturnsUser")
    void findByEmailWithExistingEmailReturnsUser() {
        // Act
        Optional<AppUser> result = appUserRepository.findByEmail(testUser.getEmail());

        // Assert
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
    }

    @Test
    @DisplayName("findByEmail_WithNonExistingEmail_ReturnsEmpty")
    void findByEmailWithNonExistingEmailReturnsEmpty() {
        // Act
        Optional<AppUser> result = appUserRepository.findByEmail("nonexistent@example.com");

        // Assert
        assertTrue(result.isEmpty());
    }

    // ============== Find by Username Tests ==============

    @Test
    @DisplayName("findByUsername_WithExistingUsername_ReturnsUser")
    void findByUsernameWithExistingUsernameReturnsUser() {
        // Act
        Optional<AppUser> result = appUserRepository.findByUsername("testuser");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testUser.getEmail(), result.get().getEmail());
    }

    @Test
    @DisplayName("findByUsername_WithNonExistingUsername_ReturnsEmpty")
    void findByUsernameWithNonExistingUsernameReturnsEmpty() {
        // Act
        Optional<AppUser> result = appUserRepository.findByUsername("nonexistent");

        // Assert
        assertTrue(result.isEmpty());
    }

    // ============== Existence Tests ==============

    @Test
    @DisplayName("existsByEmail_WithExistingEmail_ReturnsTrue")
    void existsByEmailWithExistingEmailReturnsTrue() {
        // Act
        boolean exists = appUserRepository.existsByEmail(testUser.getEmail());

        // Assert
        assertTrue(exists);
    }

    @Test
    @DisplayName("existsByEmail_WithNonExistingEmail_ReturnsFalse")
    void existsByEmailWithNonExistingEmailReturnsFalse() {
        // Act
        boolean exists = appUserRepository.existsByEmail("nonexistent@example.com");

        // Assert
        assertFalse(exists);
    }

    @Test
    @DisplayName("existsByUsername_WithExistingUsername_ReturnsTrue")
    void existsByUsernameWithExistingUsernameReturnsTrue() {
        // Act
        boolean exists = appUserRepository.existsByUsername("testuser");

        // Assert
        assertTrue(exists);
    }

    @Test
    @DisplayName("existsByUsername_WithNonExistingUsername_ReturnsFalse")
    void existsByUsernameWithNonExistingUsernameReturnsFalse() {
        // Act
        boolean exists = appUserRepository.existsByUsername("nonexistent");

        // Assert
        assertFalse(exists);
    }

    // ============== Find by ID Tests ==============

    @Test
    @DisplayName("findById_WithExistingId_ReturnsUser")
    void findByIdWithExistingIdReturnsUser() {
        // Act
        Optional<AppUser> result = appUserRepository.findById(testUser.getId());

        // Assert
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
    }

    @Test
    @DisplayName("findById_WithNonExistingId_ReturnsEmpty")
    void findByIdWithNonExistingIdReturnsEmpty() {
        // Act
        Optional<AppUser> result = appUserRepository.findById(999);

        // Assert
        assertTrue(result.isEmpty());
    }

    // ============== Update Tests ==============

    @Test
    @DisplayName("save_WithUpdatedUser_UpdatesUser")
    void saveWithUpdatedUserUpdatesUser() {
        // Arrange
        testUser.setUsername("updateduser");

        // Act
        AppUser saved = appUserRepository.save(testUser);

        // Assert
        assertEquals("updateduser", saved.getUsername());
        Optional<AppUser> result = appUserRepository.findById(testUser.getId());
        assertTrue(result.isPresent());
        assertEquals("updateduser", result.get().getUsername());
    }

    // ============== Delete Tests ==============

    @Test
    @DisplayName("delete_WithExistingUser_DeletesUser")
    void deleteWithExistingUserDeletesUser() {
        // Act
        appUserRepository.delete(testUser);

        // Assert
        Optional<AppUser> result = appUserRepository.findById(testUser.getId());
        assertTrue(result.isEmpty());
    }
}
