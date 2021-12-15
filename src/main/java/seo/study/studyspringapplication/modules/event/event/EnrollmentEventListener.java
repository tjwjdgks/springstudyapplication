package seo.study.studyspringapplication.modules.event.event;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import seo.study.studyspringapplication.infra.config.AppProperties;
import seo.study.studyspringapplication.infra.mail.EmailMessage;
import seo.study.studyspringapplication.infra.mail.EmailService;
import seo.study.studyspringapplication.modules.account.Account;
import seo.study.studyspringapplication.modules.event.Enrollment;
import seo.study.studyspringapplication.modules.event.Event;
import seo.study.studyspringapplication.modules.notification.Notification;
import seo.study.studyspringapplication.modules.notification.NotificationRepository;
import seo.study.studyspringapplication.modules.notification.NotificationType;
import seo.study.studyspringapplication.modules.study.Study;

import java.time.LocalDateTime;

@Slf4j
@Component
@Transactional
@Async
@RequiredArgsConstructor
public class EnrollmentEventListener {

    private final AppProperties appProperties;
    private final TemplateEngine templateEngine;
    private final EmailService emailService;
    private final NotificationRepository notificationRepository;

    @EventListener
    public void handleEnrollmentEvent(EnrollmentEvent enrollmentEvent){
        Enrollment enrollment = enrollmentEvent.getEnrollment();
        Account account = enrollment.getAccount();
        Event event = enrollment.getEvent();
        Study study = event.getStudy();

        if(account.isStudyEnrollmentResultByEmail()){
            sendEnrollmentMail(account, study,event,enrollmentEvent);
        }
        if(account.isStudyEnrollmentResultByWeb()){
            createEnrollmentNotifiacation(account,event,study, enrollmentEvent);
        }
    }

    private void createEnrollmentNotifiacation(Account account, Event event, Study study, EnrollmentEvent enrollmentEvent) {
        Notification notification = new Notification();
        notification.setTitle(study.getTitle() + " / " + event.getTitle());
        notification.setLink("/study/" + study.getEncodePath() + "/events/" + event.getId());
        notification.setChecked(false);
        notification.setCreatedLocalDateTime(LocalDateTime.now());
        notification.setMessage(enrollmentEvent.getMessage());
        notification.setAccount(account);
        notification.setNotificationType(NotificationType.EVENT_ENROLLMENT);
        notificationRepository.save(notification);

    }

    private void sendEnrollmentMail(Account account, Study study, Event event, EnrollmentEvent enrollmentEvent) {
        Context context = new Context();
        context.setVariable("nickname",account.getNickname());
        context.setVariable("message",enrollmentEvent.getMessage());
        context.setVariable("host",appProperties.getHost());
        context.setVariable("link", "/study/" + study.getEncodePath() + "/events/" + event.getId());
        context.setVariable("linkName",study.getTitle());

        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .subject(event.getTitle()+ " 모임 참가 결과 입니다")
                .to(account.getEmail())
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);
    }
}
