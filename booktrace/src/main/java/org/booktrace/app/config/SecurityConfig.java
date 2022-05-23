package org.booktrace.app.config;

import lombok.RequiredArgsConstructor;
import org.booktrace.app.member.application.MemberService;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;
import java.util.UUID;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor // DI를 위해 final 생성자
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final MemberService memberService; // UserDetailService를 DI하기 위해서 생성
    private final DataSource dataSource; // 토큰 저장소를 설정하기 위해서 생성


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .mvcMatchers("/", "/login", "/sign-up", "/check-email-token",
                        "/email-login", "/check-email-login", "/login-link").permitAll()
                .mvcMatchers(HttpMethod.GET, "/profile/*").permitAll()
                .anyRequest().authenticated();

        http.formLogin() // form 기반 인증을 지원
                .loginPage("/login") // 로그인 페이지를 지정
                .permitAll(); // 인증하지 않아도 접근가능
        http.logout() // 로그아웃 설정
                .logoutSuccessUrl("/"); // 루트로 이동

//        http.rememberMe().key(UUID.randomUUID().toString()); // 간단한 해싱 기반은 이렇게 생성하면 되지만 쿠키가 탈취되면 위험하다.

        http.rememberMe() // userDetailService와 token을 관리할 repository를 설정하고 기억해준다.
                .userDetailsService(memberService)
                .tokenRepository(tokenRepository());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                .mvcMatchers("/node_modules/**", "/images/**")
                .antMatchers("/h2-console/**");
    }
    @Bean
    public PersistentTokenRepository tokenRepository() { // 토큰 관리를 위한 repository 구현체를 추가, datasource를 연결
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }

}
