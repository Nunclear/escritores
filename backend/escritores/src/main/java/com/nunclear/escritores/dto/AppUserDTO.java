package com.nunclear.escritores.dto;

import com.nunclear.escritores.entity.Role;
import com.nunclear.escritores.entity.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppUserDTO {
    private Integer id;
    private String username;
    private String email;
    private String displayName;
    private String bio;
    private String avatarUrl;
    private Role role;
    private UserStatus status;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
