package org.booktrace.app.settings.controller;

import org.assertj.core.api.Assertions;
import org.booktrace.app.PreAccount;
import org.booktrace.app.member.domain.entity.Member;
import org.booktrace.app.member.infra.repository.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;


@SpringBootTest
@AutoConfigureMockMvc
class SettingsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    MemberRepository memberRepository;

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


}