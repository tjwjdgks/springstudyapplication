package seo.study.studyspringapplication.event;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import seo.study.studyspringapplication.domain.Event;
import seo.study.studyspringapplication.domain.Study;

import java.util.List;

@Transactional(readOnly = true)
public interface EventRepository extends JpaRepository<Event, Long>{
    @EntityGraph(value = "Event.withEnrollments",type = EntityGraph.EntityGraphType.LOAD)
    List<Event> findByStudyOrderByStartDateTime(Study study);
}
