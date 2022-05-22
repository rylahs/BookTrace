package org.booktrace.app.member.application;

import lombok.RequiredArgsConstructor;
import org.booktrace.app.member.domain.UserMember;
import org.booktrace.app.member.domain.entity.Member;
import org.booktrace.app.member.endpoint.controller.SignUpForm;
import org.booktrace.app.member.infra.repository.MemberRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {
    private final MemberRepository memberRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder; // DI Password Encoder

    @Transactional // DB Save
    public Member signUp(SignUpForm signUpForm) {
        /** 회원 가입 로직 시작 */
        Member newMember = saveNewMember(signUpForm);

        newMember.generateToken(); //  이메일 인증용 토큰을 생성

        sendVerificationEmail(newMember);

        return newMember;
    }
    public Member saveNewMember(SignUpForm signUpForm) {
        Member member = Member.builder() // Entity를 생성
                .email(signUpForm.getEmail())
                .password(passwordEncoder.encode(signUpForm.getPassword())) // 비밀번호를 인코딩 후 저장
                .nickname(signUpForm.getNickname())
                .build();

        // Entity를 저장
        return memberRepository.save(member);
        //12341231231
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

    public Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email); // MemberRepository에서 email을 통해 Member Entity를 가져옴
    }

    /**
     * SecurityContextHolder.getContext()로 SecurityContext를 얻습니다.
     * 전역에서 호출할 수 있고 하나의 Context 객체가 반환됩니다.
     *
     * @param member
     */
    public void login(Member member) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(new UserMember(member),
                member.getPassword(), Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));

        SecurityContextHolder.getContext().setAuthentication(token);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException { // 재정의
        Member member = Optional.ofNullable(memberRepository.findByEmail(username)) // 이메일 or 닉네임
                .orElse(memberRepository.findByNickname(username));

        if (member == null) { // 검색 X
            throw new UsernameNotFoundException(username);
        }

        return new UserMember(member); // 계정이 존재할 경우 UserDetails의 구현체를 반환한다.
    }
}
