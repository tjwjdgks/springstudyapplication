package seo.study.studyspringapplication.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import seo.study.studyspringapplication.domain.Event;

@Transactional(readOnly = true)
public interface EventRepository extends JpaRepository<Event, Long>{
}
