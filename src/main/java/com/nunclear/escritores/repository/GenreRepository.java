package com.nunclear.escritores.repository;

import com.nunclear.escritores.entity.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GenreRepository extends JpaRepository<Genre, Integer> {

    Optional<Genre> findByName(String name);

    Optional<Genre> findBySlug(String slug);

    boolean existsByName(String name);

    boolean existsBySlug(String slug);

    Page<Genre> findAll(Pageable pageable);
}
