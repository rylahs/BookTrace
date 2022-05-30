package org.booktrace.app.settings.controller.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.booktrace.app.member.domain.entity.Member;
import org.booktrace.app.member.domain.entity.Profile;
import org.hibernate.validator.constraints.Length;

import java.util.Optional;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberProfile {
    @Length(max = 40)
    private String bio;
    @Length(max = 50)
    private String url;
    @Length(max = 50)
    private String job;
    @Length(max = 50)
    private String favorite;

    private String image;
    public static MemberProfile from(Member member) {
        return new MemberProfile(member);
    }

    protected MemberProfile(Member member) {
        this.bio = Optional.ofNullable(member.getProfile()).map(Profile::getBio).orElse(null);
        this.url = Optional.ofNullable(member.getProfile()).map(Profile::getUrl).orElse(null);
        this.job = Optional.ofNullable(member.getProfile()).map(Profile::getJob).orElse(null);
        this.favorite = Optional.ofNullable(member.getProfile()).map(Profile::getFavorite).orElse(null);
        this.image = Optional.ofNullable(member.getProfile()).map(Profile::getImage).orElse(null);
    }



}
