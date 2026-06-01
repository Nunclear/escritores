package com.nunclear.escritores.validation;

import com.nunclear.escritores.dto.request.CreateGenreRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GenreValidationTest {

    @Autowired
    private Validator validator;

    @Test
    void crearGenre_conDatosValidos_pasaValidacion() {
        CreateGenreRequest request = new CreateGenreRequest(
                "Fantasía",
                "Historias de fantasía"
        );

        Set<ConstraintViolation<CreateGenreRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void crearGenre_sinNombre_fallasValidacion() {
        CreateGenreRequest request = new CreateGenreRequest(
                null,
                "Descripción"
        );

        Set<ConstraintViolation<CreateGenreRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void crearGenre_sinDescripcion_fallasValidacion() {
        CreateGenreRequest request = new CreateGenreRequest(
                "Fantasía",
                null
        );

        Set<ConstraintViolation<CreateGenreRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void crearGenre_conNombreVacio_fallasValidacion() {
        CreateGenreRequest request = new CreateGenreRequest(
                "",
                "Descripción"
        );

        Set<ConstraintViolation<CreateGenreRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void crearGenre_conDescripcionMuyLarga_debeRechazar() {
        String longDescription = "a".repeat(1001);
        CreateGenreRequest request = new CreateGenreRequest(
                "Fantasía",
                longDescription
        );

        Set<ConstraintViolation<CreateGenreRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }
}
