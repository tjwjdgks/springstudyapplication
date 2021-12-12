package seo.study.studyspringapplication.event;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import seo.study.studyspringapplication.domain.Account;
import seo.study.studyspringapplication.domain.Event;
import seo.study.studyspringapplication.domain.Study;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    public Event createEvent(Study study, Event event, Account account) {
        event.setCreatedBy(account);
        event.setCreateDateTime(LocalDateTime.now());
        event.setStudy(study);
        return eventRepository.save(event);
    }
}
