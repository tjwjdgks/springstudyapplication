package seo.study.studyspringapplication.modules.study.event;

import lombok.Data;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import seo.study.studyspringapplication.modules.study.Study;

@Getter
// applicationEvent 상속 받지 않아도 된다 // custom object 이벤트 핸들링 제공해준다
public class StudyCreatedEvent {

    private Study study;

    public StudyCreatedEvent(Study study) {
        this.study = study;
    }
}
