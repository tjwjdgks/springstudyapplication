package seo.study.studyspringapplication.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import seo.study.studyspringapplication.account.AccountService;
import seo.study.studyspringapplication.aop.TimeTest;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity // spring curity 커스터 마이징 한다는 것
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final AccountService accountService;
    private final DataSource dataSource;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .mvcMatchers("/","/login","/sign-up","/check-email-token",
                        "/email-login","/check-email-login","/login-link","login-by-email").permitAll()
                .mvcMatchers(HttpMethod.GET,"/profile/*").permitAll()
                .anyRequest().authenticated();

        http.formLogin()
                .loginPage("/login").permitAll();
        http.logout().logoutSuccessUrl("/");

        http.httpBasic();

        // key로 해싱 알고리즘, 쿠키 생성 안전하지 않음
        //http.rememberMe().key("dfsewrwre");

        // 쿠키 로그인 기억하기 username 정보, token, 시리즈 // 안전한 방법
        http.rememberMe()
                .userDetailsService(accountService)
                .tokenRepository(tokenRepository());
    }

    @Bean
    public PersistentTokenRepository tokenRepository(){
        // jdbcTokenRepository datasoruce 필요, 현재 jpa 쓰고 있으므로 datasource 등록되어 있음
        // JdbcTokenRepositoryImpl 사용하는 테이블이 있으며 사용하는 db에 스키마 있어야 한다
        JdbcTokenRepositoryImpl jdbcTokenRepository  = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().mvcMatchers("/favicon.ico");
        web.ignoring().mvcMatchers("/node_modules/**");
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }
}
