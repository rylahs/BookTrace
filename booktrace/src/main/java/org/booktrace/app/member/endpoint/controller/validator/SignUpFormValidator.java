package org.booktrace.app.member.endpoint.controller.validator;

import lombok.RequiredArgsConstructor;
import org.booktrace.app.member.endpoint.controller.SignUpForm;
import org.booktrace.app.member.infra.repository.MemberRepository;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class SignUpFormValidator implements Validator { // Validator Interface

    private final MemberRepository memberRepository; // MemberRepository DI

    @Override
    public boolean supports(Class<?> clazz) { // SignUpForm 클래스 일때만 검증을 수행하기 위해 지원하는 타입을 지정해줍니다.
        return clazz.isAssignableFrom(SignUpForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) { // Validation
        SignUpForm signUpForm = (SignUpForm) target; // Casting

        if (memberRepository.existsByEmail(signUpForm.getEmail())) {
            errors.rejectValue("email", "invalid.email", new Object[]{signUpForm.getEmail()},
                    "이미 사용중인 이메일입니다.");
        }

        if (memberRepository.existsByNickname(signUpForm.getNickname())) {
            errors.rejectValue("nickname", "invalid.nickname", new Object[]{signUpForm.getNickname()},
                    "이미 사용중인 닉네임입니다.");
        }
    }
}
