package seo.study.studyspringapplication.event;

import org.springframework.data.jpa.repository.JpaRepository;
import seo.study.studyspringapplication.domain.Account;
import seo.study.studyspringapplication.domain.Enrollment;
import seo.study.studyspringapplication.domain.Event;

public interface EnrollmentRepository extends JpaRepository<Enrollment,Long> {
    boolean existsByEventAndAccount(Event event, Account account);
    Enrollment findByEventAndAccount(Event event,Account account);
}
