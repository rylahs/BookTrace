package org.booktrace.app.settings.controller;

import org.booktrace.app.PreAccount;
import org.booktrace.app.member.domain.entity.Member;
import org.booktrace.app.member.infra.repository.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class SettingsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @AfterEach
    void afterEach() {
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("프로필 수정 성공")
    @PreAccount("testAcc")
    public void updateProfile() throws Exception {
        String bio = "한 줄 소개";
        mockMvc.perform(MockMvcRequestBuilders.post(SettingsController.SETTINGS_PROFILE_URL)
                        .param("bio", bio)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl(SettingsController.SETTINGS_PROFILE_URL))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("success_message"));

        Member testMember = memberRepository.findByNickname("testAcc");
        assertThat(bio).isEqualTo(testMember.getProfile().getBio());
    }

    @Test
    @DisplayName("프로필 수정 실패(입력값 에러)")
    @PreAccount("testAcc")
    public void updateProfileFailInputError() throws Exception {
        String bio = "40자 넘으면 에러입니다. QWERTYASDFGZXCVBQWERTYASDFGZXCVBQWERTYASDFGZXCVBQWERTYASDFGZXCVB"; // 40자 초과 입력
        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
                        .param("bio", bio)
                        .with(csrf()))
                .andExpect(status().isOk()) // 200번 OK이지만 에러
                .andExpect(view().name(SettingsController.SETTING_PROFILE_TEMPLATE_NAME)) // 리다이렉트가 아니라 그 뷰를 보여줌
                .andExpect(model().hasErrors()) // 에러 객체가 있는지 확인
                .andExpect(model().attributeExists("member")) // Setting Controller에서 에러 인 경우 동작하는 지 확인
                .andExpect(model().attributeExists("memberProfile"));
        Member testMember = memberRepository.findByNickname("testAcc");
        assertThat(testMember.getProfile().getBio()).isNull(); // DB에 소개가 업데이트 되지 않았을 것이므로 Null
    }

    @Test
    @DisplayName("프로필 조회 : 성공")
    @PreAccount("testAcc")
    void updateProfileForm() throws Exception {
        String bio = "한줄소개입력";
        mockMvc.perform(get(SettingsController.SETTINGS_PROFILE_URL))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTING_PROFILE_TEMPLATE_NAME))
                .andExpect(model().attributeExists("member"))
                .andExpect(model().attributeExists("memberProfile"));
    }

    @Test
    @DisplayName("비밀번호 변경 폼 진입 테스트")
    @PreAccount("testAcc")
    void updatePasswordForm() throws Exception {
        mockMvc.perform(get(SettingsController.SETTINGS_PASSWORD_URL))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTING_PASSWORD_TEMPLATE_NAME))
                .andExpect(model().attributeExists("member"))
                .andExpect(model().attributeExists("passwordForm"));
    }

    @Test
    @DisplayName("비밀번호 변경 성공")
    @PreAccount("testAcc")
    void updatePasswordSuccess() throws Exception {
        mockMvc.perform(post(SettingsController.SETTINGS_PASSWORD_URL)
                        .param("newPassword", "12344321")
                        .param("newPasswordConfirm", "12344321")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTINGS_PASSWORD_URL))
                .andExpect(flash().attributeExists("success_message"));

        Member member = memberRepository.findByNickname("testAcc");
        assertThat(passwordEncoder.matches("12344321", member.getPassword()));
    }





    @Test
    @DisplayName("비밀번호 변경 실패(일치하지 않음)")
    @PreAccount("testAcc")
    void updatePasswordFailNotEqual() throws Exception {
        mockMvc.perform(post(SettingsController.SETTINGS_PASSWORD_URL)
                        .param("newPassword", "12344321")
                        .param("newPasswordConfirm", "12344325")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTING_PASSWORD_TEMPLATE_NAME))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("passwordForm"))
                .andExpect(model().attributeExists("member"));

        Member member = memberRepository.findByNickname("testAcc");
        assertThat(passwordEncoder.matches("12341234", member.getPassword()));
    }
    @Test
    @DisplayName("비밀번호 변경 실패(길이가 짧음)")
    @PreAccount("testAcc")
    void updatePasswordFailShortLength() throws Exception {
        mockMvc.perform(post(SettingsController.SETTINGS_PASSWORD_URL)
                        .param("newPassword", "1234")
                        .param("newPasswordConfirm", "1234")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTING_PASSWORD_TEMPLATE_NAME))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("passwordForm"))
                .andExpect(model().attributeExists("member"));

        Member member = memberRepository.findByNickname("testAcc");
        assertThat(passwordEncoder.matches("12341234", member.getPassword()));
    }
    @Test
    @DisplayName("비밀번호 변경 실패(길이가 김)")
    @PreAccount("testAcc")
    void updatePasswordFailLongLength() throws Exception {
        mockMvc.perform(post(SettingsController.SETTINGS_PASSWORD_URL)
                        .param("newPassword", "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890")
                        .param("newPasswordConfirm", "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTING_PASSWORD_TEMPLATE_NAME))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("passwordForm"))
                .andExpect(model().attributeExists("member"));

        Member member = memberRepository.findByNickname("testAcc");
        assertThat(passwordEncoder.matches("12341234", member.getPassword()));
    }


}