package seo.study.studyspringapplication.settings.form;

import lombok.Data;
import lombok.NoArgsConstructor;
import seo.study.studyspringapplication.domain.Account;

@Data
//@NoArgsConstructor
// 현재 modelmapper 사용
public class Notifications {
    private boolean studyCreatedByEmail;

    private boolean studyCreatedByWeb;

    private boolean studyEnrollmentResultByEmail;

    private boolean studyEnrollmentResultByWeb;

    private boolean studyUpdatedByEmail;

    private boolean studyUpdatedByWeb;

}

