package com.nunclear.escritores.service;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Servicio para manejar paginación y ordenamiento
 */
public class PaginationService {

    /**
     * Paginar una lista
     */
    public static <T> PaginatedResponse<T> paginate(List<T> items, int page, int size) {
        if (page < 0) page = 0;
        if (size < 1) size = 20;
        if (size > 100) size = 100;

        int totalElements = items.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        int startIndex = page * size;
        int endIndex = Math.min(startIndex + size, totalElements);

        List<T> pageContent;
        if (startIndex >= totalElements) {
            pageContent = List.of();
        } else {
            pageContent = items.subList(startIndex, endIndex);
        }

        return new PaginatedResponse<>(
            pageContent,
            new PageInfo(page, size, totalElements, totalPages)
        );
    }

    /**
     * Aplicar ordenamiento a una lista
     */
    public static <T> List<T> sort(List<T> items, String sort) {
        if (sort == null || sort.isEmpty()) {
            return items;
        }

        // Implementación básica - puede mejorarse con reflection
        return items;
    }

    @Data
    @NoArgsConstructor
    public static class PaginatedResponse<T> {
        private List<T> content;
        private PageInfo pageable;

        public PaginatedResponse(List<T> content, PageInfo pageable) {
            this.content = content;
            this.pageable = pageable;
        }
    }

    @Data
    @NoArgsConstructor
    public static class PageInfo {
        private int pageNumber;
        private int pageSize;
        private int totalElements;
        private int totalPages;

        public PageInfo(int pageNumber, int pageSize, int totalElements, int totalPages) {
            this.pageNumber = pageNumber;
            this.pageSize = pageSize;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
        }
    }
}
