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
import java.util.HashSet;
import java.util.Set;

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
                        sendStudyMail(study, account,"새로운 스터디가 생겼습니다",
                                "스터디," + study.getTitle() + "스터디가 생겼습니다.");
                    }
                    if(account.isStudyCreatedByWeb()){
                        createNotification(study, account,study.getShortDescription(),NotificationType.STUDY_CREATED);
                    }
        });
    }

    @EventListener
    public void handleStudyUpdateEvent(StudyUpdateEvent studyUpdateEvent){
        Study study = studyRepository.findStudyWithManagersAndMembersById(studyUpdateEvent.getStudy().getId());
        Set<Account> accounts = new HashSet<>();
        accounts.addAll(study.getManagers());
        accounts.addAll(study.getMembers());

        accounts.forEach(account -> {
            if(account.isStudyCreatedByEmail()){
                sendStudyMail(study, account, studyUpdateEvent.getMessage(),study.getTitle()+" 스터디에 새소식이 있습니다");
            }
            if(account.isStudyCreatedByWeb()){
                createNotification(study, account,studyUpdateEvent.getMessage(),NotificationType.STUDY_UPDATED);
            }
        });


    }


    private void createNotification(Study study, Account account, String message, NotificationType notificationType) {
        Notification notification = new Notification();
        notification.setTitle(study.getTitle());
        notification.setLink("/study/"+ study.getEncodePath());
        notification.setChecked(false);
        notification.setCreatedLocalDateTime(LocalDateTime.now());
        notification.setMessage(message);
        notification.setAccount(account);
        notification.setNotificationType(notificationType);
        notificationRepository.save(notification);
    }

    private void sendStudyMail(Study study, Account account, String contextMessage, String emailSubject) {
        Context context = new Context();
        context.setVariable("nickname", account.getNickname());
        context.setVariable("link","/study/"+ study.getEncodePath());
        context.setVariable("linkName", study.getTitle());
        context.setVariable("message",contextMessage);
        context.setVariable("host",appProperties.getHost());
        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .subject(emailSubject)
                .to(account.getEmail())
                .message(message)
                .build();
        emailService.sendEmail(emailMessage);
    }
}
