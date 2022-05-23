package org.booktrace.app.member.domain.entity;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * tokenRepository의 구현체에서 CREATE_TABLE_SQL을 보면 다음과 같다.
 * "create table persistent_logins
 *                              (username varchar(64) not null, series varchar(64) primary key, "
 *                              + "token varchar(64) not null, last_used timestamp not null)";
 *  이를 맞춰주기 위한 클래스를 생성한다.
 */

@Table(name = "persistent_logins")
@Entity
@Getter
public class PersistentLogins {
    @Id
    @Column(length = 64)
    private String series;

    @Column(length = 64)
    private String username;

    @Column(length = 64)
    private String token;

    @Column(name = "last_used", length = 64)
    private LocalDateTime lastUsed;

}
