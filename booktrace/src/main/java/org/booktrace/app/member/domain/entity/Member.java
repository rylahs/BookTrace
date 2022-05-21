package org.booktrace.app.member.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder @Getter @ToString
public class Member {

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

    @Embedded                                                                                           // (4)
    private Profile profile;

    public void generateToken() {
        this.emailToken = UUID.randomUUID().toString();
    }
}
