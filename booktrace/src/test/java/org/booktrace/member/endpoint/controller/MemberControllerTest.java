package org.booktrace.member.endpoint.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class MemberControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("회원 가입 화면 진입 성공 테스트")
    public void signUpFormAccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/sign-up"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk()) // HTTP Status가 200 OK인지 확인합니다.
                .andExpect(MockMvcResultMatchers.view().name("member/sign-up")) // view가 제대로 이동했는지 확인합니다.
                .andExpect(MockMvcResultMatchers.model().attributeExists("signUpForm")); // 객체로 전달했던 attribute가 존재하는지 확인합니다.
    }

}