package org.booktrace.app.member.endpoint.controller;

import org.booktrace.app.member.domain.entity.Member;
import org.booktrace.app.member.infra.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
                .andExpect(MockMvcResultMatchers.model().attributeExists("signUpForm")) // 객체로 전달했던 attribute가 존재하는지 확인합니다.
                .andExpect(unauthenticated());
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
                .andExpect(view().name("member/sign-up"))
                .andExpect(unauthenticated());
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
                .andExpect(view().name("member/sign-up"))
                .andExpect(unauthenticated());
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
                .andExpect(view().name("member/sign-up"))
                .andExpect(unauthenticated());
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
                .andExpect(view().name("member/sign-up"))
                .andExpect(unauthenticated());
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
                .andExpect(view().name("redirect:/"))
                .andExpect(authenticated().withUsername("BookTraceAdmin"));

        assertThat(memberRepository.existsByEmail("booktrace@admin.com")).isTrue();

        Member member = memberRepository.findByEmail("booktrace@admin.com");
        assertThat(member.getPassword()).isNotEqualTo("12341234");

        assertThat(member.getEmailToken()).isNotNull();

        then(mailSender)
                .should()
                .send(any(SimpleMailMessage.class));
    }


    @Test
    @DisplayName("회원 가입 성공 후 비밀번호 검증")
    public void signUpSubmitPasswordNotEquals() throws Exception {
        mockMvc.perform(post("/sign-up")
                        .param("nickname", "BookTAd")
                        .param("email", "booktad@admin.com")
                        .param("password", "12341234")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"))
                .andExpect(authenticated().withUsername("BookTAd"));

        assertThat(memberRepository.existsByEmail("booktad@admin.com")).isTrue();

        Member member = memberRepository.findByEmail("booktad@admin.com");
        assertThat(member.getPassword()).isNotEqualTo("12341234");
//        System.out.println("account.getPassword() = " + account.getPassword());

        then(mailSender)
                .should()
                .send(any(SimpleMailMessage.class));

    }


    @Test
    @DisplayName("인증 메일 확인: 잘못된 링크 상황")
    public void verifyEmailWithWrongLink() throws Exception {
        mockMvc.perform(get("/check-email-token")
                        .param("token", "token") // 유효하지 않은 토큰과 이메일을 입력
                        .param("email", "email"))
                .andExpect(status().isOk()) // 상태 자체는 200 OK 에서 변함이 없음
                .andExpect(view().name("member/email-verification")) // 뷰도 변함 없음
                .andExpect(model().attributeExists("error")) // error 객체가 model 객체를 통해 전달
                .andExpect(unauthenticated());

    }

    @Test
    @DisplayName("인증 메일 확인: 유효한 링크 상황")
    @Transactional // DB Use
    public void verifyEmailWithSuccessLink() throws Exception {
        Member member = Member.builder() // 토큰을 생성하고 비교해야 되므로 계정을 생성
                .email("test@test.com")
                .password("12341234")
                .nickname("testwwwww")
                .build();

        Member newMember = memberRepository.save(member);// Member Entity Save

        newMember.generateToken(); //  Token Generate

        mockMvc.perform(get("/check-email-token")
                        .param("token", newMember.getEmailToken()) // 생성한 계정의 토큰
                        .param("email", newMember.getEmail())) // 생성한 계정의 이메일
                .andExpect(status().isOk()) // 상태는 변함 없음
                .andExpect(view().name("member/email-verification")) // 뷰도 변함 없음
                .andExpect(model().attributeDoesNotExist("error")) // Error 객체가 포함되지 않았는지 체크
                .andExpect(model().attributeExists("numberOfMembers", "nickname")) // numberOfMembers와 nickname이 Model에 담겼는지 체크
                .andExpect(authenticated().withUsername("testwwwww"));
    }

}