package com.nunclear.escritores.controller;

import com.nunclear.escritores.dto.AppUserDTO;
import com.nunclear.escritores.service.AuthService;
import com.nunclear.escritores.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    /**
     * POST /api/auth/register - Registrar nuevo usuario
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            if (request.getEmail() == null || request.getPassword() == null || request.getUsername() == null) {
                return ResponseEntity.badRequest().body(
                    Map.of("error", "Email, username y password son requeridos")
                );
            }

            AppUserDTO user = authService.register(
                request.getEmail(),
                request.getPassword(),
                request.getUsername()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(
                Map.of(
                    "message", "Usuario registrado exitosamente",
                    "user", user
                )
            );
        } catch (Exception e) {
            log.error("Error en registro: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                Map.of("error", e.getMessage())
            );
        }
    }

    /**
     * POST /api/auth/login - Iniciar sesión
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            if (request.getEmail() == null || request.getPassword() == null) {
                return ResponseEntity.badRequest().body(
                    Map.of("error", "Email y password son requeridos")
                );
            }

            AuthService.LoginResponse response = authService.login(
                request.getEmail(),
                request.getPassword()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error en login: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                Map.of("error", e.getMessage())
            );
        }
    }

    /**
     * POST /api/auth/refresh - Renovar token
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        try {
            if (request.getRefreshToken() == null) {
                return ResponseEntity.badRequest().body(
                    Map.of("error", "Refresh token es requerido")
                );
            }

            AuthService.LoginResponse response = authService.refreshToken(request.getRefreshToken());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error en refresh token: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                Map.of("error", e.getMessage())
            );
        }
    }

    /**
     * GET /api/auth/me - Obtener usuario autenticado
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of("error", "Token no proporcionado")
                );
            }

            String token = authHeader.substring(7);
            Integer userId = jwtService.validateToken(token);

            AppUserDTO user = authService.getCurrentUser(userId);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            log.error("Error obteniendo usuario actual: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                Map.of("error", e.getMessage())
            );
        }
    }

    /**
     * POST /api/auth/forgot-password - Recuperar contraseña
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        try {
            if (request.getEmail() == null) {
                return ResponseEntity.badRequest().body(
                    Map.of("error", "Email es requerido")
                );
            }

            authService.forgotPassword(request.getEmail());
            return ResponseEntity.ok(
                Map.of("message", "Se envió un email con instrucciones para recuperar tu contraseña")
            );
        } catch (Exception e) {
            log.error("Error en forgot password: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                Map.of("error", e.getMessage())
            );
        }
    }

    /**
     * POST /api/auth/reset-password - Restablecer contraseña
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            if (request.getToken() == null || request.getNewPassword() == null) {
                return ResponseEntity.badRequest().body(
                    Map.of("error", "Token y newPassword son requeridos")
                );
            }

            authService.resetPassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok(
                Map.of("message", "Contraseña restablecida exitosamente")
            );
        } catch (Exception e) {
            log.error("Error en reset password: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                Map.of("error", e.getMessage())
            );
        }
    }

    /**
     * POST /api/auth/change-password - Cambiar contraseña (usuario autenticado)
     */
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ChangePasswordRequest request) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of("error", "Token no proporcionado")
                );
            }

            String token = authHeader.substring(7);
            Integer userId = jwtService.validateToken(token);

            authService.changePassword(userId, request.getCurrentPassword(), request.getNewPassword());
            return ResponseEntity.ok(
                Map.of("message", "Contraseña cambiada exitosamente")
            );
        } catch (Exception e) {
            log.error("Error en change password: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                Map.of("error", e.getMessage())
            );
        }
    }

    /**
     * POST /api/auth/verify-email - Verificar email
     */
    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestBody VerifyEmailRequest request) {
        try {
            if (request.getToken() == null) {
                return ResponseEntity.badRequest().body(
                    Map.of("error", "Token es requerido")
                );
            }

            authService.verifyEmail(request.getToken());
            return ResponseEntity.ok(
                Map.of("message", "Email verificado exitosamente")
            );
        } catch (Exception e) {
            log.error("Error en verify email: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                Map.of("error", e.getMessage())
            );
        }
    }

    /**
     * POST /api/auth/send-verification - Enviar email de verificación
     */
    @PostMapping("/send-verification")
    public ResponseEntity<?> sendVerification(@RequestBody SendVerificationRequest request) {
        try {
            if (request.getEmail() == null) {
                return ResponseEntity.badRequest().body(
                    Map.of("error", "Email es requerido")
                );
            }

            authService.sendEmailVerification(request.getEmail());
            return ResponseEntity.ok(
                Map.of("message", "Se envió un email de verificación")
            );
        } catch (Exception e) {
            log.error("Error en send verification: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                Map.of("error", e.getMessage())
            );
        }
    }

    /**
     * POST /api/auth/logout - Cerrar sesión (client-side: eliminar tokens)
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // En una implementación con token blacklist, aquí se agregaría el token a la lista negra
        return ResponseEntity.ok(
            Map.of("message", "Sesión cerrada exitosamente. Elimina tus tokens del cliente.")
        );
    }

    // Request DTOs
    @lombok.Data
    public static class RegisterRequest {
        private String email;
        private String username;
        private String password;
    }

    @lombok.Data
    public static class LoginRequest {
        private String email;
        private String password;
    }

    @lombok.Data
    public static class RefreshTokenRequest {
        private String refreshToken;
    }

    @lombok.Data
    public static class ForgotPasswordRequest {
        private String email;
    }

    @lombok.Data
    public static class ResetPasswordRequest {
        private String token;
        private String newPassword;
    }

    @lombok.Data
    public static class ChangePasswordRequest {
        private String currentPassword;
        private String newPassword;
    }

    @lombok.Data
    public static class VerifyEmailRequest {
        private String token;
    }

    @lombok.Data
    public static class SendVerificationRequest {
        private String email;
    }
}
