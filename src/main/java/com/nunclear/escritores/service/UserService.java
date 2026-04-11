package com.nunclear.escritores.service;

import com.nunclear.escritores.dto.request.*;
import com.nunclear.escritores.dto.response.*;
import com.nunclear.escritores.entity.*;
import com.nunclear.escritores.enums.AccountState;
import com.nunclear.escritores.exception.BadRequestException;
import com.nunclear.escritores.exception.ResourceNotFoundException;
import com.nunclear.escritores.exception.UnauthorizedException;
import com.nunclear.escritores.repository.*;
import com.nunclear.escritores.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AppUserRepository appUserRepository;
    private final StoryRepository storyRepository;
    private final UserFollowRepository userFollowRepository;
    private final UserSessionRepository userSessionRepository;
    private final PasswordEncoder passwordEncoder;

    public UserProfileResponse getUserById(Integer id) {
        AppUser user = appUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        validatePublicReadable(user);

        return new UserProfileResponse(
                user.getId(),
                user.getLoginName(),
                user.getDisplayName(),
                user.getBioText(),
                user.getAvatarUrl(),
                user.getAccessLevel().name(),
                user.getCreatedAt()
        );
    }

    public CurrentUserResponse getMyProfile() {
        AppUser user = getAuthenticatedUser();

        return new CurrentUserResponse(
                user.getId(),
                user.getLoginName(),
                user.getEmailAddress(),
                user.getDisplayName(),
                user.getBioText(),
                user.getAvatarUrl(),
                user.getAccessLevel().name(),
                user.getAccountState().name()
        );
    }

    public PageResponse<UserListItemResponse> listUsers(int page, int size, String sort) {
        Pageable pageable = buildPageable(page, size, sort);
        Page<AppUser> result = appUserRepository.findAllActive(pageable);

        List<UserListItemResponse> content = result.getContent().stream()
                .map(user -> new UserListItemResponse(
                        user.getId(),
                        user.getLoginName(),
                        user.getDisplayName(),
                        user.getAccessLevel().name(),
                        user.getAccountState().name()
                ))
                .toList();

        return new PageResponse<>(
                content,
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    public PageResponse<UserSearchItemResponse> searchUsers(String q, int page, int size, String sort) {
        Pageable pageable = buildPageable(page, size, sort == null || sort.isBlank() ? "displayName,asc" : sort);
        Page<AppUser> result = appUserRepository.searchUsers(q, pageable);

        List<UserSearchItemResponse> content = result.getContent().stream()
                .map(user -> new UserSearchItemResponse(
                        user.getId(),
                        user.getLoginName(),
                        user.getDisplayName(),
                        user.getAvatarUrl()
                ))
                .toList();

        return new PageResponse<>(
                content,
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    public UpdateMyProfileResponse updateMyProfile(UpdateMyProfileRequest request) {
        AppUser user = getAuthenticatedUser();

        user.setDisplayName(request.displayName());
        user.setBioText(request.bioText());
        user.setAvatarUrl(request.avatarUrl());

        AppUser saved = appUserRepository.save(user);

        return new UpdateMyProfileResponse(
                saved.getId(),
                saved.getDisplayName(),
                saved.getBioText(),
                saved.getAvatarUrl(),
                saved.getUpdatedAt()
        );
    }

    public AvatarResponse changeAvatar(ChangeAvatarRequest request) {
        AppUser user = getAuthenticatedUser();
        user.setAvatarUrl(request.avatarUrl());
        AppUser saved = appUserRepository.save(user);

        return new AvatarResponse(saved.getAvatarUrl(), saved.getUpdatedAt());
    }

    public MessageResponse changePassword(ChangePasswordRequest request) {
        AppUser user = getAuthenticatedUser();

        if (!passwordEncoder.matches(request.oldPassword(), user.getPasswordHash())) {
            throw new BadRequestException("La contraseña actual no es correcta");
        }

        if (passwordEncoder.matches(request.newPassword(), user.getPasswordHash())) {
            throw new BadRequestException("La nueva contraseña no puede ser igual a la anterior");
        }

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        appUserRepository.save(user);

        for (UserSession session : userSessionRepository.findByUserIdAndRevokedAtIsNull(user.getId())) {
            session.setRevokedAt(LocalDateTime.now());
            userSessionRepository.save(session);
        }

        return new MessageResponse("Contraseña actualizada correctamente");
    }

    public ChangeEmailResponse changeEmail(ChangeEmailRequest request) {
        AppUser user = getAuthenticatedUser();

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadRequestException("La contraseña no es correcta");
        }

        if (appUserRepository.existsByEmailAddressIgnoreCase(request.newEmailAddress())) {
            throw new BadRequestException("El nuevo correo ya está en uso");
        }

        if (appUserRepository.existsByPendingEmailAddressIgnoreCase(request.newEmailAddress())) {
            throw new BadRequestException("Ese correo ya está pendiente de confirmación por otro usuario");
        }

        user.setPendingEmailAddress(request.newEmailAddress());
        user.setEmailChangeRequestedAt(LocalDateTime.now());
        appUserRepository.save(user);

        return new ChangeEmailResponse(
                "Cambio de correo solicitado",
                user.getPendingEmailAddress()
        );
    }

    public MessageResponse deactivateMyAccount() {
        AppUser user = getAuthenticatedUser();

        user.setAccountState(AccountState.banned);
        user.setDeletedAt(LocalDateTime.now());
        appUserRepository.save(user);

        for (UserSession session : userSessionRepository.findByUserIdAndRevokedAtIsNull(user.getId())) {
            session.setRevokedAt(LocalDateTime.now());
            userSessionRepository.save(session);
        }

        return new MessageResponse("Cuenta desactivada correctamente");
    }

    public PublicAuthorProfileResponse getPublicAuthorProfile(Integer id) {
        AppUser user = appUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        validatePublicReadable(user);

        long followersCount = userFollowRepository.countByFollowedUserId(id);
        long storiesCount = storyRepository.countByOwnerUserIdAndVisibilityStateIgnoreCaseAndPublicationStateIgnoreCase(
                id, "public", "published"
        );

        return new PublicAuthorProfileResponse(
                user.getId(),
                user.getDisplayName(),
                user.getBioText(),
                user.getAvatarUrl(),
                followersCount,
                storiesCount
        );
    }

    public PageResponse<UserStoryItemResponse> getPublicStoriesByAuthor(Integer id, int page, int size, String sort) {
        AppUser user = appUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        validatePublicReadable(user);

        Pageable pageable = buildPageable(page, size, sort == null || sort.isBlank() ? "createdAt,desc" : sort);

        Page<Story> result = storyRepository.findByOwnerUserIdAndVisibilityStateIgnoreCaseAndPublicationStateIgnoreCase(
                id, "public", "published", pageable
        );

        List<UserStoryItemResponse> content = result.getContent().stream()
                .map(story -> new UserStoryItemResponse(
                        story.getId(),
                        story.getTitle(),
                        story.getSlugText(),
                        story.getPublicationState(),
                        story.getVisibilityState()
                ))
                .toList();

        return new PageResponse<>(
                content,
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    private AppUser getAuthenticatedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!(principal instanceof CustomUserDetails userDetails)) {
            throw new UnauthorizedException("No autenticado");
        }

        return appUserRepository.findById(userDetails.getId())
                .orElseThrow(() -> new UnauthorizedException("Usuario no encontrado"));
    }

    private void validatePublicReadable(AppUser user) {
        if (user.getDeletedAt() != null) {
            throw new ResourceNotFoundException("Usuario no encontrado");
        }

        if (user.getAccountState() == AccountState.banned) {
            throw new ResourceNotFoundException("Usuario no encontrado");
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
            case "displayName" -> "displayName";
            case "loginName" -> "loginName";
            default -> "createdAt";
        };
    }
}