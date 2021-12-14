package seo.study.studyspringapplication.modules.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import seo.study.studyspringapplication.modules.account.Account;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    public List<Notification> getcheckedNotifications(Account account, boolean checked) {
        return notificationRepository.findByAccountAndCheckedOrderByCreatedLocalDateTimeDesc(account,checked);
    }

    public void markAsRead(List<Notification> notCheckedNotifications) {
        notCheckedNotifications.forEach(notification->notification.setChecked(true));
        notificationRepository.saveAll(notCheckedNotifications);
    }

    public void deleteCheckedNotificatons(Account account) {
        notificationRepository.deleteByAccountAndChecked(account,true);
    }
}
