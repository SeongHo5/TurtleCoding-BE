package net.turtlecoding.damgo.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 모든 Entity의 Super Class로, 생성 / 수정 / 삭제 일시 등 공통 컬럼 관리를 자동화한다.
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @CreatedDate
    @Column(name="created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name="updated_at")
    private LocalDateTime updatedAt;

//    @Column(name="DELETED_DE", nullable = false)
//    private LocalDateTime deletedAt;

//    @LastModifiedBy
//    @Column(name="LAST_UPDUSR")
//    private String lastModifiedBy;
}
