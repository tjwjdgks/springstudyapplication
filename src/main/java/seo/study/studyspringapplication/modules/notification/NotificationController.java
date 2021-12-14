package seo.study.studyspringapplication.modules.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import seo.study.studyspringapplication.modules.account.Account;
import seo.study.studyspringapplication.modules.account.CurrentUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.*;

@Controller
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;

    @GetMapping("/notifications")
    public String getNotifications(@CurrentUser Account account, Model model){
        List<Notification> notCheckedNotifications = notificationService.getcheckedNotifications(account,false);
        long numberOfChecked = notificationRepository.countByAccountAndChecked(account,true);
        modelAddAttributeWithCategorization(model,notCheckedNotifications,numberOfChecked,notCheckedNotifications.size());
        model.addAttribute("isNew",true);
        notificationService.markAsRead(notCheckedNotifications);
        return "notification/list";
    }

    @GetMapping("/notifications/old")
    public String getOldNotifications(@CurrentUser Account account, Model model){
        List<Notification> checkedNotifications = notificationService.getcheckedNotifications(account,true);
        long numberOfNotChecked = notificationRepository.countByAccountAndChecked(account,false);
        modelAddAttributeWithCategorization(model,checkedNotifications,checkedNotifications.size(),numberOfNotChecked);
        model.addAttribute("isNew",false);
        return "notification/list";
    }
    @DeleteMapping("/notifications")
    public String deleteNotifications(@CurrentUser Account account, Model model){
        notificationService.deleteCheckedNotificatons(account);
        return "redirect:/notifications";
    }

    private void modelAddAttributeWithCategorization(Model model, List<Notification> notifications, long numberOfChecked, long numberOfNotChecked) {
        Map<NotificationType, List<Notification>> notificationTypeListMap = notifications.stream()
                .collect(groupingBy(Notification::getNotificationType));

        for(NotificationType type : NotificationType.values())
            if(!notificationTypeListMap.containsKey(type))
                notificationTypeListMap.put(type,new ArrayList<Notification>());

        model.addAttribute("numberOfChecked",numberOfChecked);
        model.addAttribute("numberOfNotChecked",numberOfNotChecked);
        model.addAttribute("notifications",notifications);
        model.addAttribute("newStudyNotifications",notificationTypeListMap.get(NotificationType.STUDY_CREATED));
        model.addAttribute("eventEnrollmentNotifications",notificationTypeListMap.get(NotificationType.EVENT_ENROLLMENT));
        model.addAttribute("watchingStudyNotifications",notificationTypeListMap.get(NotificationType.STUDY_UPDATED));

    }
}
