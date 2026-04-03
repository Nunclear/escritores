package com.nunclear.escritores.controller;

import com.nunclear.escritores.entity.Story;
import com.nunclear.escritores.entity.StoryView;
import com.nunclear.escritores.service.JwtService;
import com.nunclear.escritores.service.StoryMetricsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/metrics")
@RequiredArgsConstructor
public class StoryMetricsController {
    private final StoryMetricsService metricsService;
    private final JwtService jwtService;

    /**
     * Registrar una visita a una historia
     */
    @PostMapping("/stories/{storyId}/view")
    public ResponseEntity<?> recordView(
            @PathVariable Integer storyId,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            HttpServletRequest request) {
        try {
            Integer userId = null;
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                try {
                    String token = authHeader.substring(7);
                    userId = jwtService.validateToken(token);
                } catch (Exception e) {
                    // Usuario anónimo
                }
            }

            String ipAddress = request.getRemoteAddr();
            String userAgent = request.getHeader("User-Agent");

            StoryView view = metricsService.recordView(storyId, userId, ipAddress, userAgent);
            return ResponseEntity.status(HttpStatus.CREATED).body(view);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Obtener total de visitas de una historia
     */
    @GetMapping("/stories/{storyId}/total-views")
    public ResponseEntity<?> getTotalViews(@PathVariable Integer storyId) {
        try {
            long totalViews = metricsService.getTotalViews(storyId);
            return ResponseEntity.ok(Map.of("storyId", storyId, "totalViews", totalViews));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Obtener usuarios únicos que visitaron una historia
     */
    @GetMapping("/stories/{storyId}/unique-users")
    public ResponseEntity<?> getUniqueUserViews(@PathVariable Integer storyId) {
        try {
            long uniqueUsers = metricsService.getUniqueUserViews(storyId);
            return ResponseEntity.ok(Map.of("storyId", storyId, "uniqueUsers", uniqueUsers));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Obtener métricas completas de una historia
     */
    @GetMapping("/stories/{storyId}")
    public ResponseEntity<?> getStoryMetrics(@PathVariable Integer storyId) {
        try {
            Map<String, Object> metrics = metricsService.getStoryMetrics(storyId);
            return ResponseEntity.ok(metrics);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Obtener visitas recientes de una historia
     */
    @GetMapping("/stories/{storyId}/recent")
    public ResponseEntity<?> getRecentViews(
            @PathVariable Integer storyId,
            @RequestParam(defaultValue = "7") Integer days) {
        try {
            List<StoryView> views = metricsService.getRecentViews(storyId, days);
            return ResponseEntity.ok(Map.of("storyId", storyId, "days", days, "views", views));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Obtener historial de visitas del usuario autenticado
     */
    @GetMapping("/my-views")
    public ResponseEntity<?> getUserViewHistory(@RequestHeader("Authorization") String authHeader) {
        try {
            Integer userId = extractUserIdFromHeader(authHeader);
            List<StoryView> views = metricsService.getUserViewHistory(userId);
            return ResponseEntity.ok(views);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Obtener ranking de historias más vistas
     */
    @GetMapping("/top-stories")
    public ResponseEntity<?> getTopStories(@RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<Story> topStories = metricsService.getTopStoriesByViews(limit);
            return ResponseEntity.ok(Map.of("limit", limit, "stories", topStories));
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
}
