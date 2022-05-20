package org.booktrace.member.endpoint.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MemberController {

    @GetMapping("/sign-up")
    public String signUpForm(Model model) {
        return "member/sign-up";
    }
}
