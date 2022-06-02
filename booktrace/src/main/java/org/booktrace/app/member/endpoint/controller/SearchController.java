package org.booktrace.app.member.endpoint.controller;

import org.booktrace.app.member.domain.entity.Member;
import org.booktrace.app.member.support.CurrentUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SearchController {

    @GetMapping("/search")
    public String search(@CurrentUser Member member, Model model) {
        if (member != null) {
            model.addAttribute(member);
        }

        return "search";
    }
}
