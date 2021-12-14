package seo.study.studyspringapplication.modules.notification;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import seo.study.studyspringapplication.modules.account.Account;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@EqualsAndHashCode(of = "id")
public class Notification {

    @Id @GeneratedValue
    private Long id;

    private String title;

    private String link;

    private String message;

    private boolean checked;

    @ManyToOne
    private Account account;

    private LocalDateTime createdLocalDateTime;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;
}
