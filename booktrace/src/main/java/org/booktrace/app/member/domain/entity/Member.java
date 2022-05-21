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

    private LocalDateTime joinedDate;

    @Embedded                                                                                           // (4)
    private Profile profile;

    public void generateToken() {
        this.emailToken = UUID.randomUUID().toString();
    }

    public void verified() { // 계정이 유효한지 알 수 있는 항목, 가입 시간은 현재시간
        this.isValid = true;
        joinedDate = LocalDateTime.now();
    }
}
