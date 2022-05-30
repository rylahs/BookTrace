package org.booktrace.app.member.endpoint.controller;

import org.booktrace.app.member.application.MemberService;
import org.booktrace.app.member.endpoint.controller.dto.SignUpForm;
import org.booktrace.app.member.infra.repository.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MainControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @BeforeEach
    void beforeEach() {
        SignUpForm signUpForm = new SignUpForm();

        signUpForm.setNickname("testUser");
        signUpForm.setEmail("testUser@korea.ac.kr");
        signUpForm.setPassword("12341234");
        memberService.signUp(signUpForm); // 회원 가입
    }

    @AfterEach
    void afterEach() {
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("이메일로 로그인 성공하기")
    public void loginSuccessUsingEmail() throws Exception {
        mockMvc.perform(post("/login") // 로그인 요청
                        .param("username","testUser@korea.ac.kr") // 아이디
                        .param("password","12341234") // 비밀번호
                        .with(csrf())) // csrf 요청이 필요 Spring Security
                .andExpect(status().is3xxRedirection()) // redirect 코드가 오는지 체크
                .andExpect(redirectedUrl("/")) // 루트로 오는지 체크
                .andExpect(authenticated().withUsername("testUser")); // 유저 이름이 일치하는지 체크
    }

    @Test
    @DisplayName("닉네임으로 로그인 성공하기")
    public void loginSuccessUsingNickName() throws Exception {
        mockMvc.perform(post("/login") // 로그인 요청
                        .param("username","testUser") // 아이디
                        .param("password","12341234") // 비밀번호
                        .with(csrf())) // csrf 요청이 필요 Spring Security
                .andExpect(status().is3xxRedirection()) // redirect 코드가 오는지 체크
                .andExpect(redirectedUrl("/")) // 루트로 오는지 체크
                .andExpect(authenticated().withUsername("testUser")); // 유저 이름이 일치하는지 체크
    }

    @Test
    @DisplayName("이메일로 로그인 실패하기")
    public void loginFailUnknownUser() throws Exception {
        mockMvc.perform(post("/login") // 로그인 요청
                        .param("username", "tdd@tdd.com") // 아이디
                        .param("password", "13241324") // 비밀번호
                        .with(csrf())) // csrf 요청이 필요 Spring Security
                .andExpect(status().is3xxRedirection()) // redirect 코드가 오는지 체크
                .andExpect(redirectedUrl("/login?error")) // 자동으로 처리되는 부분
                .andExpect(unauthenticated());
    }


    @Test
    @DisplayName("닉네임으로 로그인 실패하기")
    public void loginFailUnknownNickname() throws Exception {
        mockMvc.perform(post("/login") // 로그인 요청
                        .param("username", "tdd") // 아이디
                        .param("password", "13241324") // 비밀번호
                        .with(csrf())) // csrf 요청이 필요 Spring Security
                .andExpect(status().is3xxRedirection()) // redirect 코드가 오는지 체크
                .andExpect(redirectedUrl("/login?error")) // 자동으로 처리되는 부분
                .andExpect(unauthenticated());
    }


    @Test
    @DisplayName("이메일로 로그인 실패하기")
    public void loginFailWrongPasswordUseEmail() throws Exception {
        mockMvc.perform(post("/login") // 로그인 요청
                        .param("username", "testUser@korea.ac.kr") // 아이디
                        .param("password", "13241324") // 비밀번호
                        .with(csrf())) // csrf 요청이 필요 Spring Security
                .andExpect(status().is3xxRedirection()) // redirect 코드가 오는지 체크
                .andExpect(redirectedUrl("/login?error")) // 자동으로 처리되는 부분
                .andExpect(unauthenticated());
    }

    @Test
    @DisplayName("닉네임으로 로그인 실패하기")
    public void loginFailWrongPasswordUseNickname() throws Exception {
        mockMvc.perform(post("/login") // 로그인 요청
                        .param("username", "testUser") // 아이디
                        .param("password", "13241324") // 비밀번호
                        .with(csrf())) // csrf 요청이 필요 Spring Security
                .andExpect(status().is3xxRedirection()) // redirect 코드가 오는지 체크
                .andExpect(redirectedUrl("/login?error")) // 자동으로 처리되는 부분
                .andExpect(unauthenticated());
    }


    @Test
    @DisplayName("로그아웃_성공")
    public void logout() throws Exception {
        mockMvc.perform(post("/logout") // 로그아웃 요청
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/")) // 리다이렉트 루트로
                .andExpect(unauthenticated()); // 인증되지 않음
    }

}