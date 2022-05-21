package org.booktrace.member.application;

import lombok.RequiredArgsConstructor;
import org.booktrace.member.domain.entity.Member;
import org.booktrace.member.endpoint.controller.SignUpForm;
import org.booktrace.member.infra.repository.MemberRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final JavaMailSender mailSender;

    public Member saveNewMember(SignUpForm signUpForm) {
        Member member = Member.builder() // Entity를 생성
                .email(signUpForm.getEmail())
                .password(signUpForm.getPassword())
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
