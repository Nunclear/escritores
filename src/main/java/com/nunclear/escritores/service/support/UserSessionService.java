package com.nunclear.escritores.service.support;

import com.nunclear.escritores.repository.UserSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserSessionService {

    private final UserSessionRepository userSessionRepository;

    public void revokeAllSessions(Integer userId) {
        var activeSessions = userSessionRepository.findByUserIdAndRevokedAtIsNull(userId);

        LocalDateTime now = LocalDateTime.now();
        activeSessions.forEach(session -> session.setRevokedAt(now));

        userSessionRepository.saveAll(activeSessions);
    }
}