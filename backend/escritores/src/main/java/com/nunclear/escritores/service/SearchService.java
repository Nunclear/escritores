package com.nunclear.escritores.service;

import com.nunclear.escritores.entity.Story;
import com.nunclear.escritores.entity.StoryCharacter;
import com.nunclear.escritores.entity.Chapter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import com.nunclear.escritores.repository.StoryRepository;
import com.nunclear.escritores.repository.StoryCharacterRepository;
import com.nunclear.escritores.repository.ChapterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final StoryRepository storyRepository;
    private final StoryCharacterRepository characterRepository;
    private final ChapterRepository chapterRepository;

    /**
     * Búsqueda global de historias por título
     */
    public Page<Story> searchStoriesByTitle(String title, Pageable pageable) {
        List<Story> filtered = storyRepository.findAll().stream()
                .filter(s -> s.getTitle() != null &&
                        s.getTitle().toLowerCase().contains(title.toLowerCase()))
                .toList();

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filtered.size());

        List<Story> content = start >= filtered.size()
                ? List.of()
                : filtered.subList(start, end);

        return new PageImpl<>(content, pageable, filtered.size());
    }

    /**
     * Búsqueda de historias por autor
     */
    public List<Story> searchStoriesByAuthor(String authorName) {
        return storyRepository.findAll().stream()
                .filter(s -> s.getOwnerUser().getUsername().toLowerCase()
                        .contains(authorName.toLowerCase()))
                .toList();
    }

    /**
     * Búsqueda de historias por estado de publicación
     */
    public List<Story> searchStoriesByPublicationState(String publicationState) {
        return storyRepository.findAll().stream()
                .filter(s -> s.getPublicationState().equalsIgnoreCase(publicationState))
                .toList();
    }

    /**
     * Búsqueda de personajes por nombre
     */
    public List<StoryCharacter> searchCharactersByName(String name) {
        return characterRepository.findAll().stream()
                .filter(c -> c.getName().toLowerCase().contains(name.toLowerCase()))
                .toList();
    }

    /**
     * Búsqueda de personajes por historia
     */
    public List<StoryCharacter> searchCharactersByStory(Integer storyId) {
        return characterRepository.findByStoryId(storyId);
    }

    /**
     * Búsqueda de capítulos por título
     */
    public List<Chapter> searchChaptersByTitle(String title) {
        return chapterRepository.findAll().stream()
                .filter(ch -> ch.getTitle().toLowerCase().contains(title.toLowerCase()))
                .toList();
    }

    /**
     * Búsqueda de capítulos por historia
     */
    public List<Chapter> searchChaptersByStory(Integer storyId) {
        return chapterRepository.findByStoryId(storyId);
    }

    /**
     * Búsqueda avanzada de historias con múltiples criterios
     */
    public List<Story> advancedStorySearch(String title, String authorName, String publicationState) {
        return storyRepository.findAll().stream()
                .filter(s -> title == null || s.getTitle().toLowerCase().contains(title.toLowerCase()))
                .filter(s -> authorName == null || s.getOwnerUser().getUsername().toLowerCase()
                        .contains(authorName.toLowerCase()))
                .filter(s -> publicationState == null || s.getPublicationState()
                        .equalsIgnoreCase(publicationState))
                .toList();
    }

    /**
     * Búsqueda global que retorna resultados de múltiples tipos
     */
    public Map<String, Object> globalSearch(String query) {
        Map<String, Object> results = new HashMap<>();
        
        results.put("stories", storyRepository.findAll().stream()
                .filter(s -> s.getTitle().toLowerCase().contains(query.toLowerCase()))
                .limit(10)
                .toList());
        
        results.put("characters", characterRepository.findByNameContaining(query).stream()
                .limit(10)
                .toList());
        
        results.put("chapters", chapterRepository.findAll().stream()
                .filter(ch -> ch.getTitle().toLowerCase().contains(query.toLowerCase()))
                .limit(10)
                .toList());
        
        return results;
    }
}
