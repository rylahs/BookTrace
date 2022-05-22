package org.booktrace.app.member.endpoint.controller;

import org.booktrace.app.member.domain.entity.Member;
import org.booktrace.app.member.support.CurrentUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    @GetMapping("/")
    public String home(@CurrentUser Member member, Model model) { // @CurrentUser의 영향을 받아 현재 인증된 사용자 정보에 따라 객체가 할당
        if (member != null) {
            model.addAttribute(member);
        }

        return "index";
    }
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
