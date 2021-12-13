package seo.study.studyspringapplication.modules.event;

import lombok.*;
import seo.study.studyspringapplication.modules.account.Account;
import seo.study.studyspringapplication.modules.account.UserAccount;
import seo.study.studyspringapplication.modules.study.Study;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.function.Predicate.not;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@NamedEntityGraph(name = "Event.withEnrollments",
        attributeNodes = @NamedAttributeNode(value = "enrollments")
)
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
    private List<Enrollment> enrollments = new ArrayList<>();

    @Enumerated(value = EnumType.STRING)
    private EventType eventType;
    public boolean isEnrollableFor(UserAccount user){
        return isNotClosed() && !isAttended(user) && !isAlreadyEnrolled(user);
    }
    public boolean isDisenrollableFor(UserAccount user){
        return isNotClosed() && !isAttended(user) && isAlreadyEnrolled(user);
    }
    private boolean isAlreadyEnrolled(UserAccount user) {
        final Account account = user.getAccount();
        return enrollments.stream()
                .anyMatch(enrollment -> enrollment.getAccount().equals(account));
    }
    public boolean isAttended(UserAccount user){
        final Account account = user.getAccount();
        return enrollments.stream().
               anyMatch(enrollment -> enrollment.getAccount().equals(account) && enrollment.isAttended());
    }
    public long numberOfRemainSpots(){
        return this.limitOfEnrollments - this.enrollments.stream().filter(Enrollment::isAccepted).count();
    }
    public boolean canAcceptable(Enrollment enrollment){
        return this.eventType == EventType.CONFIRMATIVE
                && this.enrollments.contains(enrollment)
                && !enrollment.isAttended()
                && !enrollment.isAccepted();
    }
    public boolean canRejectable(Enrollment enrollment){
        return this.eventType == EventType.CONFIRMATIVE
                && this.enrollments.contains(enrollment)
                && !enrollment.isAttended()
                && enrollment.isAccepted();
    }
    private boolean isNotClosed(){
        return this.endEnrollmentDateTime.isAfter(LocalDateTime.now());
    }

    public void addEnrollment(Enrollment enrollment) {
        this.enrollments.add(enrollment);
        enrollment.setEvent(this);
    }

    public boolean isAbleToAcceptWaitingEnrollment() {
        return this.eventType == EventType.FCFS && numberOfRemainSpots()>0;
    }

    public void removeEnrollment(Enrollment enrollment) {
        this.enrollments.remove(enrollment);
        enrollment.setEvent(null);
    }

    public void acceptNextWaitingEnrollment() {
        if(this.isAbleToAcceptWaitingEnrollment()){
            enrollments.stream().filter(not(Enrollment::isAccepted))
                    .findFirst()
                    .ifPresent(enrollment -> enrollment.setAccepted(true));
        }
    }

    public void acceptWaitingList() {
        if(this.isAbleToAcceptWaitingEnrollment()){
            final long numberOfRemainSpots = numberOfRemainSpots();
            enrollments.stream().filter(not(Enrollment::isAccepted))
                    .limit(numberOfRemainSpots)
                    .forEach(enrollment -> enrollment.setAccepted(true));
        }
    }

    public void accept(Enrollment enrollment) {
        if(this.eventType == EventType.CONFIRMATIVE && this.numberOfRemainSpots()>0){
            enrollment.setAccepted(true);
        }
    }

    public void reject(Enrollment enrollment) {
        if(this.eventType == EventType.CONFIRMATIVE){
            enrollment.setAccepted(false);
        }
    }
}
