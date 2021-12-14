package seo.study.studyspringapplication.modules.study.event;

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
import seo.study.studyspringapplication.modules.account.AccountPredicates;
import seo.study.studyspringapplication.modules.account.AccountRepository;
import seo.study.studyspringapplication.modules.notification.Notification;
import seo.study.studyspringapplication.modules.notification.NotificationRepository;
import seo.study.studyspringapplication.modules.notification.NotificationType;
import seo.study.studyspringapplication.modules.study.Study;
import seo.study.studyspringapplication.modules.study.StudyRepository;

import java.time.LocalDateTime;

@Slf4j
@Async
@Component
@Transactional
@RequiredArgsConstructor
public class StudyEventListener {

    private final StudyRepository studyRepository;
    private final AccountRepository accountRepository;
    private final EmailService emailService;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;
    private final NotificationRepository notificationRepository;
    @EventListener
    public void handleStudyCreatedEvent(StudyCreatedEvent studyCreatedEvent){
        // detached 객체이며 study를 findStudyWithManagersByPath로 조회했기 때문에 Zone과 tag는 null값이다
        Study study = studyRepository.findStudyWithTagsAndZonesById(studyCreatedEvent.getStudy().getId());
        Iterable<Account> accounts = accountRepository
                .findAll(AccountPredicates.findByTagsAndZones(study.getTags(), study.getZones()));
        accounts.forEach(account -> {
                    if(account.isStudyCreatedByEmail()){
                        sendStuydyCreatedEmail(study, account);
                    }
                    if(account.isStudyCreatedByWeb()){
                        saveStudyCreatedNotification(study, account);
                    }
        });
    }

    private void saveStudyCreatedNotification(Study study, Account account) {
        Notification notification = new Notification();
        notification.setTitle(study.getTitle());
        notification.setLink("/study/"+ study.getEncodePath());
        notification.setChecked(false);
        notification.setCreatedLocalDateTime(LocalDateTime.now());
        notification.setMessage(study.getShortDescription());
        notification.setAccount(account);
        notification.setNotificationType(NotificationType.STUDY_CREATED);
        notificationRepository.save(notification);
    }

    private void sendStuydyCreatedEmail(Study study, Account account) {
        Context context = new Context();
        context.setVariable("nickname", account.getNickname());
        context.setVariable("link","/study/"+ study.getEncodePath());
        context.setVariable("linkName", study.getTitle());
        context.setVariable("message","새로운 스터디가 생겼습니다");
        context.setVariable("host",appProperties.getHost());
        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .subject("스터디," + study.getTitle() + "스터디가 생겼습니다.")
                .to(account.getEmail())
                .message(message)
                .build();
        emailService.sendEmail(emailMessage);
    }
}
