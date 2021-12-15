package seo.study.studyspringapplication.modules.event.event;

import seo.study.studyspringapplication.modules.event.Enrollment;

public class EnrollmentAcceptedEvent extends EnrollmentEvent {
    public EnrollmentAcceptedEvent(Enrollment enrollment) {
        super(enrollment, "모임 참가 신청이 확정되었습니다");
    }
}
