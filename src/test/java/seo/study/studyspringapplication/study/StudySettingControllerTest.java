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
import org.springframework.http.MediaType;
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
import seo.study.studyspringapplication.domain.Tag;
import seo.study.studyspringapplication.domain.Zone;
import seo.study.studyspringapplication.study.form.StudyDescriptionForm;
import seo.study.studyspringapplication.study.form.StudyForm;
import seo.study.studyspringapplication.tag.TagForm;
import seo.study.studyspringapplication.tag.TagRepository;
import seo.study.studyspringapplication.zone.ZoneForm;
import seo.study.studyspringapplication.zone.ZoneRepository;

import javax.validation.constraints.AssertTrue;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
    @Autowired
    TagRepository tagRepository;
    @Autowired
    ZoneRepository zoneRepository;

    private Zone testzone = Zone.builder().city("test").localNameOfCity("테스트시").province("testP").build();


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
        tagRepository.deleteAll();
        zoneRepository.deleteAll();
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
    @Test
    @WithUserDetails(value = "test",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("스터디 tag 폼")
    public void studyTagForm() throws Exception{
        mockMvc.perform(get("/study/test/settings/tags"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("tags"))
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(view().name("study/settings/tags"));
    }
    @Test
    @WithUserDetails(value = "test",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("스터디 tag 저장")
    public void studyTagAdd() throws Exception{
        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("test");
        assertNull(tagRepository.findByTitle("test"));
        mockMvc.perform(post("/study/test/settings/tags/add")
                        .content(objectMapper.writeValueAsString(tagForm))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk());
        assertNotNull(tagRepository.findByTitle("test"));
    }
    @Test
    @WithUserDetails(value = "test",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("스터디 tag 삭제")
    public void studyTagRemove() throws Exception{
        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("test");
        Tag tag = Tag.builder().title("test").build();
        Study test = studyRepository.findStudyWithTagsByPath("test");
        Tag saveTag = tagRepository.save(tag);
        studyService.addTag(test,saveTag);
        assertNotNull(tagRepository.findByTitle("test"));
        mockMvc.perform(post("/study/test/settings/tags/remove")
                        .content(objectMapper.writeValueAsString(tagForm))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk());
        test = studyRepository.findStudyWithTagsByPath("test");
        assertFalse(test.getTags().contains(saveTag));
    }
    @Test
    @WithUserDetails(value = "test",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("스터디 zone 폼")
    public void studyZoneForm() throws Exception{
        mockMvc.perform(get("/study/test/settings/zones"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("zones"))
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(view().name("study/settings/zones"));
    }
    @Test
    @WithUserDetails(value = "test",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("스터디 zone 저장")
    public void studyZoneAdd() throws Exception{
        ZoneForm zoneForm = new ZoneForm();
        zoneForm.setZoneName(testzone.toString());
        zoneRepository.save(testzone);
        mockMvc.perform(post("/study/test/settings/zones/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(zoneForm))
                        .with(csrf()))
                .andExpect(status().isOk());

        Study test = studyRepository.findStudyWithZonesByPath("test");
        assertTrue(test.getZones().contains(testzone));
    }
    @Test
    @WithUserDetails(value = "test",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("스터디 zone 삭제")
    public void studyZoneRemove() throws Exception{
        ZoneForm zoneForm = new ZoneForm();
        zoneForm.setZoneName(testzone.toString());
        Zone save = zoneRepository.save(testzone);
        Study test = studyRepository.findStudyWithZonesByPath("test");
        studyService.addZone(test,save);
        mockMvc.perform(post("/study/test/settings/zones/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(zoneForm))
                        .with(csrf()))
                .andExpect(status().isOk());

        test = studyRepository.findStudyWithZonesByPath("test");
        assertFalse(test.getZones().contains(testzone));
    }
    @Test
    @WithUserDetails(value = "test",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("스터디 study폼")
    public void studyStudyForm() throws Exception{
        mockMvc.perform(get("/study/test/settings/study"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"))
                .andExpect(view().name("study/settings/study"));
    }
    @Test
    @WithUserDetails(value = "test",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("스터디 study 등록")
    public void studyPublish() throws Exception{
        mockMvc.perform(post("/study/test/settings/study/publish")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("message"))
                .andExpect(redirectedUrl("/study/test/settings/study"));
        Study test = studyRepository.findStudyWithManagersByPath("test");
        assertTrue(test.isPublished());
    }
    @Test
    @WithUserDetails(value = "test",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("스터디 study 종료")
    public void studyClose() throws Exception{
        Study test = studyRepository.findStudyWithManagersByPath("test");
        test.publish();
        mockMvc.perform(post("/study/test/settings/study/close")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("message"))
                .andExpect(redirectedUrl("/study/test/settings/study"));
        test = studyRepository.findStudyWithManagersByPath("test");
        assertTrue(test.isClosed());
    }
    @Test
    @WithUserDetails(value = "test",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("스터디 팀원 모집")
    public void studyRecruit() throws Exception{
        Study test = studyRepository.findStudyWithManagersByPath("test");
        test.publish();
        mockMvc.perform(post("/study/test/settings/recruit/start")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("message"))
                .andExpect(redirectedUrl("/study/test/settings/study"));
        test = studyRepository.findStudyWithManagersByPath("test");
        assertTrue(test.isRecruiting());
    }
    @Test
    @WithUserDetails(value = "test",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("스터디 팀원 모집 종료")
    public void studyStopRecruit() throws Exception{
        Study test = studyRepository.findStudyWithManagersByPath("test");
        test.publish();
        test.setRecruiting(true);
        test.setPublishedDateTime(LocalDateTime.now().minusHours(2));
        mockMvc.perform(post("/study/test/settings/recruit/stop")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("message"))
                .andExpect(redirectedUrl("/study/test/settings/study"));
        test = studyRepository.findStudyWithManagersByPath("test");
        assertFalse(test.isRecruiting());
    }
    @Test
    @WithUserDetails(value = "test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("스터디 path 변경 성공")
    public void updateStudyPath_success() throws Exception{
        String changeTestPath_success = "change_path";
        mockMvc.perform(post("/study/test/settings/study/path")
                        .param("newPath",changeTestPath_success)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/"+changeTestPath_success+"/settings/study"));
        assertNull(studyRepository.findStudyWithManagersByPath("test"));
        assertNotNull(studyRepository.findStudyWithManagersByPath(changeTestPath_success));
    }
    @Test
    @WithUserDetails(value = "test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("스터디 path 변경 실패")
    public void updateStudyPath_fail() throws Exception{
        String changeTestPath_fail = "Fail";
        mockMvc.perform(post("/study/test/settings/study/path")
                        .param("newPath",changeTestPath_fail)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("studyPathError"));
        assertNotNull(studyRepository.findStudyWithManagersByPath("test"));
    }

    @Test
    @WithUserDetails(value = "test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("스터디 이름 변경 성공")
    public void updateStudyTitle_success() throws Exception{
        String changeTestTitle_success = "change_path";
        mockMvc.perform(post("/study/test/settings/study/title")
                        .param("newTitle",changeTestTitle_success)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/test/settings/study"));
        Study test = studyRepository.findStudyWithManagersByPath("test");
        assertEquals(changeTestTitle_success,test.getTitle());
    }
    @Test
    @WithUserDetails(value = "test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("스터디 이름 변경 실패")
    public void updateStudyTitle_fail() throws Exception{
        String changeTestTitle_fail = "*".repeat(51);
        mockMvc.perform(post("/study/test/settings/study/title")
                        .param("newTitle",changeTestTitle_fail)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("studyTitleError"));
        Study test = studyRepository.findStudyWithManagersByPath("test");
        assertEquals("test",test.getTitle());
    }
    @Test
    @WithUserDetails(value = "test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("스터디 제거")
    public void deleteStudyTitle_fail() throws Exception{
        mockMvc.perform(post("/study/test/settings/study/remove")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
        assertNull(studyRepository.findStudyWithManagersByPath("test"));
    }
}