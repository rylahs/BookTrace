package org.booktrace.member.endpoint.controller;

import lombok.RequiredArgsConstructor;
import org.booktrace.member.domain.entity.Member;
import org.booktrace.member.endpoint.controller.validator.SignUpFormValidator;
import org.booktrace.member.infra.repository.MemberRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final SignUpFormValidator signUpFormValidator;
    private final MemberRepository memberRepository; // MemberRepository DI
    private final JavaMailSender mailSender; // JavaMailSender DI

    /**
     * @InitBinder를 사용해서 Attribute로 바인딩할 객체를 지정해서
     * WebDataBinder를 이용해서 Validator를 추가해준다.
     * 검증 로직을 직접 추가 X
     */
    @InitBinder("signUpForm") //
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signUpFormValidator);
    }

    @GetMapping("/sign-up")
    public String signUpForm(Model model) {
        model.addAttribute(new SignUpForm()); // attribute를 추가할 때 클래스의 camel-case와 동일한 키를 사용하는 경우 키를 별도로 지정할 필요가 없습니다.
        return "member/sign-up";
    }

    @PostMapping("/sign-up")
    public String signUpSubmit(@Validated @ModelAttribute SignUpForm signUpForm, BindingResult bindingResult) {
        /**
         * @Validated 애너테이션을 추가하면 타입에 대한 검증을 실시합니다.
         * @ModelAttributes 애너테이션을 추가하면 전달 받은 데이터를 해당 타입에 매핑해줍니다.
         * BindingResult : 에러를 담을 수 있는 객체입니다.
         */

        //  에러가 존재할 경우 다시 회원가입 페이지를 띄웁니다. Errors 객체로 에러가 전달되기 때문에 Thymeleaf로 렌더링 된 HTML에 해당 에러를 전달해 업데이트할 수 있습니다.
        if (bindingResult.hasErrors()) {
            return "member/sign-up";
        }

        /** 검증 로직 부분
        signUpFormValidator.validate(signUpForm,bindingResult); // Validator를 이용해 객체를 검증하고, 에러가 있을 경우 기존과 동일하게 처리합니다.
        if (bindingResult.hasErrors()) {
            return "member/sign-up";
        }
        */

        /** 회원 가입 로직 시작 */
        Member member = Member.builder() // Entity를 생성
                .email(signUpForm.getEmail())
                .password(signUpForm.getPassword())
                .nickname(signUpForm.getNickname())
                .build();

        Member newMember = memberRepository.save(member); // Entity를 저장

        newMember.generateToken(); //  이메일 인증용 토큰을 생성

        SimpleMailMessage mailMessage = new SimpleMailMessage(); //  이메일을 객체를 생성

        // 이메일 내용 삽입
        mailMessage.setTo(newMember.getEmail());
        mailMessage.setSubject("BookTrace 회원 가입 인증");
        mailMessage.setText(String.format("/check-email-token?token=%s&email=%s",
                                newMember.getEmailToken(), newMember.getEmail())); // 본문에 추가할 링크 주소

        mailSender.send(mailMessage); // 메일 보내기


        return "redirect:/";
    }
}
