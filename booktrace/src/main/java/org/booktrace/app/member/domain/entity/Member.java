package org.booktrace.app.member.domain.entity;

import lombok.*;
import org.booktrace.app.domain.entity.AuditingEntity;
import org.booktrace.app.settings.controller.dto.MemberProfile;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null || Hibernate.getClass(this) != Hibernate.getClass(obj)) {
            return false;
        }

        // Object -> Account Casting
        Member member = (Member) obj;
        // id가 비어있지 않고 비교 객체와 object 객체가 equals인지 검증 // id가 같으면 동일 객체
        return id != null && Objects.equals(this.id, member.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
    @PostLoad
    private void init() {
        if (profile == null) {
            profile = new Profile();
        }
    }

    public void updateProfile(MemberProfile memberProfile) {
        if (this.profile == null) {
            this.profile = new Profile();
        }

        this.profile.setBio(memberProfile.getBio());
        this.profile.setUrl(memberProfile.getUrl());
        this.profile.setJob(memberProfile.getJob());
        this.profile.setFavorite(memberProfile.getFavorite());
        this.profile.setImage(memberProfile.getImage());

    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }
}
