package org.booktrace.member.endpoint.controller;

import org.booktrace.member.infra.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class MemberControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    MemberRepository memberRepository;

    @MockBean
    JavaMailSender mailSender;

    @Test
    @DisplayName("회원 가입 화면 진입 성공 테스트")
    public void signUpFormAccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/sign-up"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk()) // HTTP Status가 200 OK인지 확인합니다.
                .andExpect(MockMvcResultMatchers.view().name("member/sign-up")) // view가 제대로 이동했는지 확인합니다.
                .andExpect(MockMvcResultMatchers.model().attributeExists("signUpForm")); // 객체로 전달했던 attribute가 존재하는지 확인합니다.
    }

    @Test
    @DisplayName("회원가입 처리 : 입력값 오류1 (닉네임 길이 오류)")
    public void signUpSubmitWithNickNameValidLength() throws Exception {
        mockMvc.perform(post("/sign-up")
                        .param("nickname", "Bo")
                        .param("email", "booktrace@admin.com")
                        .param("password", "1234!!!!!")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("member/sign-up"));
    }

    @Test
    @DisplayName("회원가입 처리 : 입력값 오류2 (닉네임 정규식 오류)")
    public void signUpSubmitWithNickNameValidRegexp() throws Exception {
        mockMvc.perform(post("/sign-up")
                        .param("nickname", "^^^^^^")
                        .param("email", "bookadmin@admin.com")
                        .param("password", "1234!!!!!")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("member/sign-up"));
    }

    @Test
    @DisplayName("회원가입 처리 : 입력값 오류3 (이메일 오류)")
    public void signUpSubmitWithErrorEmailValid() throws Exception {
        mockMvc.perform(post("/sign-up")
                        .param("nickname", "Boo")
                        .param("email", "adadadadadad")
                        .param("password", "1234!!!!!!")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("member/sign-up"));
    }

    @Test
    @DisplayName("회원가입 처리 : 입력값 오류4 (비밀번호 오류)")
    public void signUpSubmitWithErrorPasswordValid() throws Exception {
        mockMvc.perform(post("/sign-up")
                        .param("nickname", "BookTrace")
                        .param("email", "booktrace@admin.com")
                        .param("password", "1234!")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("member/sign-up"));
    }


    @Test
    @DisplayName("회원가입 처리 : 성공")
    public void signUpSubmitSuccess() throws Exception {
        mockMvc.perform(post("/sign-up")
                        .param("nickname", "BookTraceAdmin")
                        .param("email", "booktrace@admin.com")
                        .param("password", "12341234")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));
    }

}