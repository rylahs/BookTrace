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

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository; // DI
    private final JavaMailSender mailSender; // DI JavaMailSender

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Member signUp(SignUpForm signUpForm) {
        Member newMember = saveNewMember(signUpForm);
        newMember.generateToken(); // Generate Token()
        sendVerificationEmail(newMember);

        return newMember;
    }

    public Member saveNewMember(SignUpForm signUpForm) {
        Member member = Member.builder() // Account Create
                .email(signUpForm.getEmail())
                .password(passwordEncoder.encode(signUpForm.getPassword()))
                .nickname(signUpForm.getNickname())
                .build();

        Member newMember = memberRepository.save(member); // Account Entity Save
        return newMember;
    }

    public void sendVerificationEmail(Member newMember) {
        SimpleMailMessage mailMessage = new SimpleMailMessage(); // Create Email Object
        mailMessage.setTo(newMember.getEmail());
        mailMessage.setSubject("BookTrace 회원 가입 인증");
        mailMessage.setText(String.format("/check-email-token?token=%s&email=%s", newMember.getEmailToken(),
                newMember.getEmail())); // Email Contents
        mailSender.send(mailMessage); // Send Mail
    }

    public Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email); // AccountRepository에서 mail을 이용해 Account Entity를 가져옵니다.
    }

    public void login(Member member) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(new UserMember(member),
                member.getPassword(), Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));

        SecurityContextHolder.getContext().setAuthentication(token);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = Optional.ofNullable(memberRepository.findByEmail(username)) // (3)
                .orElse(memberRepository.findByNickname(username));
        if (member == null) { // (4)
            throw new UsernameNotFoundException(username);
        }
        return new UserMember(member); // (5)
    }
}

