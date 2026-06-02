package com.nunclear.escritores.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.nunclear.escritores.entity.Auditable;

@Entity
@Table(name = "media")
@Getter
@Setter
public class Media extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "filename", nullable = false, length = 255)
    private String filename;

    @Column(name = "original_filename", length = 255)
    private String originalFilename;

    @Column(name = "media_kind", nullable = false, length = 50)
    private String mediaKind;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "chapter_id", nullable = false)
    private Integer chapterId;

    @Column(name = "storage_path", length = 500)
    private String storagePath;

    /**
     * Initializes audit timestamps before persisting this media.
     */
    @PrePersist
    public void prePersist() {
        super.onCreate();
    }

    /**
     * Updates the {@code updatedAt} timestamp before updating this media.
     */
    @PreUpdate
    public void preUpdate() {
        super.onUpdate();
    }

}