package com.safeops.attachment;

import com.safeops.common.audit.Auditable;
import com.safeops.user.User;
import jakarta.persistence.*;

@Entity
@Table(name = "attachments", indexes = {
    @Index(name = "idx_attachments_entity_type_id", columnList = "entity_type, entity_id")
})
public class Attachment extends Auditable {

    @Column(name = "entity_type", nullable = false)
    private String entityType; // e.g. "PERMIT", "HAZARD", "INCIDENT"

    @Column(name = "entity_id", nullable = false)
    private String entityId; // UUID as string of the related entity

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_key", nullable = false) // S3 key
    private String fileKey;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "file_size")
    private Long fileSize;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by_user_id")
    private User uploadedBy;

    protected Attachment() {}

    public Attachment(String entityType, String entityId, String fileName, String fileKey, User uploadedBy) {
        this.entityType = entityType;
        this.entityId = entityId;
        this.fileName = fileName;
        this.fileKey = fileKey;
        this.uploadedBy = uploadedBy;
    }

    public String getEntityType() { return entityType; }
    public String getEntityId() { return entityId; }
    public String getFileName() { return fileName; }
    public String getFileKey() { return fileKey; }
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    public User getUploadedBy() { return uploadedBy; }
}
