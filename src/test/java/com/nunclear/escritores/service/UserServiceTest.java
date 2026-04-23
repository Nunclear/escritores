package com.nunclear.escritores.service;

import com.nunclear.escritores.dto.request.*;
import com.nunclear.escritores.dto.response.*;
import com.nunclear.escritores.entity.AppUser;
import com.nunclear.escritores.entity.Story;
import com.nunclear.escritores.entity.UserFollow;
import com.nunclear.escritores.entity.UserSession;
import com.nunclear.escritores.enums.AccessLevel;
import com.nunclear.escritores.enums.AccountState;
import com.nunclear.escritores.exception.BadRequestException;
import com.nunclear.escritores.exception.ResourceNotFoundException;
import com.nunclear.escritores.exception.UnauthorizedException;
import com.nunclear.escritores.repository.AppUserRepository;
import com.nunclear.escritores.repository.StoryRepository;
import com.nunclear.escritores.repository.UserFollowRepository;
import com.nunclear.escritores.repository.UserSessionRepository;
import com.nunclear.escritores.security.CustomUserDetails;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private AppUserRepository appUserRepository;
    @Mock
    private StoryRepository storyRepository;
    @Mock
    private UserFollowRepository userFollowRepository;
    @Mock
    private UserSessionRepository userSessionRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getUserById_deberiaRetornarPerfil() {
        AppUser user = mock(AppUser.class);

        when(appUserRepository.findById(1)).thenReturn(Optional.of(user));
        when(user.getDeletedAt()).thenReturn(null);
        when(user.getAccountState()).thenReturn(AccountState.active);
        when(user.getId()).thenReturn(1);
        when(user.getLoginName()).thenReturn("juan");
        when(user.getDisplayName()).thenReturn("Juan");
        when(user.getBioText()).thenReturn("Bio");
        when(user.getAvatarUrl()).thenReturn("https://img.test/a.jpg");
        when(user.getAccessLevel()).thenReturn(AccessLevel.user);
        when(user.getCreatedAt()).thenReturn(LocalDateTime.of(2026, 4, 22, 10, 0));

        UserProfileResponse response = userService.getUserById(1);

        assertEquals(1, response.id());
        assertEquals("juan", response.loginName());
        assertEquals("Juan", response.displayName());
        assertEquals("user", response.accessLevel());
    }

    @Test
    void getUserById_deberiaLanzarNotFound_siUsuarioEliminado() {
        AppUser user = mock(AppUser.class);

        when(appUserRepository.findById(1)).thenReturn(Optional.of(user));
        when(user.getDeletedAt()).thenReturn(LocalDateTime.now());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.getUserById(1)
        );

        assertEquals("Usuario no encontrado", ex.getMessage());
    }

    @Test
    void getUserById_deberiaLanzarNotFound_siUsuarioBaneado() {
        AppUser user = mock(AppUser.class);

        when(appUserRepository.findById(1)).thenReturn(Optional.of(user));
        when(user.getDeletedAt()).thenReturn(null);
        when(user.getAccountState()).thenReturn(AccountState.banned);

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.getUserById(1)
        );

        assertEquals("Usuario no encontrado", ex.getMessage());
    }

    @Test
    void getMyProfile_deberiaRetornarPerfilActual() {
        AppUser user = mock(AppUser.class);
        CustomUserDetails principal = mock(CustomUserDetails.class);

        when(principal.getId()).thenReturn(1);
        mockAuthenticated(principal);

        when(appUserRepository.findById(1)).thenReturn(Optional.of(user));
        when(user.getId()).thenReturn(1);
        when(user.getLoginName()).thenReturn("juan");
        when(user.getEmailAddress()).thenReturn("juan@test.com");
        when(user.getDisplayName()).thenReturn("Juan");
        when(user.getBioText()).thenReturn("Bio");
        when(user.getAvatarUrl()).thenReturn("https://img.test/a.jpg");
        when(user.getAccessLevel()).thenReturn(AccessLevel.user);
        when(user.getAccountState()).thenReturn(AccountState.active);

        CurrentUserResponse response = userService.getMyProfile();

        assertEquals(1, response.id());
        assertEquals("juan@test.com", response.emailAddress());
        assertEquals("active", response.accountState());
    }

    @Test
    void listUsers_deberiaRetornarPagina() {
        AppUser user = mock(AppUser.class);
        Page<AppUser> page = new PageImpl<>(List.of(user), PageRequest.of(0, 20), 1);

        when(appUserRepository.findAllActive(any(Pageable.class))).thenReturn(page);
        when(user.getId()).thenReturn(1);
        when(user.getLoginName()).thenReturn("juan");
        when(user.getDisplayName()).thenReturn("Juan");
        when(user.getAccessLevel()).thenReturn(AccessLevel.user);
        when(user.getAccountState()).thenReturn(AccountState.active);

        PageResponse<UserListItemResponse> response = userService.listUsers(0, 20, "createdAt,desc");

        assertEquals(1, response.content().size());
        assertEquals("juan", response.content().get(0).loginName());
    }

    @Test
    void searchUsers_deberiaRetornarPagina() {
        AppUser user = mock(AppUser.class);
        Page<AppUser> page = new PageImpl<>(List.of(user), PageRequest.of(0, 20), 1);

        when(appUserRepository.searchUsers(eq("juan"), any(Pageable.class))).thenReturn(page);
        when(user.getId()).thenReturn(1);
        when(user.getLoginName()).thenReturn("juan");
        when(user.getDisplayName()).thenReturn("Juan");
        when(user.getAvatarUrl()).thenReturn("https://img.test/a.jpg");

        PageResponse<UserSearchItemResponse> response = userService.searchUsers("juan", 0, 20, null);

        assertEquals(1, response.content().size());
        assertEquals("Juan", response.content().get(0).displayName());
    }

    @Test
    void updateMyProfile_deberiaActualizarPerfil() {
        AppUser user = mock(AppUser.class);
        AppUser saved = mock(AppUser.class);
        CustomUserDetails principal = mock(CustomUserDetails.class);

        when(principal.getId()).thenReturn(1);
        mockAuthenticated(principal);
        when(appUserRepository.findById(1)).thenReturn(Optional.of(user));

        when(appUserRepository.save(user)).thenReturn(saved);
        when(saved.getId()).thenReturn(1);
        when(saved.getDisplayName()).thenReturn("Juan Actualizado");
        when(saved.getBioText()).thenReturn("Nueva bio");
        when(saved.getAvatarUrl()).thenReturn("https://img.test/new.jpg");
        when(saved.getUpdatedAt()).thenReturn(LocalDateTime.of(2026, 4, 22, 12, 0));

        UpdateMyProfileResponse response = userService.updateMyProfile(
                new UpdateMyProfileRequest("Juan Actualizado", "Nueva bio", "https://img.test/new.jpg")
        );

        assertEquals(1, response.id());
        assertEquals("Juan Actualizado", response.displayName());

        verify(user).setDisplayName("Juan Actualizado");
        verify(user).setBioText("Nueva bio");
        verify(user).setAvatarUrl("https://img.test/new.jpg");
    }

    @Test
    void changeAvatar_deberiaActualizarAvatar() {
        AppUser user = mock(AppUser.class);
        AppUser saved = mock(AppUser.class);
        CustomUserDetails principal = mock(CustomUserDetails.class);

        when(principal.getId()).thenReturn(1);
        mockAuthenticated(principal);
        when(appUserRepository.findById(1)).thenReturn(Optional.of(user));

        when(appUserRepository.save(user)).thenReturn(saved);
        when(saved.getAvatarUrl()).thenReturn("https://img.test/new.jpg");
        when(saved.getUpdatedAt()).thenReturn(LocalDateTime.of(2026, 4, 22, 12, 0));

        AvatarResponse response = userService.changeAvatar(
                new ChangeAvatarRequest("https://img.test/new.jpg")
        );

        assertEquals("https://img.test/new.jpg", response.avatarUrl());
        verify(user).setAvatarUrl("https://img.test/new.jpg");
    }

    @Test
    void changePassword_deberiaActualizarPasswordYRevocarSesiones() {
        AppUser user = mock(AppUser.class);
        UserSession session1 = mock(UserSession.class);
        UserSession session2 = mock(UserSession.class);
        CustomUserDetails principal = mock(CustomUserDetails.class);

        when(principal.getId()).thenReturn(1);
        mockAuthenticated(principal);
        when(appUserRepository.findById(1)).thenReturn(Optional.of(user));

        when(user.getId()).thenReturn(1);
        when(user.getPasswordHash()).thenReturn("oldHash");

        when(passwordEncoder.matches("oldPass", "oldHash")).thenReturn(true);
        when(passwordEncoder.matches("newPass123", "oldHash")).thenReturn(false);
        when(passwordEncoder.encode("newPass123")).thenReturn("newHash");

        when(userSessionRepository.findByUserIdAndRevokedAtIsNull(1)).thenReturn(List.of(session1, session2));

        MessageResponse response = userService.changePassword(
                new ChangePasswordRequest("oldPass", "newPass123")
        );

        assertEquals("Contraseña actualizada correctamente", response.message());
        verify(user).setPasswordHash("newHash");
        verify(appUserRepository).save(user);
        verify(session1).setRevokedAt(any(LocalDateTime.class));
        verify(session2).setRevokedAt(any(LocalDateTime.class));
        verify(userSessionRepository, times(2)).save(any(UserSession.class));
    }

    @Test
    void changePassword_deberiaLanzarBadRequest_siOldPasswordIncorrecta() {
        AppUser user = mock(AppUser.class);
        CustomUserDetails principal = mock(CustomUserDetails.class);

        when(principal.getId()).thenReturn(1);
        mockAuthenticated(principal);
        when(appUserRepository.findById(1)).thenReturn(Optional.of(user));
        when(user.getPasswordHash()).thenReturn("oldHash");
        when(passwordEncoder.matches("mala", "oldHash")).thenReturn(false);

        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> userService.changePassword(new ChangePasswordRequest("mala", "newPass123"))
        );

        assertEquals("La contraseña actual no es correcta", ex.getMessage());
    }

    @Test
    void changePassword_deberiaLanzarBadRequest_siNuevaEsIgual() {
        AppUser user = mock(AppUser.class);
        CustomUserDetails principal = mock(CustomUserDetails.class);

        when(principal.getId()).thenReturn(1);
        mockAuthenticated(principal);
        when(appUserRepository.findById(1)).thenReturn(Optional.of(user));
        when(user.getPasswordHash()).thenReturn("oldHash");
        when(passwordEncoder.matches("oldPass", "oldHash")).thenReturn(true);
        when(passwordEncoder.matches("oldPass", "oldHash")).thenReturn(true);

        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> userService.changePassword(new ChangePasswordRequest("oldPass", "oldPass"))
        );

        assertEquals("La nueva contraseña no puede ser igual a la anterior", ex.getMessage());
    }

    @Test
    void changeEmail_deberiaActualizarPendingEmail() {
        AppUser user = mock(AppUser.class);
        CustomUserDetails principal = mock(CustomUserDetails.class);

        when(principal.getId()).thenReturn(1);
        mockAuthenticated(principal);
        when(appUserRepository.findById(1)).thenReturn(Optional.of(user));
        when(user.getPasswordHash()).thenReturn("hash");
        when(passwordEncoder.matches("secret", "hash")).thenReturn(true);

        when(appUserRepository.existsByEmailAddressIgnoreCase("nuevo@test.com")).thenReturn(false);
        when(appUserRepository.existsByPendingEmailAddressIgnoreCase("nuevo@test.com")).thenReturn(false);
        when(user.getPendingEmailAddress()).thenReturn("nuevo@test.com");

        ChangeEmailResponse response = userService.changeEmail(
                new ChangeEmailRequest("nuevo@test.com", "secret")
        );

        assertEquals("Cambio de correo solicitado", response.message());
        assertEquals("nuevo@test.com", response.pendingEmailAddress());

        verify(user).setPendingEmailAddress("nuevo@test.com");
        verify(user).setEmailChangeRequestedAt(any(LocalDateTime.class));
        verify(appUserRepository).save(user);
    }

    @Test
    void changeEmail_deberiaLanzarBadRequest_siPasswordIncorrecta() {
        AppUser user = mock(AppUser.class);
        CustomUserDetails principal = mock(CustomUserDetails.class);

        when(principal.getId()).thenReturn(1);
        mockAuthenticated(principal);
        when(appUserRepository.findById(1)).thenReturn(Optional.of(user));
        when(user.getPasswordHash()).thenReturn("hash");
        when(passwordEncoder.matches("bad", "hash")).thenReturn(false);

        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> userService.changeEmail(new ChangeEmailRequest("nuevo@test.com", "bad"))
        );

        assertEquals("La contraseña no es correcta", ex.getMessage());
    }

    @Test
    void deactivateMyAccount_deberiaBanearYRevocarSesiones() {
        AppUser user = mock(AppUser.class);
        UserSession session = mock(UserSession.class);
        CustomUserDetails principal = mock(CustomUserDetails.class);

        when(principal.getId()).thenReturn(1);
        mockAuthenticated(principal);
        when(appUserRepository.findById(1)).thenReturn(Optional.of(user));
        when(user.getId()).thenReturn(1);
        when(userSessionRepository.findByUserIdAndRevokedAtIsNull(1)).thenReturn(List.of(session));

        MessageResponse response = userService.deactivateMyAccount();

        assertEquals("Cuenta desactivada correctamente", response.message());
        verify(user).setAccountState(AccountState.banned);
        verify(user).setDeletedAt(any(LocalDateTime.class));
        verify(appUserRepository).save(user);
        verify(session).setRevokedAt(any(LocalDateTime.class));
        verify(userSessionRepository).save(session);
    }

    @Test
    void getPublicAuthorProfile_deberiaRetornarPerfilPublico() {
        AppUser user = mock(AppUser.class);

        when(appUserRepository.findById(2)).thenReturn(Optional.of(user));
        when(user.getDeletedAt()).thenReturn(null);
        when(user.getAccountState()).thenReturn(AccountState.active);
        when(user.getId()).thenReturn(2);
        when(user.getDisplayName()).thenReturn("Ana");
        when(user.getBioText()).thenReturn("Bio");
        when(user.getAvatarUrl()).thenReturn("https://img.test/a.jpg");

        when(userFollowRepository.countByFollowedUserId(2)).thenReturn(15L);
        when(storyRepository.countByOwnerUserIdAndVisibilityStateIgnoreCaseAndPublicationStateIgnoreCase(
                2, "public", "published"
        )).thenReturn(4L);

        PublicAuthorProfileResponse response = userService.getPublicAuthorProfile(2);

        assertEquals(2, response.id());
        assertEquals("Ana", response.displayName());
        assertEquals(15L, response.followersCount());
        assertEquals(4L, response.storiesCount());
    }

    @Test
    void getPublicStoriesByAuthor_deberiaRetornarPagina() {
        AppUser user = mock(AppUser.class);
        Story story = mock(Story.class);
        Page<Story> page = new PageImpl<>(List.of(story), PageRequest.of(0, 20), 1);

        when(appUserRepository.findById(2)).thenReturn(Optional.of(user));
        when(user.getDeletedAt()).thenReturn(null);
        when(user.getAccountState()).thenReturn(AccountState.active);

        when(storyRepository.findByOwnerUserIdAndVisibilityStateIgnoreCaseAndPublicationStateIgnoreCase(
                eq(2), eq("public"), eq("published"), any(Pageable.class)
        )).thenReturn(page);

        when(story.getId()).thenReturn(10);
        when(story.getTitle()).thenReturn("Historia");
        when(story.getSlugText()).thenReturn("historia");
        when(story.getPublicationState()).thenReturn("published");
        when(story.getVisibilityState()).thenReturn("public");

        PageResponse<UserStoryItemResponse> response = userService.getPublicStoriesByAuthor(2, 0, 20, null);

        assertEquals(1, response.content().size());
        assertEquals("Historia", response.content().get(0).title());
    }

    @Test
    void getAuthenticatedUser_deberiaLanzarUnauthorized_siPrincipalNoEsCustomUserDetails() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("anonymousUser", null, List.of())
        );

        UnauthorizedException ex = assertThrows(
                UnauthorizedException.class,
                () -> userService.getMyProfile()
        );

        assertEquals("No autenticado", ex.getMessage());
    }

    private void mockAuthenticated(CustomUserDetails principal) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, List.of())
        );
    }
}