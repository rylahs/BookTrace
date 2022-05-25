package org.booktrace.app;

import org.booktrace.app.member.application.MemberService;
import org.booktrace.app.member.endpoint.controller.SignUpForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithSecurityContextFactory;


public class WithMemberSecurityContextFactory implements WithSecurityContextFactory<PreAccount> {

    private final MemberService memberService;

    public WithMemberSecurityContextFactory(MemberService memberService) {
        this.memberService = memberService;
    }

    @Override
    public SecurityContext createSecurityContext(PreAccount annotation) {
        String nickName = annotation.value();

        joinMemberTest(nickName);

        SecurityContext context = getSecurityContext(nickName);

        return context;
    }

    private void joinMemberTest(String nickName) {
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setNickname(nickName);
        signUpForm.setEmail(nickName + "@gmail.com");
        signUpForm.setPassword("12341234");
        memberService.signUp(signUpForm);
    }

    private SecurityContext getSecurityContext(String nickName) {
        UserDetails principal = memberService.loadUserByUsername(nickName);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principal,
                principal.getPassword(), principal.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        return context;
    }
}
