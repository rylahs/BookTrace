package org.booktrace.app.settings.controller;


import org.booktrace.app.member.domain.entity.Member;
import org.booktrace.app.member.support.CurrentUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SettingsController {

    @GetMapping("/settings/profile") // 인증된 사용자만 이용 가능
    public String profileUpdateForm(@CurrentUser Member member, Model model) {
        model.addAttribute(member);
        model.addAttribute(MemberProfile.from(member));

        return "settings/profile";

    }

}
