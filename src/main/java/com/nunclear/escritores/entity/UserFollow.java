package com.nunclear.escritores.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_follow")
@SQLDelete(sql = "UPDATE user_follow SET deleted = true, deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
public class UserFollow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "follower_user_id", nullable = false)
    private Integer followerUserId;

    @Column(name = "followed_user_id", nullable = false)
    private Integer followedUserId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "deleted", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

}