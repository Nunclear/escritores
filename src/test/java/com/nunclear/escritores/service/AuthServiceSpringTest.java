package com.nunclear.escritores.service;

import com.nunclear.escritores.dto.request.*;
import com.nunclear.escritores.dto.response.*;
import com.nunclear.escritores.entity.*;
import com.nunclear.escritores.enums.AccessLevel;
import com.nunclear.escritores.enums.AccountState;
import com.nunclear.escritores.exception.BadRequestException;
import com.nunclear.escritores.exception.ConflictException;
import com.nunclear.escritores.exception.UnauthorizedException;
import com.nunclear.escritores.repository.*;
import com.nunclear.escritores.security.CustomUserDetails;
import com.nunclear.escritores.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private UserSessionRepository userSessionRepository;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private EmailVerificationTokenRepository emailVerificationTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private HttpServletRequest httpServletRequest;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "accessExpirationSeconds", 3600L);
        ReflectionTestUtils.setField(authService, "refreshExpirationSeconds", 86400L);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }


    @Test
    @DisplayName("registro exitoso")
    void register_success() {
        RegisterRequest request = new RegisterRequest(
                "usuario1",
                "usuario1@test.com",
                "Usuario Uno",
                "Password123!"
        );

        when(appUserRepository.existsByLoginNameIgnoreCase("usuario1")).thenReturn(false);
        when(appUserRepository.existsByEmailAddressIgnoreCase("usuario1@test.com")).thenReturn(false);
        when(passwordEncoder.encode("Password123!")).thenReturn("hashed-password");
        when(appUserRepository.save(any(AppUser.class))).thenAnswer(invocation -> {
            AppUser user = invocation.getArgument(0);
            user.setId(1);
            user.setCreatedAt(LocalDateTime.now());
            return user;
        });
        when(emailVerificationTokenRepository.save(any(EmailVerificationToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RegisterResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals(1, response.id());
        assertEquals("usuario1", response.loginName());
        assertEquals("usuario1@test.com", response.emailAddress());
        assertEquals("Usuario Uno", response.displayName());
        assertEquals("user", response.accessLevel());
        assertEquals("pending_verification", response.accountState());

        verify(appUserRepository).save(any(AppUser.class));
        verify(emailVerificationTokenRepository).save(any(EmailVerificationToken.class));
    }

    @Test
    @DisplayName("registro fallido por loginName duplicado")
    void register_fail_duplicateLoginName() {
        RegisterRequest request = new RegisterRequest(
                "usuario1",
                "usuario1@test.com",
                "Usuario Uno",
                "Password123!"
        );

        when(appUserRepository.existsByLoginNameIgnoreCase("usuario1")).thenReturn(true);

        ConflictException ex = assertThrows(ConflictException.class, () -> authService.register(request));

        assertEquals("El loginName ya está registrado", ex.getMessage());
        verify(appUserRepository, never()).save(any(AppUser.class));
    }

    @Test
    @DisplayName("registro fallido por emailAddress duplicado")
    void register_fail_duplicateEmail() {
        RegisterRequest request = new RegisterRequest(
                "usuario1",
                "usuario1@test.com",
                "Usuario Uno",
                "Password123!"
        );

        when(appUserRepository.existsByLoginNameIgnoreCase("usuario1")).thenReturn(false);
        when(appUserRepository.existsByEmailAddressIgnoreCase("usuario1@test.com")).thenReturn(true);

        ConflictException ex = assertThrows(ConflictException.class, () -> authService.register(request));

        assertEquals("El emailAddress ya está registrado", ex.getMessage());
        verify(appUserRepository, never()).save(any(AppUser.class));
    }

    @Test
    @DisplayName("login correcto con usuario válido")
    void login_success() {
        LoginRequest request = new LoginRequest("usuario1", "Password123!");
        AppUser user = buildUser(1, "usuario1", "usuario1@test.com", "hashed-password");

        when(appUserRepository.findByLoginNameIgnoreCaseOrEmailAddressIgnoreCase("usuario1", "usuario1"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Password123!", "hashed-password")).thenReturn(true);
        when(appUserRepository.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(httpServletRequest.getRemoteAddr()).thenReturn("127.0.0.1");
        when(httpServletRequest.getHeader("User-Agent")).thenReturn("JUnit-Test");
        when(jwtService.generateAccessToken(eq(1), eq("usuario1"), eq("user"), anyString()))
                .thenReturn("access-token");
        when(userSessionRepository.save(any(UserSession.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LoginResponse response = authService.login(request, httpServletRequest);

        assertNotNull(response);
        assertEquals("access-token", response.accessToken());
        assertNotNull(response.refreshToken());
        assertEquals("Bearer", response.tokenType());
        assertEquals(3600L, response.expiresIn());
        assertEquals(1, response.user().id());
        assertEquals("usuario1", response.user().loginName());
    }

    @Test
    @DisplayName("login fallido por contraseña incorrecta")
    void login_fail_wrongPassword() {
        LoginRequest request = new LoginRequest("usuario1", "PasswordIncorrecta");
        AppUser user = buildUser(1, "usuario1", "usuario1@test.com", "hashed-password");

        when(appUserRepository.findByLoginNameIgnoreCaseOrEmailAddressIgnoreCase("usuario1", "usuario1"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("PasswordIncorrecta", "hashed-password")).thenReturn(false);

        UnauthorizedException ex = assertThrows(UnauthorizedException.class,
                () -> authService.login(request, httpServletRequest));

        assertEquals("Credenciales inválidas", ex.getMessage());
        verify(userSessionRepository, never()).save(any(UserSession.class));
    }

    @Test
    @DisplayName("refresh token válido")
    void refresh_success() {
        String rawRefreshToken = "refresh-token-raw";
        String hashedRefreshToken = sha256Base64(rawRefreshToken);

        UserSession currentSession = new UserSession();
        currentSession.setId(10L);
        currentSession.setUserId(1);
        currentSession.setRefreshTokenHash(hashedRefreshToken);
        currentSession.setSessionIdentifier("old-session");
        currentSession.setExpiresAt(LocalDateTime.now().plusMinutes(30));

        AppUser user = buildUser(1, "usuario1", "usuario1@test.com", "hashed-password");

        when(userSessionRepository.findByRefreshTokenHashAndRevokedAtIsNull(hashedRefreshToken))
                .thenReturn(Optional.of(currentSession));
        when(appUserRepository.findById(1)).thenReturn(Optional.of(user));
        when(userSessionRepository.save(any(UserSession.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtService.generateAccessToken(eq(1), eq("usuario1"), eq("user"), anyString()))
                .thenReturn("new-access-token");

        RefreshResponse response = authService.refresh(new RefreshTokenRequest(rawRefreshToken));

        assertNotNull(response);
        assertEquals("new-access-token", response.accessToken());
        assertNotNull(response.refreshToken());
        assertEquals("Bearer", response.tokenType());
        assertEquals(3600L, response.expiresIn());
        assertNotNull(currentSession.getRevokedAt());
        verify(userSessionRepository, times(2)).save(any(UserSession.class));
    }

    @Test
    @DisplayName("refresh token inválido o expirado")
    void refresh_fail_invalidOrExpired() {
        String invalidRawToken = "invalid-token";
        String invalidHash = sha256Base64(invalidRawToken);

        when(userSessionRepository.findByRefreshTokenHashAndRevokedAtIsNull(invalidHash))
                .thenReturn(Optional.empty());

        UnauthorizedException invalidEx = assertThrows(UnauthorizedException.class,
                () -> authService.refresh(new RefreshTokenRequest(invalidRawToken)));
        assertEquals("Refresh token inválido", invalidEx.getMessage());

        String expiredRawToken = "expired-token";
        String expiredHash = sha256Base64(expiredRawToken);
        UserSession expiredSession = new UserSession();
        expiredSession.setUserId(1);
        expiredSession.setRefreshTokenHash(expiredHash);
        expiredSession.setExpiresAt(LocalDateTime.now().minusSeconds(1));

        when(userSessionRepository.findByRefreshTokenHashAndRevokedAtIsNull(expiredHash))
                .thenReturn(Optional.of(expiredSession));

        UnauthorizedException expiredEx = assertThrows(UnauthorizedException.class,
                () -> authService.refresh(new RefreshTokenRequest(expiredRawToken)));
        assertEquals("Refresh token expirado", expiredEx.getMessage());
    }

    @Test
    @DisplayName("logout invalida refresh token")
    void logout_success() {
        String rawRefreshToken = "refresh-token-raw";
        String hashedRefreshToken = sha256Base64(rawRefreshToken);

        UserSession session = new UserSession();
        session.setId(99L);
        session.setUserId(1);
        session.setRefreshTokenHash(hashedRefreshToken);
        session.setExpiresAt(LocalDateTime.now().plusDays(1));

        when(userSessionRepository.findByRefreshTokenHashAndRevokedAtIsNull(hashedRefreshToken))
                .thenReturn(Optional.of(session));
        when(userSessionRepository.save(any(UserSession.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MessageResponse response = authService.logout(new LogoutRequest(rawRefreshToken));

        assertEquals("Sesión cerrada correctamente", response.message());
        assertNotNull(session.getRevokedAt());
    }

    @Test
    @DisplayName("me devuelve el usuario autenticado")
    void me_returnsAuthenticatedUser() {
        AppUser user = buildUser(1, "usuario1", "usuario1@test.com", "hashed-password");
        user.setBioText("Bio de prueba");
        user.setAvatarUrl("https://example.com/avatar.png");
        user.setAccountState(AccountState.active);

        setAuthenticatedUser(user);
        when(appUserRepository.findById(1)).thenReturn(Optional.of(user));

        CurrentUserResponse response = authService.me();

        assertNotNull(response);
        assertEquals(1, response.id());
        assertEquals("usuario1", response.loginName());
        assertEquals("usuario1@test.com", response.emailAddress());
        assertEquals("Usuario Demo", response.displayName());
        assertEquals("active", response.accountState());
    }

    @Test
    @DisplayName("forgot password responde sin filtrar si el correo existe")
    void forgotPassword_doesNotLeakEmailExistence() {
        ForgotPasswordRequest existingEmailRequest = new ForgotPasswordRequest("usuario1@test.com");
        ForgotPasswordRequest missingEmailRequest = new ForgotPasswordRequest("noexiste@test.com");

        AppUser user = buildUser(1, "usuario1", "usuario1@test.com", "hashed-password");

        PasswordResetToken previous1 = new PasswordResetToken();
        previous1.setId(1L);
        previous1.setUserId(1);
        PasswordResetToken previous2 = new PasswordResetToken();
        previous2.setId(2L);
        previous2.setUserId(1);

        when(appUserRepository.findByEmailAddressIgnoreCase("usuario1@test.com")).thenReturn(Optional.of(user));
        when(appUserRepository.findByEmailAddressIgnoreCase("noexiste@test.com")).thenReturn(Optional.empty());
        when(passwordResetTokenRepository.findByUserIdAndUsedAtIsNull(1)).thenReturn(List.of(previous1, previous2));
        when(passwordResetTokenRepository.save(any(PasswordResetToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MessageResponse existingResponse = authService.forgotPassword(existingEmailRequest);
        MessageResponse missingResponse = authService.forgotPassword(missingEmailRequest);

        assertEquals("Si el correo existe, se enviaron instrucciones", existingResponse.message());
        assertEquals("Si el correo existe, se enviaron instrucciones", missingResponse.message());
    }

    @Test
    @DisplayName("reset password falla con token inválido")
    void resetPassword_fail_invalidToken() {
        ResetPasswordRequest request = new ResetPasswordRequest("token-invalido", "NuevaPassword123!");
        String hashedToken = sha256Base64("token-invalido");

        when(passwordResetTokenRepository.findByTokenHashAndUsedAtIsNull(hashedToken))
                .thenReturn(Optional.empty());

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> authService.resetPassword(request));

        assertEquals("Token inválido", ex.getMessage());
        verify(appUserRepository, never()).save(any(AppUser.class));
    }

    private AppUser buildUser(Integer id, String loginName, String email, String passwordHash) {
        AppUser user = new AppUser();
        user.setId(id);
        user.setLoginName(loginName);
        user.setEmailAddress(email);
        user.setDisplayName("Usuario Demo");
        user.setPasswordHash(passwordHash);
        user.setAccessLevel(AccessLevel.user);
        user.setAccountState(AccountState.active);
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

    private String sha256Base64(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
