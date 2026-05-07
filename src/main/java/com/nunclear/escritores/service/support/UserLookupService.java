package com.nunclear.escritores.service.support;

import com.nunclear.escritores.entity.AppUser;
import com.nunclear.escritores.exception.ResourceNotFoundException;
import com.nunclear.escritores.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserLookupService {

    private final AppUserRepository appUserRepository;

    public AppUser getActiveUser(Integer id) {
        AppUser user = appUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (user.getDeletedAt() != null) {
            throw new ResourceNotFoundException("Usuario no encontrado");
        }

        return user;
    }
}