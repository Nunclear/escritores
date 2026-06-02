package com.nunclear.escritores.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.nunclear.escritores.entity.Auditable;

@Entity
@Table(name = "item")
@Getter
@Setter
public class Item extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "unit_name", length = 50)
    private String unitName;

    @Column(name = "story_id", nullable = false)
    private Integer storyId;

    /**
     * Populates the audit timestamps before persisting.
     */
    @PrePersist
    public void prePersist() {
        super.onCreate();
    }

    /**
     * Updates the {@code updatedAt} timestamp before updating.
     */
    @PreUpdate
    public void preUpdate() {
        super.onUpdate();
    }

}