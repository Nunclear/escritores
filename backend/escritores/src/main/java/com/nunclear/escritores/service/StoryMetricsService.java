package com.nunclear.escritores.service;

import com.nunclear.escritores.entity.AppUser;
import com.nunclear.escritores.entity.Story;
import com.nunclear.escritores.entity.StoryView;
import com.nunclear.escritores.repository.AppUserRepository;
import com.nunclear.escritores.repository.StoryRepository;
import com.nunclear.escritores.repository.StoryViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StoryMetricsService {
    private final StoryViewRepository viewRepository;
    private final StoryRepository storyRepository;
    private final AppUserRepository userRepository;

    /**
     * Registrar una visita a una historia
     */
    @Transactional
    public StoryView recordView(Integer storyId, Integer userId, String ipAddress, String userAgent) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new RuntimeException("Historia no encontrada"));

        AppUser user = null;
        if (userId != null) {
            user = userRepository.findById(userId).orElse(null);
        }

        StoryView view = StoryView.builder()
                .story(story)
                .user(user)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .createdAt(LocalDateTime.now())
                .build();

        return viewRepository.save(view);
    }

    /**
     * Obtener total de visitas de una historia
     */
    public long getTotalViews(Integer storyId) {
        return viewRepository.countViewsByStoryId(storyId);
    }

    /**
     * Obtener únicos usuarios que visitaron una historia
     */
    public long getUniqueUserViews(Integer storyId) {
        return viewRepository.countUniqueUsersByStoryId(storyId);
    }

    /**
     * Obtener métricas completas de una historia
     */
    public Map<String, Object> getStoryMetrics(Integer storyId) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new RuntimeException("Historia no encontrada"));

        long totalViews = getTotalViews(storyId);
        long uniqueUsers = getUniqueUserViews(storyId);

        Map<String, Object> metrics = new HashMap<>();
        metrics.put("storyId", storyId);
        metrics.put("totalViews", totalViews);
        metrics.put("uniqueUsers", uniqueUsers);
        metrics.put("title", story.getTitle());
        metrics.put("author", story.getOwnerUser().getUsername());

        return metrics;
    }

    /**
     * Obtener visitas recientes de una historia
     */
    public List<StoryView> getRecentViews(Integer storyId, Integer days) {
        LocalDateTime threshold = LocalDateTime.now().minusDays(days);
        return viewRepository.findByStoryIdAndCreatedAtAfter(storyId, threshold);
    }

    /**
     * Obtener historial de visitas de un usuario
     */
    public List<StoryView> getUserViewHistory(Integer userId) {
        return viewRepository.findByUserId(userId);
    }

    /**
     * Obtener ranking de historias más vistas
     */
    public List<Story> getTopStoriesByViews(int limit) {
        List<Story> stories = storyRepository.findAll();
        return stories.stream()
                .sorted((a, b) -> Long.compare(
                    viewRepository.countViewsByStoryId(b.getId()),
                    viewRepository.countViewsByStoryId(a.getId())
                ))
                .limit(limit)
                .toList();
    }
}
