package com.myrealtrip.ohmyhotel.core.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.NotAudited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public abstract class BaseEntity {

    @Column(name = "created_by", updatable = false)
    @CreatedBy
    @NotAudited
    private String createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    @NotAudited
    private LocalDateTime createdAt;

    @Column(name = "updated_by")
    @LastModifiedBy
    @NotAudited
    private String updatedBy;

    @Column(name = "updated_at", nullable = false)
    @LastModifiedDate
    @NotAudited
    private LocalDateTime updatedAt;

    @Column(name = "deleted_by")
    @NotAudited
    private String deletedBy;

    @Column(name = "deleted_at")
    @NotAudited
    private LocalDateTime deletedAt;

    public boolean isNotDeleted() {
        return this.deletedAt == null;
    }

    private void recordDeletedAt() {
        if (this.deletedAt == null) {
            this.deletedAt = LocalDateTime.now();
        }
    }
}
