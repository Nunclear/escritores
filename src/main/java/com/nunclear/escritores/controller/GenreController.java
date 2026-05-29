package com.nunclear.escritores.controller;

import com.nunclear.escritores.dto.request.AssignGenresToStoryRequest;
import com.nunclear.escritores.dto.request.CreateGenreRequest;
import com.nunclear.escritores.dto.response.GenreResponse;
import com.nunclear.escritores.entity.Genre;
import com.nunclear.escritores.service.GenreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public GenreResponse createGenre(@Valid @RequestBody CreateGenreRequest request) {
        Genre genre = genreService.createGenre(request.name(), request.description(), request.iconUrl());
        return mapToResponse(genre);
    }

    @GetMapping
    public List<GenreResponse> getAllGenres(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        Page<Genre> genres = genreService.getAllGenres(PageRequest.of(page, size));
        return genres.getContent().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public GenreResponse getGenreById(@PathVariable Integer id) {
        Genre genre = genreService.getGenreById(id);
        return mapToResponse(genre);
    }

    @GetMapping("/slug/{slug}")
    public GenreResponse getGenreBySlug(@PathVariable String slug) {
        Genre genre = genreService.getGenreBySlug(slug);
        return mapToResponse(genre);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public GenreResponse updateGenre(
            @PathVariable Integer id,
            @Valid @RequestBody CreateGenreRequest request
    ) {
        Genre genre = genreService.updateGenre(id, request.name(), request.description(), request.iconUrl());
        return mapToResponse(genre);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteGenre(@PathVariable Integer id) {
        genreService.deleteGenre(id);
    }

    @PostMapping("/stories/{storyId}/genres")
    @PreAuthorize("hasAnyRole('USER','MODERATOR','ADMIN')")
    public void assignGenresToStory(
            @PathVariable Integer storyId,
            @Valid @RequestBody AssignGenresToStoryRequest request
    ) {
        genreService.assignGenresToStory(storyId, request.genreIds());
    }

    private GenreResponse mapToResponse(Genre genre) {
        return new GenreResponse(
                genre.getId(),
                genre.getName(),
                genre.getSlug(),
                genre.getDescription(),
                genre.getIconUrl(),
                genre.getCreatedAt(),
                genre.getUpdatedAt()
        );
    }
}
