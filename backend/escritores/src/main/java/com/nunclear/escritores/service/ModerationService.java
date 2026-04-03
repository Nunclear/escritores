package com.nunclear.escritores.service;

import com.nunclear.escritores.entity.*;
import com.nunclear.escritores.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ModerationService {

    private final StoryCommentRepository commentRepository;
    private final ContentReportRepository reportRepository;
    private final UserSanctionRepository sanctionRepository;
    private final AppUserRepository userRepository;
    private final StoryRepository storyRepository;
    private final ChapterRepository chapterRepository;

    /**
     * Ocultar comentario
     */
    public void hideComment(Integer commentId, String reason) {
        StoryComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado"));

        comment.setVisibility(Visibility.HIDDEN);
        comment.setHideReason(reason);
        comment.setHiddenAt(LocalDateTime.now());
        commentRepository.save(comment);
    }

    /**
     * Restaurar comentario
     */
    public void restoreComment(Integer commentId) {
        StoryComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado"));

        comment.setVisibility(Visibility.VISIBLE);
        comment.setHideReason(null);
        comment.setHiddenAt(null);
        commentRepository.save(comment);
    }

    /**
     * Marcar comentario como revisado
     */
    public void markCommentAsReviewed(Integer commentId) {
        StoryComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado"));

        comment.setReviewedAt(LocalDateTime.now());
        commentRepository.save(comment);
    }

    /**
     * Listar comentarios ocultos
     */
    public List<StoryComment> getHiddenComments() {
        return commentRepository.findAll().stream()
                .filter(c -> c.getVisibility() == Visibility.HIDDEN)
                .toList();
    }

    /**
     * Crear reporte de contenido
     */
    public ContentReport createReport(Integer reporterId, Integer storyId, Integer chapterId, Integer commentId, String reason) {
        AppUser reporter = userRepository.findById(reporterId)
                .orElseThrow(() -> new RuntimeException("Usuario reportero no encontrado"));

        ContentReport report = ContentReport.builder()
                .reporterUser(reporter)
                .story(storyId != null ? storyRepository.findById(storyId).orElse(null) : null)
                .chapter(chapterId != null ? chapterRepository.findById(chapterId).orElse(null) : null)
                .comment(commentId != null ? commentRepository.findById(commentId).orElse(null) : null)
                .reasonText(reason)
                .status(ReportStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        return reportRepository.save(report);
    }

    /**
     * Listar reportes pendientes
     */
    public List<ContentReport> getPendingReports() {
        return reportRepository.findAll().stream()
                .filter(r -> r.getStatus() == ReportStatus.PENDING)
                .toList();
    }

    /**
     * Listar reportes por estado
     */
    public List<ContentReport> getReportsByStatus(ReportStatus status) {
        return reportRepository.findAll().stream()
                .filter(r -> r.getStatus() == status)
                .toList();
    }

    /**
     * Resolver reporte
     */
    public void resolveReport(Integer reportId, String resolution, Integer moderatorId) {
        ContentReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado"));

        AppUser moderator = userRepository.findById(moderatorId)
                .orElseThrow(() -> new RuntimeException("Moderador no encontrado"));

        report.setStatus(ReportStatus.RESOLVED);
        report.setResolution(resolution);
        report.setResolvedByMod(moderator);
        report.setResolvedAt(LocalDateTime.now());
        reportRepository.save(report);
    }

    /**
     * Rechazar reporte
     */
    public void rejectReport(Integer reportId, String reason, Integer moderatorId) {
        ContentReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado"));

        AppUser moderator = userRepository.findById(moderatorId)
                .orElseThrow(() -> new RuntimeException("Moderador no encontrado"));

        report.setStatus(ReportStatus.REJECTED);
        report.setResolution(reason);
        report.setResolvedByMod(moderator);
        report.setResolvedAt(LocalDateTime.now());
        reportRepository.save(report);
    }

    /**
     * Crear advertencia a usuario
     */
    public UserSanction createWarning(Integer userId, String reason) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        UserSanction sanction = UserSanction.builder()
                .user(user)
                .type(SanctionType.WARNING)
                .reason(reason)
                .status(SanctionStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        return sanctionRepository.save(sanction);
    }

    /**
     * Aplicar baneo temporal
     */
    public UserSanction applyTemporaryBan(Integer userId, String reason, int durationDays) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        LocalDateTime expiresAt = LocalDateTime.now().plusDays(durationDays);

        UserSanction sanction = UserSanction.builder()
                .user(user)
                .type(SanctionType.TEMP_BAN)
                .reason(reason)
                .status(SanctionStatus.ACTIVE)
                .expiresAt(expiresAt)
                .createdAt(LocalDateTime.now())
                .build();

        UserSanction saved = sanctionRepository.save(sanction);

        // Cambiar estado del usuario a SUSPENDED
        user.setStatus(UserStatus.SUSPENDED);
        userRepository.save(user);

        return saved;
    }

    /**
     * Aplicar baneo permanente
     */
    public UserSanction applyPermanentBan(Integer userId, String reason) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        UserSanction sanction = UserSanction.builder()
                .user(user)
                .type(SanctionType.PERMANENT_BAN)
                .reason(reason)
                .status(SanctionStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        UserSanction saved = sanctionRepository.save(sanction);

        // Cambiar estado del usuario a BANNED
        user.setStatus(UserStatus.BANNED);
        userRepository.save(user);

        return saved;
    }

    /**
     * Levantar sanción
     */
    public void removeSanction(Integer sanctionId) {
        UserSanction sanction = sanctionRepository.findById(sanctionId)
                .orElseThrow(() -> new RuntimeException("Sanción no encontrada"));

        sanction.setStatus(SanctionStatus.LIFTED);
        sanction.setLiftedAt(LocalDateTime.now());
        sanctionRepository.save(sanction);

        // Cambiar estado del usuario a ACTIVE si no tiene otras sanciones activas
        Integer userId = sanction.getUser().getId();
        List<UserSanction> activeSanctions = sanctionRepository.findAll().stream()
                .filter(s -> s.getUser().getId().equals(userId) && s.getStatus() == SanctionStatus.ACTIVE)
                .toList();

        if (activeSanctions.isEmpty()) {
            AppUser user = userRepository.findById(userId).orElseThrow();
            user.setStatus(UserStatus.ACTIVE);
            userRepository.save(user);
        }
    }

    /**
     * Listar sanciones activas de un usuario
     */
    public List<UserSanction> getActiveSanctionsForUser(Integer userId) {
        return sanctionRepository.findAll().stream()
                .filter(s -> s.getUser().getId().equals(userId) && s.getStatus() == SanctionStatus.ACTIVE)
                .toList();
    }

    /**
     * Verificar si usuario está sancionado
     */
    public boolean isUserSanctioned(Integer userId) {
        return !getActiveSanctionsForUser(userId).isEmpty();
    }

    /**
     * Listar todas las sanciones activas
     */
    public List<UserSanction> getAllActiveSanctions() {
        return sanctionRepository.findAll().stream()
                .filter(s -> s.getStatus() == SanctionStatus.ACTIVE)
                .toList();
    }

    /**
     * Limpiar baneos temporales expirados
     */
    @Transactional
    public void cleanupExpiredSanctions() {
        LocalDateTime now = LocalDateTime.now();

        List<UserSanction> expiredSanctions = sanctionRepository.findAll().stream()
                .filter(s -> s.getStatus() == SanctionStatus.ACTIVE &&
                           s.getExpiresAt() != null &&
                           s.getExpiresAt().isBefore(now))
                .toList();

        for (UserSanction sanction : expiredSanctions) {
            removeSanction(sanction.getId());
        }
    }
}
