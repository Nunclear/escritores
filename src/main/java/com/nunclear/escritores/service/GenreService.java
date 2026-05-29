package com.nunclear.escritores.service;

import com.nunclear.escritores.entity.Genre;
import com.nunclear.escritores.exception.BadRequestException;
import com.nunclear.escritores.exception.ResourceNotFoundException;
import com.nunclear.escritores.repository.GenreRepository;
import com.nunclear.escritores.repository.StoryGenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class GenreService {

    private static final String GENRE_NOT_FOUND = "Género no encontrado";
    private static final String GENRE_ALREADY_EXISTS = "Este género ya existe";

    private final GenreRepository genreRepository;
    private final StoryGenreRepository storyGenreRepository;

    public Genre createGenre(String name, String description, String iconUrl) {
        if (genreRepository.existsByName(name)) {
            throw new BadRequestException(GENRE_ALREADY_EXISTS);
        }

        Genre genre = new Genre();
        genre.setName(name);
        genre.setSlug(generateSlug(name));
        genre.setDescription(description);
        genre.setIconUrl(iconUrl);

        return genreRepository.save(genre);
    }

    public Genre getGenreById(Integer id) {
        return genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(GENRE_NOT_FOUND));
    }

    public Genre getGenreByName(String name) {
        return genreRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException(GENRE_NOT_FOUND));
    }

    public Genre getGenreBySlug(String slug) {
        return genreRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException(GENRE_NOT_FOUND));
    }

    public Page<Genre> getAllGenres(Pageable pageable) {
        return genreRepository.findAll(pageable);
    }

    public Genre updateGenre(Integer id, String name, String description, String iconUrl) {
        Genre genre = getGenreById(id);

        if (name != null && !name.equals(genre.getName()) && genreRepository.existsByName(name)) {
            throw new BadRequestException(GENRE_ALREADY_EXISTS);
        }

        if (name != null) {
            genre.setName(name);
            genre.setSlug(generateSlug(name));
        }
        if (description != null) {
            genre.setDescription(description);
        }
        if (iconUrl != null) {
            genre.setIconUrl(iconUrl);
        }

        return genreRepository.save(genre);
    }

    public void deleteGenre(Integer id) {
        Genre genre = getGenreById(id);
        storyGenreRepository.deleteByGenreId(id);
        genreRepository.delete(genre);
    }

    public void assignGenresToStory(Integer storyId, List<Integer> genreIds) {
        storyGenreRepository.deleteByStoryId(storyId);

        for (Integer genreId : genreIds) {
            if (!genreRepository.existsById(genreId)) {
                throw new ResourceNotFoundException(GENRE_NOT_FOUND);
            }
        }

        genreIds.forEach(genreId -> {
            com.nunclear.escritores.entity.StoryGenre storyGenre = new com.nunclear.escritores.entity.StoryGenre();
            storyGenre.setStoryId(storyId);
            storyGenre.setGenreId(genreId);
            storyGenreRepository.save(storyGenre);
        });
    }

    public List<Genre> getGenresByStoryId(Integer storyId) {
        List<com.nunclear.escritores.entity.StoryGenre> storyGenres = storyGenreRepository.findByStoryId(storyId);
        return storyGenres.stream()
                .map(sg -> getGenreById(sg.getGenreId()))
                .toList();
    }

    private String generateSlug(String name) {
        String normalized = Normalizer.normalize(name, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String slug = pattern.matcher(normalized).replaceAll("");
        return slug.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
    }
}
