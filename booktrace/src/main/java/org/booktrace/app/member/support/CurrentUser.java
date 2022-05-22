package org.booktrace.app.member.support;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) // Runtime시 유지
@Target(ElementType.PARAMETER) // 파라미터에 사용
@AuthenticationPrincipal(expression = "#this == 'anonymousUser' ? null : member") //spring EL을 이용하여 인증정보가 존재하지 않으면 null / 존재하면 member property를 반환
public @interface CurrentUser {
}
