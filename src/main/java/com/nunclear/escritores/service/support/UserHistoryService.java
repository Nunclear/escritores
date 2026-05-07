package com.nunclear.escritores.service.support;

import com.nunclear.escritores.entity.UserChangeHistory;
import com.nunclear.escritores.repository.UserChangeHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserHistoryService {

    private final UserChangeHistoryRepository userChangeHistoryRepository;

    public void saveHistory(
            Integer userId,
            String changedField,
            String oldValue,
            String newValue,
            Integer changedByUserId
    ) {
        UserChangeHistory history = new UserChangeHistory();
        history.setUserId(userId);
        history.setChangedField(changedField);
        history.setOldValue(oldValue);
        history.setNewValue(newValue);
        history.setChangedByUserId(changedByUserId);
        history.setChangedAt(LocalDateTime.now());

        userChangeHistoryRepository.save(history);
    }
}