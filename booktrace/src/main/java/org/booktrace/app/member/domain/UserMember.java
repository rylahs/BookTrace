package org.booktrace.app.member.domain;

import lombok.Getter;
import org.booktrace.app.member.domain.entity.Member;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

public class UserMember extends User {
    @Getter
    private final Member member;

    public UserMember(Member member) {
        super(member.getNickname(), member.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_USER")));
        this.member = member;
    }

}
