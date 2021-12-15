package seo.study.studyspringapplication.modules.event.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import seo.study.studyspringapplication.modules.event.Enrollment;

@RequiredArgsConstructor
@Getter
public class EnrollmentEvent {

    protected final Enrollment enrollment;
    protected final String message;
}
