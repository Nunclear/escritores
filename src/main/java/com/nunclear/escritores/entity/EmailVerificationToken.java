package com.nunclear.escritores.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.nunclear.escritores.entity.BaseToken;
import java.time.LocalDateTime;

@Entity
@Table(name = "email_verification_token")
@Getter
@Setter
public class EmailVerificationToken extends BaseToken {

    /**
     * Timestamp when the email associated with this token was verified.  Once
     * this field is set, the token should be considered consumed.
     */
    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;
}