package seo.study.studyspringapplication.modules.main;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import seo.study.studyspringapplication.infra.MockMvcTest;
import seo.study.studyspringapplication.modules.account.AccountRepository;
import seo.study.studyspringapplication.modules.account.AccountService;
import seo.study.studyspringapplication.modules.account.form.SignUpForm;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockMvcTest
class MainControllerTest {
    // junit이 먼저 개입을 하기 때문에 생성자로 주입은 안된다.
    @Autowired MockMvc mockMvc;
    @Autowired AccountService accountService;
    @Autowired
    AccountRepository accountRepository;

    @BeforeEach
    public void beforeEach(){
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setNickname("test");
        signUpForm.setEmail("seo@email.com");
        signUpForm.setPassword("12345678");

        accountService.processNewAccount(signUpForm);
    }
    @AfterEach
    public void afterEach(){
        accountRepository.deleteAll();
    }

    @Test
    @DisplayName("이메일로 로그인")
    public void login_with_email() throws Exception{
        mockMvc.perform(post("/login")
                        .param("username","seo@email.com")
                        .param("password","12345678")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withAuthenticationName("test")); // UserAccount class에서 nickname으로 저장
        
    }

    @Test
    @DisplayName("닉네임으로 로그인")
    public void login_with_nickname() throws Exception{

        mockMvc.perform(post("/login")
                        .param("username","test")
                        .param("password","12345678")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withAuthenticationName("test")); // UserAccount class에서 nickname으로 저장

    }

    @Test
    @DisplayName("로그인 실패")
    public void login_fail() throws Exception{
        mockMvc.perform(post("/login")
                        .param("username","1111")
                        .param("password","31231322")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());
    }

}