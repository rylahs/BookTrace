package org.booktrace.app.domain.entity;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class) // 시간에 대해서 JPA가 자동으로 값을 넣어줌
@MappedSuperclass // JPA Entity 클래스들이 해당 추상 클래스를 상속할 경우 createDate, modifiedDate를 컬럼으로 인식
@Getter
public class AuditingEntity {

    // Entity가 생성되어 저장될 때 시간이 자동으로 저장됩니다.
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    // 조회한 Entity 값을 변경할 때 시간이 자동 저장 됩니다.
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

}
