package seo.study.studyspringapplication.modules.study;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import seo.study.studyspringapplication.modules.account.Account;

@Component
public class StudyFactory {
    @Autowired StudyService studyService;
    @Autowired StudyRepository studyRepository;

    public Study createStudy(String path, Account manager){
        Study study = new Study();
        study.setPath(path);
        studyService.creatNewStudy(study, manager);
        return study;
    }
}
