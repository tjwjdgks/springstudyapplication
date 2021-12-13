package seo.study.studyspringapplication.modules.study;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import seo.study.studyspringapplication.infra.MockMvcTest;
import seo.study.studyspringapplication.modules.account.WithAccount;
import seo.study.studyspringapplication.modules.account.AccountRepository;
import seo.study.studyspringapplication.modules.account.Account;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcTest
class StudyControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    StudyRepository studyRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    StudyService studyService;

    @AfterEach
    public void afterEach(){
        accountRepository.deleteAll();
    }

    @Test
    @WithAccount("test")
    @DisplayName("스터디 개설 폼 조회")
    public void createStudyForm() throws Exception {
        mockMvc.perform(get("/new-study"))
                .andExpect(status().isOk())
                .andExpect(view().name("study/form"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("studyForm"));
    }

    @Test
    @WithAccount("test")
    @DisplayName("스터디 개설 완료")
    public void createStudy_seccess() throws Exception{
        mockMvc.perform(post("/new-study")
                        .param("path","test")
                        .param("title","test t")
                        .param("shortDescription","test sd")
                        .param("fullDescription","test fd")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/test"));

        Study study = studyRepository.findByPath("test");
        assertNotNull(study);
        Account account = accountRepository.findByNickname("test");
        assertTrue(study.getManagers().contains(account));
    }
    @Test
    @WithAccount("test")
    @DisplayName("스터디 개설 실패")
    public void createStudy_fail() throws Exception{
        mockMvc.perform(post("/new-study")
                        .param("path","wrong path")
                        .param("title","test t")
                        .param("shortDescription","test sd")
                        .param("fullDescription","test fd")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("study/form"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("studyForm"));
    }

    @Test
    @WithAccount("test")
    @DisplayName("스터디 조회")
    public void viewStudy() throws Exception {
        Study study = new Study();
        study.setPath("test");
        study.setTitle("test study");
        study.setShortDescription("tt");
        study.setFullDescription("tfd");

        Account test = accountRepository.findByNickname("test");
        studyService.creatNewStudy(study,test);

        mockMvc.perform(get("/study/test"))
                .andExpect(status().isOk())
                .andExpect(view().name("study/view"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"));
    }
}