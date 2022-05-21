package org.booktrace.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Member Class가 @MappedSuperClass가 적용된 AuditingEntity 추상 클래스를 상속하기 때문에 JPA가 생성일자, 수정일자를 인식
 * 영속성 컨텍스트에 저장하고 AuditingEntity클래스의 Auditing 기능으로 Transaction Commit 시점에 flush()가 호출 할 때
 * Hibernate가 자동으로 시간 값을 채워줍니다.
 * 이 기능을 사용하기 위해서 실행 클래스에 @EnableJpaAuditing 활성화 해줘야 됩니다.
 */
@SpringBootApplication
@EnableJpaAuditing
public class BooktraceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BooktraceApplication.class, args);
	}

}
