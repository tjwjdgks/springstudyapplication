package seo.study.studyspringapplication.study;


import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import seo.study.studyspringapplication.domain.Account;
import seo.study.studyspringapplication.domain.Study;
import seo.study.studyspringapplication.study.form.StudyDescriptionForm;

@Service
@Transactional // 모든 퍼블릭 메소드에 Transactional 효과
@RequiredArgsConstructor
public class StudyService {

    private final StudyRepository repository;
    private final ModelMapper modelMapper;

    public Study creatNewStudy(Study study, Account account) {
        Study newStudy = repository.save(study);
        newStudy.addManager(account);
        return newStudy;
    }
    public Study getStudyToUpdate(Account account, String path){
        Study study = this.getStudy(path);
        if(!account.isManagerOf(study)){
            throw new AccessDeniedException("해당 기능을 사용할 수 없습니다");
        }
        return study;
    }
    public Study getStudy(String path){
        Study study = this.repository.findByPath(path);
        if(study == null){
            throw new IllegalArgumentException(path+"에 해당하는 스터디가 없습니다");
        }
        return study;
    }

    public void updateStudyDescription(Study study, StudyDescriptionForm studyDescriptionForm) {
        modelMapper.map(studyDescriptionForm, study);
    }

    public void enableStudyBanner(Study study) {
        study.setUseBanner(true);
    }

    public void disableStudyBanner(Study study) {
        study.setUseBanner(false);
    }

    public void updateStudyImage(Study study, String image) {
        study.setImage(image);
    }
}
