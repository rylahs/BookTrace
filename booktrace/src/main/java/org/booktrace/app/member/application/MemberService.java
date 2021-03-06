package org.booktrace.app.member.application;

import lombok.RequiredArgsConstructor;
import org.booktrace.app.config.AppProperties;
import org.booktrace.app.mail.EmailMessage;
import org.booktrace.app.mail.EmailService;
import org.booktrace.app.member.domain.UserMember;
import org.booktrace.app.member.domain.entity.Member;
import org.booktrace.app.member.endpoint.controller.dto.SignUpForm;
import org.booktrace.app.member.infra.repository.MemberRepository;
import org.booktrace.app.settings.controller.dto.MemberProfile;
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
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository; // DI

    private final EmailService emailService;

    private final PasswordEncoder passwordEncoder;

    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;
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
        Context context = new Context();
        context.setVariable("link", String.format("/check-email-token?token=%s&email=%s", newMember.getEmailToken(),
                newMember.getEmail()));
        context.setVariable("nickname", newMember.getNickname());
        context.setVariable("linkName", "????????? ??????");
        context.setVariable("message", "BookTrace ?????? ????????? ?????? ????????? ???????????????");
        context.setVariable("host", appProperties.getHost());
        String msg = templateEngine.process("mail/simple-link",context);
        emailService.sendEMail(EmailMessage.builder()
                .to(newMember.getEmail())
                .subject("BookTrace ?????? ?????? ?????? ??????")
                .message(msg)
                .build());
    }

    public Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email); // AccountRepository?????? mail??? ????????? Account Entity??? ???????????????.
    }

    public void login(Member member) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(new UserMember(member),
                member.getPassword(), Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));

        SecurityContextHolder.getContext().setAuthentication(token);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = Optional.ofNullable(memberRepository.findByEmail(username)) // (3)
                .orElse(memberRepository.findByNickname(username));
        if (member == null) { // (4)
            throw new UsernameNotFoundException(username);
        }
        return new UserMember(member); // (5)
    }

    public void verify(Member member) { // Controller ?????? ?????????
        member.verified();
        memberRepository.save(member);
        login(member);
    }

    public void updateProfile(Member member, MemberProfile memberProfile) {
        member.updateProfile(memberProfile);
        memberRepository.save(member); // DB Transaction
    }

    public void updatePassword(Member member, String newPassword) {
        member.updatePassword(passwordEncoder.encode(newPassword));
        memberRepository.save(member); // DB Transaction
    }

    public void updateNickname(Member member, String nickname) {
        member.updateNickname(nickname);
        memberRepository.save(member);
        login(member);
    }
}

