package com.nunclear.escritores.util;

import com.nunclear.escritores.exception.BadRequestException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public final class PageUtils {

    private PageUtils() {
    }

    public static Pageable buildPageable(int page, int size, String sort, List<String> allowedFields, String defaultSort) {
        validatePage(page);
        validateSize(size);

        String finalSort = (sort == null || sort.isBlank()) ? defaultSort : sort;

        String[] sortParts = finalSort.split(",");
        String field = sortParts[0].trim();

        if (!allowedFields.contains(field)) {
            throw new BadRequestException("Campo de ordenamiento no permitido: " + field);
        }

        Sort.Direction direction = Sort.Direction.ASC;
        if (sortParts.length > 1) {
            try {
                direction = Sort.Direction.fromString(sortParts[1].trim());
            } catch (IllegalArgumentException ex) {
                throw new BadRequestException("Dirección de ordenamiento inválida: " + sortParts[1]);
            }
        }

        return PageRequest.of(page, size, Sort.by(direction, field));
    }

    public static void validatePage(int page) {
        if (page < 0) {
            throw new BadRequestException("page debe ser >= 0");
        }
    }

    public static void validateSize(int size) {
        if (size <= 0) {
            throw new BadRequestException("size debe ser > 0");
        }
    }

    public static List<String> sortAsList(String sort, String defaultSort) {
        return List.of((sort == null || sort.isBlank()) ? defaultSort : sort);
    }
}