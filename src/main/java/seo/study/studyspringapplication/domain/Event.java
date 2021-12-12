package seo.study.studyspringapplication.domain;

import lombok.*;
import seo.study.studyspringapplication.account.UserAccount;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
public class Event {

    @Id @GeneratedValue
    private Long Id;

    @ManyToOne
    private Study study;

    @ManyToOne
    private Account createdBy;

    @Column(nullable = false)
    private String title;

    @Lob
    private String description;

    @Column(nullable = false)
    private LocalDateTime createDateTime;

    @Column(nullable = false)
    private LocalDateTime endEnrollmentDateTime;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;

    @Column
    private Integer limitOfEnrollments;

    // 연관관계의 주인이 아니기 때문에 값을 변경해도 DB 반영 X, 연관 관계 주인 "event" 과 관련이 있음을 알려준다
    @OneToMany(mappedBy = "event")
    private List<Enrollment> enrollments;

    @Enumerated(value = EnumType.STRING)
    private EventType eventType;
    public boolean isEnrollableFor(UserAccount user){
        return isNotClosed() && !isAlreadyEnrolled(user);
    }
    public boolean isDisenrollableFor(UserAccount user){
        return isNotClosed() && isAlreadyEnrolled(user);
    }
    private boolean isAlreadyEnrolled(UserAccount user) {
        Account account = user.getAccount();
        for(Enrollment e : this.enrollments){
            if(e.getAccount().equals(account))
                return true;
        }
        return false;
    }
    public boolean isAttended(UserAccount user){
        Account account = user.getAccount();
        for (Enrollment e : this.enrollments) {
            if (e.getAccount().equals(account) && e.isAttended()) {
                return true;
            }
        }
        return false;
    }
    private boolean isNotClosed(){
        return this.endEnrollmentDateTime.isAfter(LocalDateTime.now());
    }

}
