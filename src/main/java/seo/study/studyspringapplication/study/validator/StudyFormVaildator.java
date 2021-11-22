package seo.study.studyspringapplication.study.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import seo.study.studyspringapplication.study.StudyRepository;
import seo.study.studyspringapplication.study.form.StudyForm;

@Component
@RequiredArgsConstructor
public class StudyFormVaildator implements Validator {

    private final StudyRepository studyRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return StudyForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        StudyForm studyForm = (StudyForm) target;
        if(studyRepository.existsByPath(studyForm.getPath())){
            errors.rejectValue("path","wrong.path","스터디 경로를 사용할 수 없습니다.");
        }
    }
}
