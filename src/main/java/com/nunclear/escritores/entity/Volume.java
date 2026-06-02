package com.nunclear.escritores.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.nunclear.escritores.entity.Auditable;

@Entity
@Table(name = "volume")
@Getter
@Setter
public class Volume extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "story_id", nullable = false)
    private Integer storyId;

    @Column(name = "arc_id")
    private Integer arcId;

    @Column(name = "position_index")
    private Integer positionIndex;

    /**
     * Initializes audit timestamps before persisting this volume.
     */
    @PrePersist
    public void prePersist() {
        super.onCreate();
    }

    /**
     * Updates the {@code updatedAt} timestamp prior to updating this volume.
     */
    @PreUpdate
    public void preUpdate() {
        super.onUpdate();
    }

}