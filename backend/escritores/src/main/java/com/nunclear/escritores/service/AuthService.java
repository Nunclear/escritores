package com.nunclear.escritores.service;

import com.nunclear.escritores.dto.AppUserDTO;
import com.nunclear.escritores.entity.AppUser;
import com.nunclear.escritores.entity.Role;
import com.nunclear.escritores.entity.UserStatus;
import com.nunclear.escritores.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /**
     * Registrar nuevo usuario
     */
    public AppUserDTO register(String email, String password, String username) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email ya registrado");
        }

        AppUser user = AppUser.builder()
                .email(email)
                .username(username)
                .password(passwordEncoder.encode(password))
                .role(Role.USUARIO)
                .status(UserStatus.ACTIVE)
                .emailVerified(false)
                .createdAt(LocalDateTime.now())
                .build();

        AppUser saved = userRepository.save(user);
        return convertToDTO(saved);
    }

    /**
     * Realizar login con email y contraseña
     */
    public LoginResponse login(String email, String password) {
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Contraseña inválida");
        }

        if (user.getStatus() == UserStatus.BANNED) {
            throw new RuntimeException("Usuario baneado");
        }

        if (user.getStatus() == UserStatus.SUSPENDED) {
            throw new RuntimeException("Usuario suspendido");
        }

        String accessToken = jwtService.generateToken(user.getId(), user.getEmail(), user.getRole().name());
        String refreshToken = jwtService.generateRefreshToken(user.getId());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(convertToDTO(user))
                .expiresIn(3600)
                .build();
    }

    /**
     * Renovar token JWT
     */
    public LoginResponse refreshToken(String refreshToken) {
        Integer userId = jwtService.validateRefreshToken(refreshToken);
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String newAccessToken = jwtService.generateToken(user.getId(), user.getEmail(), user.getRole().name());
        String newRefreshToken = jwtService.generateRefreshToken(user.getId());

        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .user(convertToDTO(user))
                .expiresIn(3600)
                .build();
    }

    /**
     * Obtener usuario actual autenticado
     */
    public AppUserDTO getCurrentUser(Integer userId) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return convertToDTO(user);
    }

    /**
     * Recuperar contraseña - generar token
     */
    public void forgotPassword(String email) {
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email no registrado"));

        String resetToken = jwtService.generatePasswordResetToken(user.getId());
        // TODO: Enviar email con el token
        user.setPasswordResetToken(resetToken);
        user.setPasswordResetTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(user);
    }

    /**
     * Restablecer contraseña
     */
    public void resetPassword(String token, String newPassword) {
        Integer userId = jwtService.validatePasswordResetToken(token);
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiry(null);
        userRepository.save(user);
    }

    /**
     * Cambiar contraseña (usuario autenticado)
     */
    public void changePassword(Integer userId, String currentPassword, String newPassword) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Contraseña actual inválida");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    /**
     * Verificar email
     */
    public void verifyEmail(String token) {
        Integer userId = jwtService.validateEmailVerificationToken(token);
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        user.setEmailVerified(true);
        userRepository.save(user);
    }

    /**
     * Enviar nuevo email de verificación
     */
    public void sendEmailVerification(String email) {
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email no encontrado"));

        if (user.isEmailVerified()) {
            throw new RuntimeException("Email ya verificado");
        }

        String verificationToken = jwtService.generateEmailVerificationToken(user.getId());
        // TODO: Enviar email con el token
    }

    /**
     * Cambiar email (requiere verificación)
     */
    public void changeEmail(Integer userId, String newEmail, String verificationToken) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (userRepository.findByEmail(newEmail).isPresent()) {
            throw new RuntimeException("Email ya en uso");
        }

        Integer tokenUserId = jwtService.validateEmailVerificationToken(verificationToken);
        if (!tokenUserId.equals(userId)) {
            throw new RuntimeException("Token inválido");
        }

        user.setEmail(newEmail);
        user.setEmailVerified(true);
        userRepository.save(user);
    }

    // Helper method
    private AppUserDTO convertToDTO(AppUser user) {
        return AppUserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .role(user.getRole())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .build();
    }

    // DTO para respuesta de login
    @lombok.Data
    @lombok.Builder
    public static class LoginResponse {
        private String accessToken;
        private String refreshToken;
        private AppUserDTO user;
        private long expiresIn;
    }
}
