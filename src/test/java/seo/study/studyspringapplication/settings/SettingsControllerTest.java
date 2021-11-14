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
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import seo.study.studyspringapplication.WithAccount;
import seo.study.studyspringapplication.account.AccountRepository;
import seo.study.studyspringapplication.account.AccountService;
import seo.study.studyspringapplication.account.SignUpForm;
import seo.study.studyspringapplication.domain.Account;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
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
}