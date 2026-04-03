package com.nunclear.escritores.controller;

import com.nunclear.escritores.entity.GlobalNotice;
import com.nunclear.escritores.service.GlobalNoticeService;
import com.nunclear.escritores.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class GlobalNoticeController {
    private final GlobalNoticeService noticeService;
    private final JwtService jwtService;

    /**
     * Crear un comunicado global (solo admin)
     */
    @PostMapping
    public ResponseEntity<?> createNotice(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody CreateNoticeRequest request) {
        try {
            Integer userId = extractUserIdFromHeader(authHeader);
            GlobalNotice notice = noticeService.createNotice(
                userId,
                request.getTitle(),
                request.getContent(),
                request.getScheduledAt()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(notice);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Actualizar un comunicado global (solo admin)
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateNotice(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer id,
            @RequestBody UpdateNoticeRequest request) {
        try {
            extractUserIdFromHeader(authHeader); // Verify token exists
            GlobalNotice notice = noticeService.updateNotice(
                id,
                request.getTitle(),
                request.getContent(),
                request.getScheduledAt()
            );
            return ResponseEntity.ok(notice);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Activar un comunicado
     */
    @PatchMapping("/{id}/enable")
    public ResponseEntity<?> enableNotice(@PathVariable Integer id) {
        try {
            noticeService.enableNotice(id);
            return ResponseEntity.ok(Map.of("message", "Comunicado activado"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Desactivar un comunicado
     */
    @PatchMapping("/{id}/disable")
    public ResponseEntity<?> disableNotice(@PathVariable Integer id) {
        try {
            noticeService.disableNotice(id);
            return ResponseEntity.ok(Map.of("message", "Comunicado desactivado"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Obtener comunicado por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getNotice(@PathVariable Integer id) {
        try {
            GlobalNotice notice = noticeService.getNotice(id);
            return ResponseEntity.ok(notice);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Listar comunicados activos (público)
     */
    @GetMapping("/active")
    public ResponseEntity<?> getActiveNotices() {
        try {
            List<GlobalNotice> notices = noticeService.getActiveNotices();
            return ResponseEntity.ok(notices);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Listar todos los comunicados (solo admin)
     */
    @GetMapping
    public ResponseEntity<?> getAllNotices(@RequestHeader("Authorization") String authHeader) {
        try {
            extractUserIdFromHeader(authHeader); // Verify admin
            List<GlobalNotice> notices = noticeService.getAllNotices();
            return ResponseEntity.ok(notices);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Eliminar un comunicado
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotice(@PathVariable Integer id) {
        try {
            noticeService.deleteNotice(id);
            return ResponseEntity.noContent().build();
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

    @lombok.Data
    public static class CreateNoticeRequest {
        private String title;
        private String content;
        private LocalDateTime scheduledAt;
    }

    @lombok.Data
    public static class UpdateNoticeRequest {
        private String title;
        private String content;
        private LocalDateTime scheduledAt;
    }
}
