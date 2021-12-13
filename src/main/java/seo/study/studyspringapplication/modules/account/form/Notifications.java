package seo.study.studyspringapplication.modules.account.form;

import lombok.Data;

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

