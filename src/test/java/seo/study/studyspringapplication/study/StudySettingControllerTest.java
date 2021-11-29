package seo.study.studyspringapplication.study;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import seo.study.studyspringapplication.WithAccount;
import seo.study.studyspringapplication.account.AccountRepository;
import seo.study.studyspringapplication.account.AccountService;
import seo.study.studyspringapplication.account.SignUpForm;
import seo.study.studyspringapplication.domain.Account;
import seo.study.studyspringapplication.domain.Study;
import seo.study.studyspringapplication.study.form.StudyDescriptionForm;
import seo.study.studyspringapplication.study.form.StudyForm;

import javax.validation.constraints.AssertTrue;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class StudySettingControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    AccountService accountService;
    @Autowired
    StudyService studyService;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    StudyRepository studyRepository;
    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    public void beforeEach(){
        SignUpForm test = new SignUpForm();
        test.setNickname("test");
        test.setPassword("01234567789");
        test.setEmail("test@test.com");
        Account account = accountService.processNewAccount(test);

        StudyForm studyForm = new StudyForm();
        studyForm.setPath("test");
        studyForm.setTitle("test");
        studyForm.setFullDescription("aaaa");
        studyForm.setShortDescription("aaaa");
        studyService.creatNewStudy(modelMapper.map(studyForm,Study.class),account);
    }
    @AfterEach
    public void afterEach(){
        studyRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    @WithUserDetails(value = "test",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("스터디 세팅 폼 조회")
    public void studySettingForm() throws Exception {
        mockMvc.perform(get("/study/test/settings/description"))
                .andExpect(status().isOk())
                .andExpect(view().name("study/settings/description"))
                .andExpect(model().attributeExists("studyDescriptionForm"))
                .andExpect(model().attributeExists("account"));
    }

    @Test
    @WithUserDetails(value = "test",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("스터디 세팅 실패")
    public void updateStudyInfo_fail() throws Exception {
        mockMvc.perform(post("/study/test/settings/description")
                        .param("shortDescription","")
                        .param("fullDescription","2")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors());
    }

    @Test
    @WithUserDetails(value = "test",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("스터디 세팅 성공")
    public void updateStudyInfo_success() throws Exception {
        mockMvc.perform(post("/study/test/settings/description")
                        .param("shortDescription","11")
                        .param("fullDescription","2")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/test/settings/description"));

        Study test = studyRepository.findByPath("test");
        assertEquals("11",test.getShortDescription());
        assertEquals("2",test.getFullDescription());
    }

    @Test
    @WithUserDetails(value = "test",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("banner 테스트")
    public void bannerForm() throws Exception{
        mockMvc.perform(get("/study/test/settings/banner"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"))
                .andExpect(view().name("study/settings/banner"));
    }
    @Test
    @WithUserDetails(value = "test",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("banner 업데이트")
    public void bannerUpdate() throws Exception{
        mockMvc.perform(post("/study/test/settings/banner")
                        .param("image","test image")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/test/settings/banner"));
        Study test = studyRepository.findByPath("test");
        assertEquals("test image",test.getImage());
    }

    @Test
    @WithUserDetails(value = "test",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("banner 사용")
    public void enableBanner() throws Exception{
        mockMvc.perform(post("/study/test/settings/banner/enable")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/test/settings/banner"));
        Study test = studyRepository.findByPath("test");
        assertTrue(test.isUseBanner());
    }
    @Test
    @WithUserDetails(value = "test",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("banner 사용 안함")
    public void disableBanner() throws Exception{
        mockMvc.perform(post("/study/test/settings/banner/disable")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/test/settings/banner"));
        Study test = studyRepository.findByPath("test");
        assertFalse(test.isUseBanner());
    }

}