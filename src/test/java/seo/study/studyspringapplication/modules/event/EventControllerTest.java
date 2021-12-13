package seo.study.studyspringapplication.modules.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import seo.study.studyspringapplication.infra.MockMvcTest;
import seo.study.studyspringapplication.modules.account.Account;
import seo.study.studyspringapplication.modules.account.AccountFactory;
import seo.study.studyspringapplication.modules.account.AccountRepository;
import seo.study.studyspringapplication.modules.account.WithAccount;
import seo.study.studyspringapplication.modules.event.form.EventForm;
import seo.study.studyspringapplication.modules.study.Study;
import seo.study.studyspringapplication.modules.study.StudyFactory;
import seo.study.studyspringapplication.modules.study.StudyService;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockMvcTest
class EventControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private AccountRepository accountRepository;
    @Autowired private EventService eventService;
    @Autowired private EnrollmentRepository enrollmentRepository;
    @Autowired private ModelMapper modelMapper;
    @Autowired private EventFactory eventFactory;
    @Autowired private StudyFactory studyFactory;
    @Autowired private AccountFactory accountFactory;

    @Test
    @DisplayName("선착순 모임에 참가 신청- 자동 수락")
    @WithAccount("test")
    void newEnrollment_to_FCFS_event_accpted() throws Exception{
        Account user1 = accountFactory.createAccount("user1");
        Study study = studyFactory.createStudy("test-study",user1);
        Event event = eventFactory.createEvent("test-event", EventType.FCFS, 2, study, user1);

        mockMvc.perform(post("/study/"+study.getPath()+"/events/"+event.getId()+"/enroll")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/"+study.getPath()+"/events/"+event.getId()));

        Account test = accountRepository.findByNickname("test");
        isAccepted(test,event);
    }

    @Test
    @DisplayName("선착순 모임에 참가 신청 - 대기중 (인원 초과 경우)")
    @WithAccount("test")
    void newEnrollment_to_FCFS_event_waiting() throws Exception{
        Account user1 = accountFactory.createAccount("user1");
        Study study = studyFactory.createStudy("test-study",user1);
        Event event = eventFactory.createEvent("test-event", EventType.FCFS, 2, study, user1);

        Account user2 = accountFactory.createAccount("user2");
        eventService.newEnrollment(event,user1);
        eventService.newEnrollment(event,user2);

        mockMvc.perform(post("/study/"+study.getPath()+"/events/"+event.getId()+"/enroll")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/"+study.getPath()+"/events/"+event.getId()));

        Account test = accountRepository.findByNickname("test");
        isNotAccepted(test,event);
    }
    @Test
    @DisplayName("참가 신청 확정자가 선착순 모임에 참가 신청 취소하는 경우, 대기자 자동 신청")
    @WithAccount("test")
    void accepted_auto_when_acceptUser_canceled() throws Exception {
        Account user1 = accountFactory.createAccount("user1");
        Account acceptedUser = accountFactory.createAccount("user2");
        Account test = accountRepository.findByNickname("test");

        Study study = studyFactory.createStudy("test-study",user1);
        Event event = eventFactory.createEvent("test-event", EventType.FCFS, 2, study, user1);

        eventService.newEnrollment(event,user1);
        eventService.newEnrollment(event,test);

        eventService.newEnrollment(event,acceptedUser);

        isNotAccepted(acceptedUser,event);

        mockMvc.perform(post("/study/"+study.getPath()+"/events/"+event.getId()+"/leave")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/"+study.getPath()+"/events/"+event.getId()));

        isAccepted(acceptedUser,event);
        assertNull(enrollmentRepository.findByEventAndAccount(event,test));
    }
    @Test
    @DisplayName("참가 신청 비확정자가 선착순 모임에 참가 신청 취소하는 경우, 변경 없음")
    @WithAccount("test")
    void accepted_auto_when_no_acceptUser_canceled() throws Exception {
        Account user1 = accountFactory.createAccount("user1");
        Account acceptedUser = accountFactory.createAccount("user2");
        Account test = accountRepository.findByNickname("test");

        Study study = studyFactory.createStudy("test-study",user1);
        Event event = eventFactory.createEvent("test-event", EventType.FCFS, 2, study, user1);

        eventService.newEnrollment(event,acceptedUser);
        eventService.newEnrollment(event,user1);

        eventService.newEnrollment(event,test);


        isNotAccepted(test,event);

        mockMvc.perform(post("/study/"+study.getPath()+"/events/"+event.getId()+"/leave")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/"+study.getPath()+"/events/"+event.getId()));

        isAccepted(acceptedUser,event);
        isAccepted(user1,event);
        assertNull(enrollmentRepository.findByEventAndAccount(event,test));
    }

    @Test
    @DisplayName("관리자 모임에 참가 신청- 대기중 ")
    @WithAccount("test")
    void newEnrollment_to_CORNFIRMATIVE_event_accpted() throws Exception{
        Account user1 = accountFactory.createAccount("user1");
        Study study = studyFactory.createStudy("test-study",user1);
        Event event = eventFactory.createEvent("test-event", EventType.CONFIRMATIVE, 2, study, user1);

        mockMvc.perform(post("/study/"+study.getPath()+"/events/"+event.getId()+"/enroll")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/"+study.getPath()+"/events/"+event.getId()));

        Account test = accountRepository.findByNickname("test");
        isNotAccepted(test,event);
    }
    @Test
    @DisplayName("참가 인원 변경시- 대기 중 자동 수락")
    @WithAccount("test")
    void changeEnrollment_then_accpeted_auto() throws Exception{
        Account test = accountRepository.findByNickname("test");
        Study study = studyFactory.createStudy("test-study", test);
        Event event = eventFactory.createEvent("test-event", EventType.FCFS, 2, study, test);

        Account user1 = accountFactory.createAccount("user1");
        Account user2 = accountFactory.createAccount("user2");

        eventService.newEnrollment(event,test);
        eventService.newEnrollment(event,user1);
        eventService.newEnrollment(event,user2);

        isAccepted(test,event);
        isAccepted(user1,event);
        isNotAccepted(user2,event);

        EventForm eventForm = modelMapper.map(event,EventForm.class);
        eventForm.setLimitOfEnrollments(4);

        mockMvc.perform(post("/study/"+study.getPath()+"/events/"+event.getId()+"/edit")
                        .param("title",eventForm.getTitle())
                        .param("eventType", EventType.FCFS.toString())
                        .param("endEnrollmentDateTime",eventForm.getEndEnrollmentDateTime().toString())
                        .param("startDateTime",eventForm.getStartDateTime().toString())
                        .param("endDateTime",eventForm.getEndDateTime().toString())
                        .param("limitOfEnrollments",eventForm.getLimitOfEnrollments().toString())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        isAccepted(user2,event);

    }
    private void isAccepted(Account account, Event event) {
        assertTrue(enrollmentRepository.findByEventAndAccount(event, account).isAccepted());
    }
    private void isNotAccepted(Account account, Event event) {
        assertFalse(enrollmentRepository.findByEventAndAccount(event, account).isAccepted());
    }

}