package seo.study.studyspringapplication.modules.event;

import org.springframework.data.jpa.repository.JpaRepository;
import seo.study.studyspringapplication.modules.account.Account;

public interface EnrollmentRepository extends JpaRepository<Enrollment,Long> {
    boolean existsByEventAndAccount(Event event, Account account);
    Enrollment findByEventAndAccount(Event event,Account account);
}
