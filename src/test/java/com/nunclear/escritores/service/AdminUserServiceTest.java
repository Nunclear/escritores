package com.nunclear.escritores.service;

import com.nunclear.escritores.dto.request.UpdateUserAccessLevelRequest;
import com.nunclear.escritores.dto.request.UpdateUserAccountStateRequest;
import com.nunclear.escritores.dto.response.AdminUserAccessLevelResponse;
import com.nunclear.escritores.dto.response.AdminUserAccountStateResponse;
import com.nunclear.escritores.dto.response.AdminUserByRoleItemResponse;
import com.nunclear.escritores.dto.response.AdminUserByStateItemResponse;
import com.nunclear.escritores.dto.response.PageResponse;
import com.nunclear.escritores.dto.response.UserHistoryResponse;
import com.nunclear.escritores.entity.AppUser;
import com.nunclear.escritores.entity.UserChangeHistory;
import com.nunclear.escritores.entity.UserSession;
import com.nunclear.escritores.enums.AccessLevel;
import com.nunclear.escritores.enums.AccountState;
import com.nunclear.escritores.exception.BadRequestException;
import com.nunclear.escritores.exception.ResourceNotFoundException;
import com.nunclear.escritores.exception.UnauthorizedException;
import com.nunclear.escritores.repository.AppUserRepository;
import com.nunclear.escritores.repository.UserChangeHistoryRepository;
import com.nunclear.escritores.repository.UserSessionRepository;
import com.nunclear.escritores.security.CustomUserDetails;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminUserService - Mockito")
class AdminUserServiceTest {

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private UserChangeHistoryRepository userChangeHistoryRepository;

    @Mock
    private UserSessionRepository userSessionRepository;

    @InjectMocks
    private AdminUserService adminUserService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    @DisplayName("Rojo - prueba falla correctamente")
    class Rojo {

        @Test
        @DisplayName("updateAccessLevel falla si el rol ya es el mismo")
        void updateAccessLevel_fail_sameValue() {
            AppUser targetUser = buildUser(10, "autor", AccessLevel.user, AccountState.active);
            AppUser adminUser = buildUser(1, "admin", AccessLevel.admin, AccountState.active);

            setAuthenticatedUser(adminUser);

            when(appUserRepository.findById(10)).thenReturn(Optional.of(targetUser));
            when(appUserRepository.findById(1)).thenReturn(Optional.of(adminUser));

            BadRequestException ex = assertThrows(BadRequestException.class, () ->
                    adminUserService.updateAccessLevel(10, new UpdateUserAccessLevelRequest("user")));

            assertEquals("El usuario ya tiene ese accessLevel", ex.getMessage());
            verify(userChangeHistoryRepository, never()).save(any(UserChangeHistory.class));
        }

        @Test
        @DisplayName("updateAccessLevel falla con accessLevel inválido")
        void updateAccessLevel_fail_invalidAccessLevel() {
            AppUser targetUser = buildUser(10, "autor", AccessLevel.user, AccountState.active);
            AppUser adminUser = buildUser(1, "admin", AccessLevel.admin, AccountState.active);

            setAuthenticatedUser(adminUser);

            when(appUserRepository.findById(10)).thenReturn(Optional.of(targetUser));
            when(appUserRepository.findById(1)).thenReturn(Optional.of(adminUser));

            BadRequestException ex = assertThrows(BadRequestException.class, () ->
                    adminUserService.updateAccessLevel(10, new UpdateUserAccessLevelRequest("superadmin")));

            assertEquals("accessLevel inválido", ex.getMessage());
            verify(appUserRepository, never()).save(any(AppUser.class));
        }

        @Test
        @DisplayName("updateAccountState falla con accountState inválido")
        void updateAccountState_fail_invalidState() {
            AppUser targetUser = buildUser(10, "autor", AccessLevel.user, AccountState.active);
            AppUser adminUser = buildUser(1, "admin", AccessLevel.admin, AccountState.active);

            setAuthenticatedUser(adminUser);

            when(appUserRepository.findById(10)).thenReturn(Optional.of(targetUser));
            when(appUserRepository.findById(1)).thenReturn(Optional.of(adminUser));

            BadRequestException ex = assertThrows(BadRequestException.class, () ->
                    adminUserService.updateAccountState(10, new UpdateUserAccountStateRequest("frozen")));

            assertEquals("accountState inválido", ex.getMessage());
            verify(appUserRepository, never()).save(any(AppUser.class));
        }

        @Test
        @DisplayName("operaciones fallan si el usuario objetivo no existe")
        void targetUser_notFound() {
            when(appUserRepository.findById(999)).thenReturn(Optional.empty());

            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                    adminUserService.getUserHistory(999));

            assertEquals("Usuario no encontrado", ex.getMessage());
        }

        @Test
        @DisplayName("operaciones fallan si no hay usuario autenticado válido")
        void authenticatedUser_invalidPrincipal() {
            Authentication authentication = mock(Authentication.class);
            SecurityContext securityContext = mock(SecurityContext.class);
            when(authentication.getPrincipal()).thenReturn("anonymousUser");
            when(securityContext.getAuthentication()).thenReturn(authentication);
            SecurityContextHolder.setContext(securityContext);

            AppUser targetUser = buildUser(10, "autor", AccessLevel.user, AccountState.active);
            when(appUserRepository.findById(10)).thenReturn(Optional.of(targetUser));

            UnauthorizedException ex = assertThrows(UnauthorizedException.class, () ->
                    adminUserService.updateAccessLevel(10, new UpdateUserAccessLevelRequest("moderator")));

            assertEquals("No autenticado", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("Verde - prueba pasa con la implementación mínima")
    class Verde {

        @Test
        @DisplayName("updateAccessLevel cambia el rol y guarda historial")
        void updateAccessLevel_success() {
            AppUser targetUser = buildUser(10, "autor", AccessLevel.user, AccountState.active);
            targetUser.setUpdatedAt(LocalDateTime.now());
            AppUser adminUser = buildUser(1, "admin", AccessLevel.admin, AccountState.active);

            setAuthenticatedUser(adminUser);

            when(appUserRepository.findById(10)).thenReturn(Optional.of(targetUser));
            when(appUserRepository.findById(1)).thenReturn(Optional.of(adminUser));
            when(appUserRepository.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(userChangeHistoryRepository.save(any(UserChangeHistory.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            AdminUserAccessLevelResponse response = adminUserService.updateAccessLevel(
                    10,
                    new UpdateUserAccessLevelRequest("moderator")
            );

            assertNotNull(response);
            assertEquals(10, response.id());
            assertEquals("moderator", response.accessLevel());
            assertEquals(AccessLevel.moderator, targetUser.getAccessLevel());

            ArgumentCaptor<UserChangeHistory> captor = ArgumentCaptor.forClass(UserChangeHistory.class);
            verify(userChangeHistoryRepository).save(captor.capture());
            UserChangeHistory history = captor.getValue();

            assertEquals(10, history.getUserId());
            assertEquals("accessLevel", history.getChangedField());
            assertEquals("user", history.getOldValue());
            assertEquals("moderator", history.getNewValue());
            assertEquals(1, history.getChangedByUserId());
        }

        @Test
        @DisplayName("updateAccountState cambia el estado y guarda historial")
        void updateAccountState_success() {
            AppUser targetUser = buildUser(10, "autor", AccessLevel.user, AccountState.active);
            targetUser.setUpdatedAt(LocalDateTime.now());
            AppUser adminUser = buildUser(1, "admin", AccessLevel.admin, AccountState.active);

            setAuthenticatedUser(adminUser);

            when(appUserRepository.findById(10)).thenReturn(Optional.of(targetUser));
            when(appUserRepository.findById(1)).thenReturn(Optional.of(adminUser));
            when(appUserRepository.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(userChangeHistoryRepository.save(any(UserChangeHistory.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            AdminUserAccountStateResponse response = adminUserService.updateAccountState(
                    10,
                    new UpdateUserAccountStateRequest("suspended")
            );

            assertNotNull(response);
            assertEquals(10, response.id());
            assertEquals("suspended", response.accountState());
            assertEquals(AccountState.suspended, targetUser.getAccountState());

            ArgumentCaptor<UserChangeHistory> captor = ArgumentCaptor.forClass(UserChangeHistory.class);
            verify(userChangeHistoryRepository).save(captor.capture());
            UserChangeHistory history = captor.getValue();

            assertEquals("accountState", history.getChangedField());
            assertEquals("active", history.getOldValue());
            assertEquals("suspended", history.getNewValue());
            assertEquals(1, history.getChangedByUserId());
        }

        @Test
        @DisplayName("updateAccountState banea al usuario y revoca sesiones activas")
        void updateAccountState_banned_revokesSessions() {
            AppUser targetUser = buildUser(10, "autor", AccessLevel.user, AccountState.active);
            targetUser.setUpdatedAt(LocalDateTime.now());
            AppUser adminUser = buildUser(1, "admin", AccessLevel.admin, AccountState.active);

            UserSession session1 = new UserSession();
            session1.setId(100L);
            session1.setUserId(10);
            UserSession session2 = new UserSession();
            session2.setId(101L);
            session2.setUserId(10);

            setAuthenticatedUser(adminUser);

            when(appUserRepository.findById(10)).thenReturn(Optional.of(targetUser));
            when(appUserRepository.findById(1)).thenReturn(Optional.of(adminUser));
            when(userSessionRepository.findByUserIdAndRevokedAtIsNull(10)).thenReturn(List.of(session1, session2));
            when(appUserRepository.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(userChangeHistoryRepository.save(any(UserChangeHistory.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));
            when(userSessionRepository.save(any(UserSession.class))).thenAnswer(invocation -> invocation.getArgument(0));

            AdminUserAccountStateResponse response = adminUserService.updateAccountState(
                    10,
                    new UpdateUserAccountStateRequest("banned")
            );

            assertEquals("banned", response.accountState());
            assertNotNull(session1.getRevokedAt());
            assertNotNull(session2.getRevokedAt());
            verify(userSessionRepository, times(2)).save(any(UserSession.class));
        }
    }

    @Nested
    @DisplayName("Refactorización - mejora del código manteniendo las pruebas en verde")
    class Refactorizacion {

        @Test
        @DisplayName("listUsersByRole devuelve usuarios paginados")
        void listUsersByRole_success() {
            AppUser user1 = buildUser(10, "user1", AccessLevel.user, AccountState.active);
            AppUser user2 = buildUser(11, "user2", AccessLevel.user, AccountState.suspended);
            Page<AppUser> page = new PageImpl<>(List.of(user1, user2), PageRequest.of(0, 20), 2);

            when(appUserRepository.findByAccessLevelAndDeletedAtIsNull(eq(AccessLevel.user), any(Pageable.class)))
                    .thenReturn(page);

            PageResponse<AdminUserByRoleItemResponse> response = adminUserService.listUsersByRole("user", 0, 20, null);

            assertNotNull(response);
            assertEquals(2, response.content().size());
            assertEquals("user1", response.content().get(0).loginName());
            assertEquals("user", response.content().get(0).accessLevel());
            assertEquals(0, response.page());
            assertEquals(20, response.size());
            assertEquals(2, response.totalElements());
            assertEquals(1, response.totalPages());
        }

        @Test
        @DisplayName("listUsersByState devuelve usuarios paginados")
        void listUsersByState_success() {
            AppUser user1 = buildUser(10, "user1", AccessLevel.user, AccountState.active);
            AppUser user2 = buildUser(11, "user2", AccessLevel.moderator, AccountState.active);
            Page<AppUser> page = new PageImpl<>(List.of(user1, user2), PageRequest.of(0, 20), 2);

            when(appUserRepository.findByAccountStateAndDeletedAtIsNull(eq(AccountState.active), any(Pageable.class)))
                    .thenReturn(page);

            PageResponse<AdminUserByStateItemResponse> response = adminUserService.listUsersByState("active", 0, 20, null);

            assertNotNull(response);
            assertEquals(2, response.content().size());
            assertEquals("user1", response.content().get(0).loginName());
            assertEquals("active", response.content().get(0).accountState());
            assertEquals(2, response.totalElements());
        }

        @Test
        @DisplayName("getUserHistory devuelve eventos ordenados desde repositorio")
        void getUserHistory_success() {
            AppUser targetUser = buildUser(10, "autor", AccessLevel.user, AccountState.active);

            UserChangeHistory event1 = new UserChangeHistory();
            event1.setUserId(10);
            event1.setChangedField("accessLevel");
            event1.setOldValue("user");
            event1.setNewValue("moderator");
            event1.setChangedAt(LocalDateTime.now().minusDays(1));
            event1.setChangedByUserId(1);

            UserChangeHistory event2 = new UserChangeHistory();
            event2.setUserId(10);
            event2.setChangedField("accountState");
            event2.setOldValue("active");
            event2.setNewValue("suspended");
            event2.setChangedAt(LocalDateTime.now());
            event2.setChangedByUserId(1);

            when(appUserRepository.findById(10)).thenReturn(Optional.of(targetUser));
            when(userChangeHistoryRepository.findByUserIdOrderByChangedAtDesc(eq(10), any(PageRequest.class)))
                    .thenReturn(new PageImpl<>(List.of(event2, event1)));

            UserHistoryResponse response = adminUserService.getUserHistory(10);

            assertNotNull(response);
            assertEquals(2, response.events().size());
            assertEquals("accountState", response.events().get(0).changedField());
            assertEquals("accessLevel", response.events().get(1).changedField());
        }
    }

    private AppUser buildUser(Integer id, String loginName, AccessLevel accessLevel, AccountState accountState) {
        AppUser user = new AppUser();
        user.setId(id);
        user.setLoginName(loginName);
        user.setEmailAddress(loginName + "@test.com");
        user.setPasswordHash("hashed");
        user.setDisplayName("Display " + loginName);
        user.setAccessLevel(accessLevel);
        user.setAccountState(accountState);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }

    private void setAuthenticatedUser(AppUser user) {
        CustomUserDetails userDetails = new CustomUserDetails(user);
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }
}
