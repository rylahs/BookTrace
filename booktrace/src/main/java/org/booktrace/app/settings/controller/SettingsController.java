package org.booktrace.app.settings.controller;


import lombok.RequiredArgsConstructor;
import org.booktrace.app.member.application.MemberService;
import org.booktrace.app.member.domain.entity.Member;
import org.booktrace.app.member.support.CurrentUser;
import org.booktrace.app.settings.controller.dto.MemberProfile;
import org.booktrace.app.settings.controller.dto.NicknameForm;
import org.booktrace.app.settings.controller.dto.PasswordForm;
import org.booktrace.app.settings.controller.validator.NicknameFormValidator;
import org.booktrace.app.settings.controller.validator.PasswordFormValidator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class SettingsController {
    static final String SETTING_PROFILE_TEMPLATE_NAME = "settings/profile";
    static final String SETTINGS_PROFILE_URL = "/" + SETTING_PROFILE_TEMPLATE_NAME;

    static final String SETTING_PASSWORD_TEMPLATE_NAME = "settings/password";
    static final String SETTINGS_PASSWORD_URL = "/" + SETTING_PASSWORD_TEMPLATE_NAME;

    static final String SETTINGS_MEMBER_VIEW_NAME = "settings/member";
    static final String SETTINGS_MEMBER_URL = "/" + SETTINGS_MEMBER_VIEW_NAME;
    private final MemberService memberService;

    private final PasswordFormValidator passwordFormValidator;
    private final NicknameFormValidator nicknameFormValidator;


    @InitBinder("passwordForm")
    public void setPasswordFormValidator(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(passwordFormValidator);
    }

    @InitBinder("nicknameForm")
    public void setNicknameFormValidator(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(nicknameFormValidator);
    }

    @GetMapping(SETTINGS_PROFILE_URL) // 인증된 사용자만 이용 가능
    public String profileUpdateForm(@CurrentUser Member member, Model model) {
        model.addAttribute(member);
        model.addAttribute(MemberProfile.from(member));

        return SETTING_PROFILE_TEMPLATE_NAME;
    }

    @PostMapping(SETTINGS_PROFILE_URL)
    public String profileUpdate(@CurrentUser Member member, @ModelAttribute(name = "memberProfile") @Validated MemberProfile memberProfile,
                                BindingResult bindingResult, Model model, RedirectAttributes attributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute(member);
            return SETTING_PROFILE_TEMPLATE_NAME;
        }
        memberService.updateProfile(member, memberProfile);
        attributes.addFlashAttribute("success_message", "프로필을 수정하였습니다.");
        return "redirect:" + SETTINGS_PROFILE_URL;
    }


    @GetMapping(SETTINGS_PASSWORD_URL)
    public String passwordUpdateForm(@CurrentUser Member member, Model model) {
        model.addAttribute(member); // 모델에 멤버와
        model.addAttribute(new PasswordForm());// 새 패스워드 폼을 담음
        return this.SETTING_PASSWORD_TEMPLATE_NAME;
    }

    @PostMapping(SETTINGS_PASSWORD_URL)
    public String passwordUpdate(@CurrentUser Member member, @ModelAttribute(name = "passwordForm") @Validated PasswordForm passwordForm,
                                 BindingResult bindingResult, Model model, RedirectAttributes attributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(member);
            return SETTING_PASSWORD_TEMPLATE_NAME;
        }

        memberService.updatePassword(member, passwordForm.getNewPassword());
        attributes.addFlashAttribute("success_message", "비밀번호를 변경했습니다.");
        return "redirect:" + SETTINGS_PASSWORD_URL;
    }

    @GetMapping(SETTINGS_MEMBER_URL)
    public String nicknameForm(@CurrentUser Member member, Model model) {
        model.addAttribute(member);
        model.addAttribute(new NicknameForm(member.getNickname()));
        return SETTINGS_MEMBER_VIEW_NAME;
    }

    @PostMapping(SETTINGS_MEMBER_URL)
    public String updateNickname(@CurrentUser Member member, @Validated NicknameForm nicknameForm, BindingResult bindingResult, Model model, RedirectAttributes attributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(member);
            return SETTINGS_MEMBER_VIEW_NAME;
        }
        memberService.updateNickname(member, nicknameForm.getNickname());
        attributes.addFlashAttribute("success_message", "닉네임이 수정되었습니다.");
        return "redirect:" + SETTINGS_MEMBER_URL;
    }




    @GetMapping("/settings/favorite")
    public String tempFavorite() {
        return "settings/favorite";
    }


}
