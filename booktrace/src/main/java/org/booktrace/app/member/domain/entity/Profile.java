package org.booktrace.app.member.domain.entity;

import lombok.*;
import org.booktrace.app.member.domain.support.ListStringConverter;

import javax.persistence.*;
import java.util.List;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder @Getter
public class Profile {
    private String bio;
    private String url;
    private String job;
    private String location;
    private String company;
    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String image;
}