package seo.study.studyspringapplication.settings;

import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import seo.study.studyspringapplication.WithAccount;
import seo.study.studyspringapplication.account.AccountRepository;
import seo.study.studyspringapplication.account.AccountService;
import seo.study.studyspringapplication.account.SignUpForm;
import seo.study.studyspringapplication.domain.Account;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class SettingsControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    /*
    // 선택1
    @BeforeEach
    public void beforeEach(){
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setNickname("test");
        signUpForm.setPassword("12345678");
        signUpForm.setEmail("test@gmail.com");

        accountService.processNewAccount(signUpForm);
    }
    @AfterEach
    public void afterEach(){

        accountRepository.deleteAll();
    }
     */

    // 선택2
    @AfterEach
    public void afterEach(){
        accountRepository.deleteAll();
    }

    // @WithUserDetails(value="test",setupBefore = TestExecutionEvent.TEST_EXECUTION) // 선택 1
    @WithAccount("test") // 선택 2
    @DisplayName("프로필 값 수정하기 - 입력값 정상")
    @Test
    public void updateProfile() throws Exception{
        String bio = "bio 수정하는 경우";
        mockMvc.perform(post("/settings/profile")
                        .param("bio", bio)
                        .with(csrf())) // spring security, form data 보낼 때는 항상 csrf 토큰 같이 보내야함
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/profile"))
                .andExpect(flash().attributeExists("message"));

        Account test = accountRepository.findByNickname("test");
        assertEquals(bio,test.getBio());
    }

    @WithAccount("test")
    @DisplayName("프로필 값 수정하지 - 입력값 에러")
    @Test
    public void updateProfile_error() throws Exception{
        String bio = "35자 초과 35자 초과 35자 초과 35자 초과 35자 초과 35자 초과 35자 초과 35자 초과 35자 초과 35자 초과 35자 초과 ";
        mockMvc.perform(post("/settings/profile")
                        .param("bio", bio)
                        .with(csrf())) // spring security, form data 보낼 때는 항상 csrf 토큰 같이 보내야함
                .andExpect(status().isOk()) // 응답은 ok
                .andExpect(view().name("settings/profile"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().hasErrors());

        Account test = accountRepository.findByNickname("test");
        assertNull(test.getBio());
    }

    @WithAccount("test") // 선택 2
    @DisplayName("프로필 수정 폼")
    @Test
    public void updateProfileForm() throws Exception{
        mockMvc.perform(get("/settings/profile")
                        .with(csrf())) // spring security, form data 보낼 때는 항상 csrf 토큰 같이 보내야함
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attribute("account",hasProperty("nickname",is("test"))))
                .andExpect(model().attributeExists("profile"));


    }
    @WithAccount("test")
    @DisplayName("패스워드 수정 폼")
    @Test
    public void updatePasswordForm() throws Exception{
        mockMvc.perform(get("/settings/password")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"));
    }

    @WithAccount("test")
    @DisplayName("패스워드 수정 - 입력값 정상")
    @Test
    public void updatePassword() throws Exception {
        mockMvc.perform(post("/settings/password")
                        .param("newPassword","12345678")
                        .param("newPasswordConfirm","12345678")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/password"))
                .andExpect(flash().attributeExists("message"));

        Account test = accountRepository.findByNickname("test");
        assertTrue(passwordEncoder.matches("12345678",test.getPassword()));
    }
    @WithAccount("test")
    @DisplayName("패스워드 수정 - 입력값 실패")
    @Test
    public void updatePassword_fail() throws Exception {
        mockMvc.perform(post("/settings/password")
                        .param("newPassword","12345678")
                        .param("newPasswordConfirm","123456781")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/password"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("passwordForm"))
                .andExpect(model().attributeExists("account"));

        Account test = accountRepository.findByNickname("test");
        assertTrue(passwordEncoder.matches("12345678",test.getPassword()));
    }

    @WithAccount("test")
    @DisplayName("알람 수정 폼")
    @Test
    public void updateNotificationsForm() throws Exception{
        mockMvc.perform(get("/settings/notifications")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("notifications"));
    }
    @WithAccount("test")
    @DisplayName("알람 수정")
    @Test
    public void updateNotifications() throws Exception {
        mockMvc.perform(post("/settings/notifications")
                        .param("studyCreatedByEmail","true")
                        .param("studyCreatedByWeb","true")
                        .param("studyEnrollmentResultByEmail","true")
                        .param("studyEnrollmentResultByWeb","true")
                        .param("studyUpdatedByEmail","true")
                        .param("studyUpdatedByWeb","true")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/notifications"))
                .andExpect(flash().attributeExists("message"));

        Account test = accountRepository.findByNickname("test");
        assertAll(
                ()-> assertTrue(test.isStudyCreatedByEmail()),
                ()-> assertTrue(test.isStudyCreatedByWeb()),
                ()-> assertTrue(test.isStudyEnrollmentResultByEmail()),
                ()-> assertTrue(test.isStudyEnrollmentResultByWeb()),
                ()-> assertTrue(test.isStudyUpdatedByEmail()),
                ()-> assertTrue(test.isStudyUpdatedByWeb())
        );
    }
    @WithAccount("test")
    @DisplayName("닉네임 수정 폼")
    @Test
    public void updateAccountForm() throws Exception {
        mockMvc.perform(get("/settings/account").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("nicknameForm"));

    }
    @WithAccount("test")
    @DisplayName("닉네임 변경 정상")
    @Test
    public void updateAccount() throws Exception {
        mockMvc.perform(post("/settings/account")
                        .param("nickname","testtest")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/account"))
                .andExpect(flash().attributeExists("message"));

        assertNotNull(accountRepository.findByNickname("testtest"));
    }
    @WithAccount("test")
    @DisplayName("닉네임 변경 오류")
    @Test
    public void updateAccount_fail() throws Exception {
        mockMvc.perform(post("/settings/account")
                        .param("nickname","1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/account"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().hasErrors());

        assertNull(accountRepository.findByNickname("1"));

    }
}