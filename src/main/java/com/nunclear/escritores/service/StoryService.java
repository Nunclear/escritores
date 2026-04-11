package com.nunclear.escritores.service;

import com.nunclear.escritores.dto.request.CreateStoryRequest;
import com.nunclear.escritores.dto.request.DuplicateStoryRequest;
import com.nunclear.escritores.dto.request.UpdateStoryRequest;
import com.nunclear.escritores.dto.response.*;
import com.nunclear.escritores.entity.AppUser;
import com.nunclear.escritores.entity.Story;
import com.nunclear.escritores.exception.BadRequestException;
import com.nunclear.escritores.exception.ResourceNotFoundException;
import com.nunclear.escritores.exception.UnauthorizedException;
import com.nunclear.escritores.repository.AppUserRepository;
import com.nunclear.escritores.repository.StoryRepository;
import com.nunclear.escritores.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class StoryService {

    private final StoryRepository storyRepository;
    private final AppUserRepository appUserRepository;

    public CreateStoryResponse createStory(CreateStoryRequest request) {
        AppUser currentUser = getAuthenticatedUser();

        validateVisibilityState(request.visibilityState());
        validatePublicationState(request.publicationState());

        Story story = new Story();
        story.setOwnerUserId(currentUser.getId());
        story.setTitle(request.title());
        story.setSlugText(generateUniqueSlug(request.title()));
        story.setDescription(request.description());
        story.setCoverImageUrl(request.coverImageUrl());
        story.setVisibilityState(request.visibilityState().toLowerCase());
        story.setPublicationState(request.publicationState().toLowerCase());
        story.setAllowFeedback(request.allowFeedback());
        story.setAllowScores(request.allowScores());
        story.setStartedOn(request.startedOn());

        if ("published".equalsIgnoreCase(request.publicationState())) {
            story.setPublishedAt(LocalDateTime.now());
        }

        Story saved = storyRepository.save(story);

        return new CreateStoryResponse(
                saved.getId(),
                saved.getOwnerUserId(),
                saved.getTitle(),
                saved.getSlugText(),
                saved.getVisibilityState(),
                saved.getPublicationState(),
                saved.getCreatedAt()
        );
    }

    public StoryDetailResponse getStoryById(Integer id) {
        Story story = storyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Historia no encontrada"));

        validateReadAccess(story);

        return new StoryDetailResponse(
                story.getId(),
                story.getOwnerUserId(),
                story.getTitle(),
                story.getSlugText(),
                story.getDescription(),
                story.getVisibilityState(),
                story.getPublicationState()
        );
    }

    public StorySlugResponse getStoryBySlug(String slug) {
        Story story = storyRepository.findBySlugText(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Historia no encontrada"));

        validateReadAccess(story);

        return new StorySlugResponse(
                story.getId(),
                story.getSlugText(),
                story.getTitle()
        );
    }

    public PageResponse<StoryListItemResponse> listPublicStories(int page, int size, String sort) {
        Pageable pageable = buildPageable(page, size, sort == null || sort.isBlank() ? "createdAt,desc" : sort);

        Page<Story> result = storyRepository.findByVisibilityStateIgnoreCaseAndPublicationStateIgnoreCaseAndArchivedAtIsNull(
                "public",
                "published",
                pageable
        );

        return new PageResponse<>(
                result.getContent().stream()
                        .map(story -> new StoryListItemResponse(
                                story.getId(),
                                story.getTitle(),
                                story.getSlugText(),
                                story.getVisibilityState(),
                                story.getPublicationState()
                        ))
                        .toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    public PageResponse<StoryListItemResponse> searchStories(
            String q,
            String visibilityState,
            String publicationState,
            int page,
            int size,
            String sort
    ) {
        String finalVisibility = visibilityState == null || visibilityState.isBlank() ? "public" : visibilityState;
        String finalPublication = publicationState == null || publicationState.isBlank() ? "published" : publicationState;

        if (!"public".equalsIgnoreCase(finalVisibility) || !"published".equalsIgnoreCase(finalPublication)) {
            throw new BadRequestException("Solo se permite búsqueda pública de historias publicadas");
        }

        Pageable pageable = buildPageable(page, size, sort == null || sort.isBlank() ? "createdAt,desc" : sort);

        Page<Story> result = storyRepository.searchPublicStories(
                q == null ? "" : q,
                finalVisibility,
                finalPublication,
                pageable
        );

        return new PageResponse<>(
                result.getContent().stream()
                        .map(story -> new StoryListItemResponse(
                                story.getId(),
                                story.getTitle(),
                                story.getSlugText(),
                                story.getVisibilityState(),
                                story.getPublicationState()
                        ))
                        .toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    public PageResponse<UserStorySummaryResponse> getStoriesByUser(
            Integer userId,
            boolean includeDrafts,
            int page,
            int size,
            String sort
    ) {
        Pageable pageable = buildPageable(page, size, sort == null || sort.isBlank() ? "createdAt,desc" : sort);

        Page<Story> result;
        if (canSeePrivateStories(userId) && includeDrafts) {
            result = storyRepository.findAllVisibleForOwner(userId, pageable);
        } else {
            result = storyRepository.findPublicPublishedByOwner(userId, pageable);
        }

        return new PageResponse<>(
                result.getContent().stream()
                        .map(story -> new UserStorySummaryResponse(
                                story.getId(),
                                story.getOwnerUserId(),
                                story.getTitle(),
                                story.getPublicationState()
                        ))
                        .toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    public PageResponse<UserStorySummaryResponse> getMyDrafts(int page, int size, String sort) {
        AppUser currentUser = getAuthenticatedUser();
        Pageable pageable = buildPageable(page, size, sort == null || sort.isBlank() ? "updatedAt,desc" : sort);

        Page<Story> result = storyRepository.findByOwnerUserIdAndPublicationStateIgnoreCaseAndArchivedAtIsNull(
                currentUser.getId(),
                "draft",
                pageable
        );

        return new PageResponse<>(
                result.getContent().stream()
                        .map(story -> new UserStorySummaryResponse(
                                story.getId(),
                                story.getOwnerUserId(),
                                story.getTitle(),
                                story.getPublicationState()
                        ))
                        .toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    public PageResponse<ArchivedStoryItemResponse> getMyArchived(int page, int size, String sort) {
        AppUser currentUser = getAuthenticatedUser();
        Pageable pageable = buildPageable(page, size, sort == null || sort.isBlank() ? "updatedAt,desc" : sort);

        Page<Story> result = storyRepository.findByOwnerUserIdAndArchivedAtIsNotNull(currentUser.getId(), pageable);

        return new PageResponse<>(
                result.getContent().stream()
                        .map(story -> new ArchivedStoryItemResponse(
                                story.getId(),
                                story.getTitle(),
                                story.getArchivedAt()
                        ))
                        .toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    public UpdateStoryResponse updateStory(Integer id, UpdateStoryRequest request) {
        Story story = getEditableStory(id);

        validateVisibilityState(request.visibilityState());

        story.setTitle(request.title());
        story.setSlugText(generateUniqueSlugForExisting(request.title(), story.getId()));
        story.setDescription(request.description());
        story.setCoverImageUrl(request.coverImageUrl());
        story.setVisibilityState(request.visibilityState().toLowerCase());
        story.setAllowFeedback(request.allowFeedback());
        story.setAllowScores(request.allowScores());

        Story saved = storyRepository.save(story);

        return new UpdateStoryResponse(
                saved.getId(),
                saved.getTitle(),
                saved.getUpdatedAt()
        );
    }

    public StoryPublicationResponse publishStory(Integer id) {
        Story story = getEditableStory(id);

        story.setPublicationState("published");
        story.setPublishedAt(LocalDateTime.now());

        Story saved = storyRepository.save(story);

        return new StoryPublicationResponse(
                saved.getId(),
                saved.getPublicationState(),
                saved.getPublishedAt()
        );
    }

    public StoryPublicationResponse unpublishStory(Integer id) {
        Story story = getEditableStory(id);

        story.setPublicationState("draft");
        story.setPublishedAt(null);

        Story saved = storyRepository.save(story);

        return new StoryPublicationResponse(
                saved.getId(),
                saved.getPublicationState(),
                saved.getPublishedAt()
        );
    }

    public StoryArchiveResponse archiveStory(Integer id) {
        Story story = getEditableStory(id);

        story.setArchivedAt(LocalDateTime.now());

        Story saved = storyRepository.save(story);

        return new StoryArchiveResponse(
                saved.getId(),
                saved.getArchivedAt()
        );
    }

    public StoryArchiveResponse restoreStory(Integer id) {
        Story story = getEditableStory(id);

        story.setArchivedAt(null);

        Story saved = storyRepository.save(story);

        return new StoryArchiveResponse(
                saved.getId(),
                saved.getArchivedAt()
        );
    }

    public DuplicateStoryResponse duplicateStory(Integer id, DuplicateStoryRequest request) {
        Story source = storyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Historia no encontrada"));

        validateReadAccess(source);

        AppUser currentUser = getAuthenticatedUser();

        Story duplicate = new Story();
        duplicate.setOwnerUserId(currentUser.getId());
        duplicate.setTitle(request.title());
        duplicate.setSlugText(generateUniqueSlug(request.title()));
        duplicate.setDescription(source.getDescription());
        duplicate.setCoverImageUrl(source.getCoverImageUrl());
        duplicate.setVisibilityState(source.getVisibilityState());
        duplicate.setPublicationState("draft");
        duplicate.setAllowFeedback(source.getAllowFeedback());
        duplicate.setAllowScores(source.getAllowScores());
        duplicate.setStartedOn(source.getStartedOn());
        duplicate.setPublishedAt(null);
        duplicate.setArchivedAt(null);

        Story saved = storyRepository.save(duplicate);

        return new DuplicateStoryResponse(
                saved.getId(),
                source.getId(),
                saved.getTitle(),
                saved.getPublicationState()
        );
    }

    public MessageResponse deleteStory(Integer id) {
        Story story = getEditableStory(id);
        storyRepository.delete(story);
        return new MessageResponse("Historia eliminada correctamente");
    }

    private Story getEditableStory(Integer id) {
        Story story = storyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Historia no encontrada"));

        AppUser currentUser = getAuthenticatedUser();
        boolean isOwner = story.getOwnerUserId().equals(currentUser.getId());
        boolean isModeratorOrAdmin =
                currentUser.getAccessLevel().name().equals("moderator")
                        || currentUser.getAccessLevel().name().equals("admin");

        if (!isOwner && !isModeratorOrAdmin) {
            throw new UnauthorizedException("No tienes permisos para modificar esta historia");
        }

        return story;
    }

    private void validateReadAccess(Story story) {
        boolean publicPublished =
                "public".equalsIgnoreCase(story.getVisibilityState())
                        && "published".equalsIgnoreCase(story.getPublicationState())
                        && story.getArchivedAt() == null;

        if (publicPublished) {
            return;
        }

        AppUser currentUser = tryGetAuthenticatedUser();
        if (currentUser == null) {
            throw new ResourceNotFoundException("Historia no encontrada");
        }

        boolean isOwner = story.getOwnerUserId().equals(currentUser.getId());
        boolean isModeratorOrAdmin =
                currentUser.getAccessLevel().name().equals("moderator")
                        || currentUser.getAccessLevel().name().equals("admin");

        if (!isOwner && !isModeratorOrAdmin) {
            throw new ResourceNotFoundException("Historia no encontrada");
        }
    }

    private boolean canSeePrivateStories(Integer userId) {
        AppUser currentUser = tryGetAuthenticatedUser();
        if (currentUser == null) {
            return false;
        }

        return currentUser.getId().equals(userId)
                || currentUser.getAccessLevel().name().equals("moderator")
                || currentUser.getAccessLevel().name().equals("admin");
    }

    private AppUser getAuthenticatedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!(principal instanceof CustomUserDetails userDetails)) {
            throw new UnauthorizedException("No autenticado");
        }

        return appUserRepository.findById(userDetails.getId())
                .orElseThrow(() -> new UnauthorizedException("Usuario no encontrado"));
    }

    private AppUser tryGetAuthenticatedUser() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof CustomUserDetails userDetails) {
                return appUserRepository.findById(userDetails.getId()).orElse(null);
            }
            return null;
        } catch (Exception ex) {
            return null;
        }
    }

    private void validateVisibilityState(String visibilityState) {
        if (!"public".equalsIgnoreCase(visibilityState) && !"private".equalsIgnoreCase(visibilityState)) {
            throw new BadRequestException("visibilityState inválido");
        }
    }

    private void validatePublicationState(String publicationState) {
        if (!"draft".equalsIgnoreCase(publicationState) && !"published".equalsIgnoreCase(publicationState)) {
            throw new BadRequestException("publicationState inválido");
        }
    }

    private Pageable buildPageable(int page, int size, String sort) {
        String[] sortParts = sort.split(",");
        String field = sortParts[0];
        Sort.Direction direction = sortParts.length > 1 && sortParts[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        return PageRequest.of(page, size, Sort.by(direction, mapSortField(field)));
    }

    private String mapSortField(String field) {
        return switch (field) {
            case "createdAt" -> "createdAt";
            case "updatedAt" -> "updatedAt";
            case "title" -> "title";
            case "publishedAt" -> "publishedAt";
            default -> "createdAt";
        };
    }

    private String generateUniqueSlug(String title) {
        String baseSlug = slugify(title);
        String finalSlug = baseSlug;
        int counter = 2;

        while (storyRepository.existsBySlugText(finalSlug)) {
            finalSlug = baseSlug + "-" + counter;
            counter++;
        }

        return finalSlug;
    }

    private String generateUniqueSlugForExisting(String title, Integer currentStoryId) {
        String baseSlug = slugify(title);
        String candidate = baseSlug;
        int counter = 2;

        while (true) {
            Story existing = storyRepository.findBySlugText(candidate).orElse(null);
            if (existing == null || existing.getId().equals(currentStoryId)) {
                return candidate;
            }
            candidate = baseSlug + "-" + counter;
            counter++;
        }
    }

    private String slugify(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        String withoutAccents = Pattern.compile("\\p{M}").matcher(normalized).replaceAll("");
        String slug = withoutAccents
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+|-+$", "");

        if (slug.isBlank()) {
            throw new BadRequestException("No se pudo generar slug para el título");
        }

        return slug;
    }
}