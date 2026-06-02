package com.nunclear.escritores.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.nunclear.escritores.entity.BaseToken;
import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_token")
@Getter
@Setter
public class PasswordResetToken extends BaseToken {

    /**
     * Timestamp when this reset token was used.  Once this field is
     * populated, the token should no longer be valid.
     */
    @Column(name = "used_at")
    private LocalDateTime usedAt;
}