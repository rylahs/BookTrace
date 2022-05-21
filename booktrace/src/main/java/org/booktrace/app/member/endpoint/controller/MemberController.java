package org.booktrace.app.member.endpoint.controller;

import lombok.RequiredArgsConstructor;
import org.booktrace.app.member.application.MemberService;
import org.booktrace.app.member.domain.entity.Member;
import org.booktrace.app.member.endpoint.controller.validator.SignUpFormValidator;
import org.booktrace.app.member.infra.repository.MemberRepository;
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
    private final MemberService memberService;

    private final MemberRepository memberRepository;
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

        memberService.signUp(signUpForm);

        return "redirect:/";
    }

    @GetMapping("/check-email-token")
    public String verifyEmail(String token, String email, Model model) { // 이메일 링크 클릭시 호출되는 메소드
        Member member = memberService.findMemberByEmail(email); // MemberService에서 Email을 통해 회원 정보 조회

        if (member == null) { // 계정이 없으면 Error를 model 객체에 담아서 전달
            model.addAttribute("error", "wrong.email");
            return "member/email-verification";
        }

        if (!token.equals(member.getEmailToken())) { // 계정은 있지만 토큰이 일치하지 않으면 model 객체에 Error를 담아서 전달
            model.addAttribute("error", "wrong.token");
            return "member/email-verification";
        }

        member.verified(); // 인증 성공

        model.addAttribute("numberOfMembers", memberRepository.count()); // 성공시 보여줄 객체를 model에 담아서 전달
        model.addAttribute("nickname", member.getNickname()); // 성공시 보여줄 객체를 model에 담아서 전달

        return "member/email-verification"; // Redirect 이메일 인증
    }

}
