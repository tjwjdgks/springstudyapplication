package seo.study.studyspringapplication.modules.event;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import seo.study.studyspringapplication.modules.account.Account;

import java.util.List;

@Transactional(readOnly = true)
public interface EnrollmentRepository extends JpaRepository<Enrollment,Long> {
    boolean existsByEventAndAccount(Event event, Account account);
    Enrollment findByEventAndAccount(Event event,Account account);

    @EntityGraph("Enrollment.withEventAndStudy")
    List<Enrollment> findEventAndStudyWithEnrollmentByAccountAndAcceptedOrderByEnrolledAtDesc(Account account, boolean accepted);
}
