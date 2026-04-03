package com.nunclear.escritores.service;

import com.nunclear.escritores.entity.AppUser;
import com.nunclear.escritores.entity.GlobalNotice;
import com.nunclear.escritores.entity.Role;
import com.nunclear.escritores.repository.AppUserRepository;
import com.nunclear.escritores.repository.GlobalNoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GlobalNoticeService {
    private final GlobalNoticeRepository noticeRepository;
    private final AppUserRepository userRepository;

    /**
     * Crear un comunicado global
     */
    @Transactional
    public GlobalNotice createNotice(Integer creatorId, String title, String content, 
                                     LocalDateTime scheduledAt) {
        AppUser creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (creator.getRole() != Role.ADMINISTRADOR) {
            throw new RuntimeException("Solo administradores pueden crear comunicados");
        }

        GlobalNotice notice = GlobalNotice.builder()
                .title(title)
                .content(content)
                .createdByUser(creator)
                .isEnabled(true)
                .scheduledAt(scheduledAt)
                .createdAt(LocalDateTime.now())
                .build();

        return noticeRepository.save(notice);
    }

    /**
     * Actualizar un comunicado global
     */
    @Transactional
    public GlobalNotice updateNotice(Integer noticeId, String title, String content, 
                                     LocalDateTime scheduledAt) {
        GlobalNotice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new RuntimeException("Comunicado no encontrado"));

        if (title != null) {
            notice.setTitle(title);
        }
        if (content != null) {
            notice.setContent(content);
        }
        if (scheduledAt != null) {
            notice.setScheduledAt(scheduledAt);
        }
        notice.setUpdatedAt(LocalDateTime.now());

        return noticeRepository.save(notice);
    }

    /**
     * Activar un comunicado
     */
    @Transactional
    public void enableNotice(Integer noticeId) {
        GlobalNotice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new RuntimeException("Comunicado no encontrado"));
        notice.setIsEnabled(true);
        notice.setUpdatedAt(LocalDateTime.now());
        noticeRepository.save(notice);
    }

    /**
     * Desactivar un comunicado
     */
    @Transactional
    public void disableNotice(Integer noticeId) {
        GlobalNotice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new RuntimeException("Comunicado no encontrado"));
        notice.setIsEnabled(false);
        notice.setUpdatedAt(LocalDateTime.now());
        noticeRepository.save(notice);
    }

    /**
     * Obtener comunicado por ID
     */
    public GlobalNotice getNotice(Integer noticeId) {
        return noticeRepository.findById(noticeId)
                .orElseThrow(() -> new RuntimeException("Comunicado no encontrado"));
    }

    /**
     * Listar todos los comunicados activos
     */
    public List<GlobalNotice> getActiveNotices() {
        return noticeRepository.findAll().stream()
                .filter(GlobalNotice::getIsEnabled)
                .filter(n -> n.getScheduledAt() == null || n.getScheduledAt().isBefore(LocalDateTime.now()))
                .toList();
    }

    /**
     * Listar todos los comunicados (admin)
     */
    public List<GlobalNotice> getAllNotices() {
        return noticeRepository.findAll();
    }

    /**
     * Eliminar un comunicado
     */
    @Transactional
    public void deleteNotice(Integer noticeId) {
        GlobalNotice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new RuntimeException("Comunicado no encontrado"));
        noticeRepository.delete(notice);
    }
}
