package seo.study.studyspringapplication.modules.event.event;

import seo.study.studyspringapplication.modules.event.Enrollment;

public class EnrollmentRejectedEvent extends EnrollmentEvent {

    public EnrollmentRejectedEvent(Enrollment enrollment) {
        super(enrollment, "모임 참가 신청이 거절되었습니다");
    }
}
