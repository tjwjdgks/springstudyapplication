package seo.study.studyspringapplication.modules.account;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import seo.study.studyspringapplication.infra.MockMvcTest;
import seo.study.studyspringapplication.infra.mail.EmailMessage;
import seo.study.studyspringapplication.infra.mail.EmailService;


import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcTest
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AccountRepository accountRepository;

    @MockBean
    EmailService emailService;

    @Autowired
    AccountFactory accountFactory;
    @DisplayName("인증 메일 확인 - 입력값 오류")
    @Test
    public void checkEmailToken_with_wrong_input() throws Exception{
        mockMvc.perform(get("/check-email-token")
                        .param("token","testtest")
                        .param("email","email@email"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("/account/checked-email"))
                .andExpect(unauthenticated()); // 인증 확인
    }

    @DisplayName("인증 메일 확인 - 입력값 정상")
    @Test
    public void checkEmailToken() throws Exception{
        Account newAccount = accountFactory.createAccount("seo");
        newAccount.generateEmailToken();

        mockMvc.perform(get("/check-email-token")
                        .param("token",newAccount.getEmailCheckToken())
                        .param("email",newAccount.getEmail()))
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(model().attributeExists("nickname"))
                .andExpect(model().attributeExists("numberOfUser"))
                .andExpect(view().name("/account/checked-email"))
                .andExpect(authenticated());
    }

    //@SneakyThrows // 예외
    @DisplayName("회원 가입 시 화면 보이기 테스트")
    @Test
    public void signUpFrom() throws Exception {
        mockMvc.perform(get("/sign-up"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(model().attributeExists("signUpForm"))
                .andExpect(unauthenticated());
    }

    @DisplayName("회원 가입 처리 - 입력한 값 오류")
    @Test
    public void signUpSubmit_with_wrong_input() throws Exception {
        mockMvc.perform(post("/sign-up")
                        .param("nickname","seo")
                        .param("email","emails.")
                        .param("password","21")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(unauthenticated());
    }

    @DisplayName("회원 가입 처리 - 정상 ")
    @Test
    public void signUpSubmit_with_correct_input() throws Exception {
        mockMvc.perform(post("/sign-up")
                        .param("nickname","seo")
                        .param("email","tjwjdgks@naver.com")
                        .param("password","12345678")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"))
                .andExpect(authenticated())
                .andExpect(authenticated().withAuthenticationName("seo"));

        Account account = accountRepository.findByEmail("tjwjdgks@naver.com");
        assertNotNull(account);
        assertNotEquals(account.getPassword(),"12345678");
        assertNotNull(account.getEmailCheckToken());
        assertTrue(accountRepository.existsByEmail("tjwjdgks@naver.com"));
        then(emailService).should().sendEmail(any(EmailMessage.class));
    }

    @DisplayName("이메일로 로그인 폼")
    @Test
    public void login_by_emailForm() throws Exception {
        mockMvc.perform(get("/email-login"))
                .andExpect(status().isOk());

    }
    @DisplayName("이메일로 로그인 메일 보내기 - 전송 성공")
    @Test
    public void login_by_email_success() throws Exception{

        Account account = accountFactory.createAccountBeforeTime("test",LocalDateTime.now().minusHours(1));


        mockMvc.perform(post("/email-login").param("email",account.getEmail()).with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("message"));
    }
    @DisplayName("이메일로 로그인 메일 보내기 - 전송 실패")
    @Test
    public void login_by_email_fail() throws Exception{

        Account account = accountFactory.createAccountBeforeTime("test",LocalDateTime.now().minusHours(1));
        mockMvc.perform(post("/email-login").param("email",account.getEmail()).with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("message"));
    }
}