package seo.study.studyspringapplication.modules.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import seo.study.studyspringapplication.modules.account.Account;
import seo.study.studyspringapplication.modules.study.Study;

import java.time.LocalDateTime;

@Component
public class EventFactory {
    @Autowired EventService eventService;

    public Event createEvent(String eventTitle, EventType eventType, int limit, Study study, Account account) {
        Event event = new Event();
        event.setEventType(eventType);
        event.setLimitOfEnrollments(limit);
        event.setTitle(eventTitle);
        event.setCreateDateTime(LocalDateTime.now());
        event.setEndEnrollmentDateTime(LocalDateTime.now().plusDays(1));
        event.setStartDateTime(LocalDateTime.now().plusDays(1).plusHours(5));
        event.setEndDateTime(LocalDateTime.now().plusDays(1).plusHours(7));
        return eventService.createEvent(study,event, account);
    }
}
