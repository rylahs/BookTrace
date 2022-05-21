package org.booktrace.member.endpoint.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MemberController {

    @GetMapping("/sign-up")
    public String signUpForm(Model model) {
        model.addAttribute(new SignUpForm()); // attribute를 추가할 때 클래스의 camel-case와 동일한 키를 사용하는 경우 키를 별도로 지정할 필요가 없습니다.
        return "member/sign-up";
    }
}
