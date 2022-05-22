package org.booktrace.app.member.domain.entity;

import lombok.*;
import org.booktrace.app.domain.entity.AuditingEntity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder @Getter @ToString
public class Member extends AuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String nickname;

    private String password;

    private boolean isValid;

    private String emailToken;

    private LocalDateTime emailTokenGeneratedAt; // 이메일 토큰 발급 시간 저장
    private LocalDateTime joinedDate;

    @Embedded
    private Profile profile;

    public void generateToken() { 
        this.emailToken = UUID.randomUUID().toString();
        this.emailTokenGeneratedAt = LocalDateTime.now(); // 토큰 발급 시간 업데이트
    }
    
    public void verified() { // 계정이 유효한지 알 수 있는 항목, 가입 시간은 현재시간
        this.isValid = true;
        joinedDate = LocalDateTime.now();
    }
    
    public boolean enableToSendEmail() {
        return this.emailTokenGeneratedAt.isBefore(LocalDateTime.now().minusMinutes(5)); // 5분 지났는지 체크
    }
}
