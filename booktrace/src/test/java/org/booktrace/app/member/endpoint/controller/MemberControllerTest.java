package org.booktrace.app.member.endpoint.controller;

import org.booktrace.app.mail.EmailMessage;
import org.booktrace.app.mail.EmailService;
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
    EmailService emailService;

    @Test
    @DisplayName("?????? ?????? ?????? ?????? ?????? ?????????")
    public void signUpFormAccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/sign-up"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk()) // HTTP Status??? 200 OK?????? ???????????????.
                .andExpect(MockMvcResultMatchers.view().name("member/sign-up")) // view??? ????????? ??????????????? ???????????????.
                .andExpect(MockMvcResultMatchers.model().attributeExists("signUpForm")) // ????????? ???????????? attribute??? ??????????????? ???????????????.
                .andExpect(unauthenticated());
    }

    @Test
    @DisplayName("???????????? ?????? : ????????? ??????1 (????????? ?????? ??????)")
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
    @DisplayName("???????????? ?????? : ????????? ??????2 (????????? ????????? ??????)")
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
    @DisplayName("???????????? ?????? : ????????? ??????3 (????????? ??????)")
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
    @DisplayName("???????????? ?????? : ????????? ??????4 (???????????? ??????)")
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
    @DisplayName("???????????? ?????? : ??????")
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

        then(emailService)
                .should()
                .sendEMail(any(EmailMessage.class));
    }


    @Test
    @DisplayName("?????? ?????? ?????? ??? ???????????? ??????")
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

        then(emailService)
                .should()
                .sendEMail(any(EmailMessage.class));

    }


    @Test
    @DisplayName("?????? ?????? ??????: ????????? ?????? ??????")
    public void verifyEmailWithWrongLink() throws Exception {
        mockMvc.perform(get("/check-email-token")
                        .param("token", "token") // ???????????? ?????? ????????? ???????????? ??????
                        .param("email", "email"))
                .andExpect(status().isOk()) // ?????? ????????? 200 OK ?????? ????????? ??????
                .andExpect(view().name("member/email-verification")) // ?????? ?????? ??????
                .andExpect(model().attributeExists("error")) // error ????????? model ????????? ?????? ??????
                .andExpect(unauthenticated());

    }

    @Test
    @DisplayName("?????? ?????? ??????: ????????? ?????? ??????")
    @Transactional // DB Use
    public void verifyEmailWithSuccessLink() throws Exception {
        Member member = Member.builder() // ????????? ???????????? ???????????? ????????? ????????? ??????
                .email("test@test.com")
                .password("12341234")
                .nickname("testwwwww")
                .build();

        Member newMember = memberRepository.save(member);// Member Entity Save

        newMember.generateToken(); //  Token Generate

        mockMvc.perform(get("/check-email-token")
                        .param("token", newMember.getEmailToken()) // ????????? ????????? ??????
                        .param("email", newMember.getEmail())) // ????????? ????????? ?????????
                .andExpect(status().isOk()) // ????????? ?????? ??????
                .andExpect(view().name("member/email-verification")) // ?????? ?????? ??????
                .andExpect(model().attributeDoesNotExist("error")) // Error ????????? ???????????? ???????????? ??????
                .andExpect(model().attributeExists("numberOfMembers", "nickname")) // numberOfMembers??? nickname??? Model??? ???????????? ??????
                .andExpect(authenticated().withUsername("testwwwww"));
    }

}