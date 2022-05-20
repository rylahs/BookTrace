package org.booktrace.member.domain.entity;

import lombok.*;
import org.booktrace.member.domain.support.ListStringConverter;

import javax.persistence.*;
import java.util.List;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder @Getter @ToString
public class Profile {
    private String bio;
    @Convert(converter = ListStringConverter.class)                                                 // (6)
    private List<String> url;
    private String job;
    private String location;
    private String company;
    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String image;
}
