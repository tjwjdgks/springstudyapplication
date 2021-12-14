package seo.study.studyspringapplication.modules.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import seo.study.studyspringapplication.modules.account.Account;

import java.util.List;

@Transactional(readOnly = true)
public interface NotificationRepository extends JpaRepository<Notification,Long> {
    long countByAccountAndChecked(Account account,boolean checked);
    List<Notification> findByAccountAndCheckedOrderByCreatedLocalDateTimeDesc(Account account,boolean checked);
    List<Notification> findByAccount(Account account);
    void deleteByAccountAndChecked(Account account, boolean checked);
}
