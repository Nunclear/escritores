package com.nunclear.escritores.controller;

import com.nunclear.escritores.entity.ContentReport;
import com.nunclear.escritores.entity.ReportStatus;
import com.nunclear.escritores.entity.Role;
import com.nunclear.escritores.entity.StoryComment;
import com.nunclear.escritores.entity.UserSanction;
import com.nunclear.escritores.service.JwtService;
import com.nunclear.escritores.service.ModerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/moderator")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class ModerationController {

    private final ModerationService moderationService;
    private final JwtService jwtService;

    // ==================== COMENTARIOS ====================

    /**
     * PATCH /api/moderator/comments/{id}/hide - Ocultar comentario
     */
    @PatchMapping("/comments/{id}/hide")
    public ResponseEntity<?> hideComment(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer id,
            @RequestBody HideCommentRequest request) {
        try {
            Role userRole = extractRoleFromHeader(authHeader);
            if (userRole != Role.MODERADOR && userRole != Role.ADMINISTRADOR) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    Map.of("error", "Solo moderadores pueden ocultar comentarios")
                );
            }

            moderationService.hideComment(id, request.getReason());
            return ResponseEntity.ok(Map.of("message", "Comentario ocultado"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * PATCH /api/moderator/comments/{id}/restore - Restaurar comentario
     */
    @PatchMapping("/comments/{id}/restore")
    public ResponseEntity<?> restoreComment(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer id) {
        try {
            Role userRole = extractRoleFromHeader(authHeader);
            if (userRole != Role.MODERADOR && userRole != Role.ADMINISTRADOR) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    Map.of("error", "No tienes permisos")
                );
            }

            moderationService.restoreComment(id);
            return ResponseEntity.ok(Map.of("message", "Comentario restaurado"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/moderator/comments/hidden - Listar comentarios ocultos
     */
    @GetMapping("/comments/hidden")
    public ResponseEntity<?> getHiddenComments(@RequestHeader("Authorization") String authHeader) {
        try {
            Role userRole = extractRoleFromHeader(authHeader);
            if (userRole != Role.MODERADOR && userRole != Role.ADMINISTRADOR) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    Map.of("error", "No tienes permisos")
                );
            }

            List<StoryComment> comments = moderationService.getHiddenComments();
            return ResponseEntity.ok(Map.of(
                "total", comments.size(),
                "comments", comments
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== REPORTES ====================

    /**
     * POST /api/moderator/reports - Crear reporte
     */
    @PostMapping("/reports")
    public ResponseEntity<?> createReport(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody CreateReportRequest request) {
        try {
            Integer userId = extractUserIdFromHeader(authHeader);

            Integer storyId = null;
            Integer chapterId = null;
            Integer commentId = null;

            if ("STORY".equalsIgnoreCase(request.getResourceType())) {
                storyId = request.getResourceId();
            } else if ("CHAPTER".equalsIgnoreCase(request.getResourceType())) {
                chapterId = request.getResourceId();
            } else if ("COMMENT".equalsIgnoreCase(request.getResourceType())) {
                commentId = request.getResourceId();
            }

            ContentReport report = moderationService.createReport(
                userId,
                storyId,
                chapterId,
                commentId,
                request.getReason()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(report);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/moderator/reports - Listar reportes
     */
    @GetMapping("/reports")
    public ResponseEntity<?> listReports(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) String status) {
        try {
            Role userRole = extractRoleFromHeader(authHeader);
            if (userRole != Role.MODERADOR && userRole != Role.ADMINISTRADOR) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    Map.of("error", "No tienes permisos")
                );
            }

            List<ContentReport> reports;
            if (status != null) {
                reports = moderationService.getReportsByStatus(ReportStatus.valueOf(status.toUpperCase()));
            } else {
                reports = moderationService.getPendingReports();
            }

            return ResponseEntity.ok(Map.of(
                "total", reports.size(),
                "reports", reports
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * PATCH /api/moderator/reports/{id}/resolve - Resolver reporte
     */
    @PatchMapping("/reports/{id}/resolve")
    public ResponseEntity<?> resolveReport(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer id,
            @RequestBody ResolveReportRequest request) {
        try {
            Integer userId = extractUserIdFromHeader(authHeader);
            Role userRole = extractRoleFromHeader(authHeader);
            if (userRole != Role.MODERADOR && userRole != Role.ADMINISTRADOR) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    Map.of("error", "No tienes permisos")
                );
            }

            moderationService.resolveReport(id, request.getResolution(), userId);
            return ResponseEntity.ok(Map.of("message", "Reporte resuelto"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * PATCH /api/moderator/reports/{id}/reject - Rechazar reporte
     */
    @PatchMapping("/reports/{id}/reject")
    public ResponseEntity<?> rejectReport(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer id,
            @RequestBody RejectReportRequest request) {
        try {
            Integer userId = extractUserIdFromHeader(authHeader);
            Role userRole = extractRoleFromHeader(authHeader);
            if (userRole != Role.MODERADOR && userRole != Role.ADMINISTRADOR) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    Map.of("error", "No tienes permisos")
                );
            }

            moderationService.rejectReport(id, request.getReason(), userId);
            return ResponseEntity.ok(Map.of("message", "Reporte rechazado"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== SANCIONES ====================

    /**
     * POST /api/moderator/sanctions/warning - Crear advertencia
     */
    @PostMapping("/sanctions/warning")
    public ResponseEntity<?> createWarning(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody CreateSanctionRequest request) {
        try {
            Role userRole = extractRoleFromHeader(authHeader);
            if (userRole != Role.MODERADOR && userRole != Role.ADMINISTRADOR) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    Map.of("error", "No tienes permisos")
                );
            }

            UserSanction sanction = moderationService.createWarning(request.getUserId(), request.getReason());
            return ResponseEntity.status(HttpStatus.CREATED).body(sanction);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/moderator/sanctions/temp-ban - Baneo temporal
     */
    @PostMapping("/sanctions/temp-ban")
    public ResponseEntity<?> applyTemporaryBan(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody TemporaryBanRequest request) {
        try {
            Role userRole = extractRoleFromHeader(authHeader);
            if (userRole != Role.MODERADOR && userRole != Role.ADMINISTRADOR) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    Map.of("error", "No tienes permisos")
                );
            }

            UserSanction sanction = moderationService.applyTemporaryBan(
                request.getUserId(),
                request.getReason(),
                request.getDurationDays()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(sanction);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/moderator/sanctions/{userId} - Listar sanciones de usuario
     */
    @GetMapping("/sanctions/{userId}")
    public ResponseEntity<?> getUserSanctions(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer userId) {
        try {
            Role userRole = extractRoleFromHeader(authHeader);
            if (userRole != Role.MODERADOR && userRole != Role.ADMINISTRADOR) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    Map.of("error", "No tienes permisos")
                );
            }

            List<UserSanction> sanctions = moderationService.getActiveSanctionsForUser(userId);
            return ResponseEntity.ok(Map.of(
                "userId", userId,
                "sanctions", sanctions,
                "total", sanctions.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/moderator/queue - Ver cola de moderación
     */
    @GetMapping("/queue")
    public ResponseEntity<?> getModerationQueue(@RequestHeader("Authorization") String authHeader) {
        try {
            Role userRole = extractRoleFromHeader(authHeader);
            if (userRole != Role.MODERADOR && userRole != Role.ADMINISTRADOR) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    Map.of("error", "No tienes permisos")
                );
            }

            List<ContentReport> pendingReports = moderationService.getPendingReports();
            List<StoryComment> hiddenComments = moderationService.getHiddenComments();
            List<UserSanction> activeSanctions = moderationService.getAllActiveSanctions();

            return ResponseEntity.ok(Map.of(
                "pendingReports", pendingReports.size(),
                "hiddenComments", hiddenComments.size(),
                "activeSanctions", activeSanctions.size(),
                "reports", pendingReports,
                "comments", hiddenComments
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Helper methods
    private Integer extractUserIdFromHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token no proporcionado");
        }
        String token = authHeader.substring(7);
        return jwtService.validateToken(token);
    }

    private Role extractRoleFromHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token no proporcionado");
        }
        String token = authHeader.substring(7);
        String roleStr = jwtService.extractRole(token);
        return Role.valueOf(roleStr);
    }

    // Request DTOs
    @lombok.Data
    public static class HideCommentRequest {
        private String reason;
    }

    @lombok.Data
    public static class CreateReportRequest {
        private String resourceType; // STORY, CHAPTER, COMMENT, USER
        private Integer resourceId;
        private String reason;
        private String description;
    }

    @lombok.Data
    public static class ResolveReportRequest {
        private String resolution;
    }

    @lombok.Data
    public static class RejectReportRequest {
        private String reason;
    }

    @lombok.Data
    public static class CreateSanctionRequest {
        private Integer userId;
        private String reason;
    }

    @lombok.Data
    public static class TemporaryBanRequest {
        private Integer userId;
        private String reason;
        private int durationDays;
    }
}
