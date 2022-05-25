package org.booktrace.app;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME) // 런타임시 동작
@WithSecurityContext(factory = WithMemberSecurityContextFactory.class) // SecurityContext를 설정해줄 클래스
public @interface PreAccount {
    String value(); // 하나의 값을 attribute로 전달 받기 위한 메소드
}
