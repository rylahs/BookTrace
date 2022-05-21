package org.booktrace.app.member.application;

import lombok.RequiredArgsConstructor;
import org.booktrace.app.member.domain.entity.Member;
import org.booktrace.app.member.endpoint.controller.SignUpForm;
import org.booktrace.app.member.infra.repository.MemberRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final JavaMailSender mailSender;

    private final PasswordEncoder passwordEncoder; // DI Password Encoder

    public void signUp(SignUpForm signUpForm) {
        /** 회원 가입 로직 시작 */
        Member newMember = saveNewMember(signUpForm);

        newMember.generateToken(); //  이메일 인증용 토큰을 생성

        sendVerificationEmail(newMember);
    }
    public Member saveNewMember(SignUpForm signUpForm) {
        Member member = Member.builder() // Entity를 생성
                .email(signUpForm.getEmail())
                .password(passwordEncoder.encode(signUpForm.getPassword())) // 비밀번호를 인코딩 후 저장
                .nickname(signUpForm.getNickname())
                .build();

        Member newMember = memberRepository.save(member); // Entity를 저장
        return newMember;
    }

    public void sendVerificationEmail(Member newMember) {
        SimpleMailMessage mailMessage = new SimpleMailMessage(); //  이메일을 객체를 생성

        // 이메일 내용 삽입
        mailMessage.setTo(newMember.getEmail());
        mailMessage.setSubject("BookTrace 회원 가입 인증");
        mailMessage.setText(String.format("/check-email-token?token=%s&email=%s",
                newMember.getEmailToken(), newMember.getEmail())); // 본문에 추가할 링크 주소

        mailSender.send(mailMessage); // 메일 보내기
    }
}
