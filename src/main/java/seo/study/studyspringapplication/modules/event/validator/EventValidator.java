package seo.study.studyspringapplication.modules.event.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import seo.study.studyspringapplication.modules.event.Event;
import seo.study.studyspringapplication.modules.event.form.EventForm;

import java.time.LocalDateTime;

// 날짜들은 상관 관계가 있으므로 vaildation을 더 해주어야 한다
// 날짜 유효성 체크
// bean으로 등록
@Component
public class EventValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return EventForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        EventForm eventForm = (EventForm) target;
        // 접수 종료 시간은 현재 시간 보다 이전이면 안된다
        if(eventForm.getEndEnrollmentDateTime().isBefore(LocalDateTime.now())){
             errors.rejectValue("endEnrollmentDateTime","wrong.dateTime","모임 접수 종료 일시를 정확히 입력하세요.");
        }
        // 이벤트 종료 시간이 시작 시간보더 이전이면 안되며 이벤트 종료 시간이 이벤트 접수 시간보다 이전이면 안된다
        if(eventForm.getEndDateTime().isBefore(eventForm.getStartDateTime())
                || eventForm.getEndDateTime().isBefore(eventForm.getEndEnrollmentDateTime())){
            errors.rejectValue("endDateTime","wrong.dateTime","모임 종료 일시를 정확히 입력하세요.");
        }
        // 이벤트 시작 시간이 이벤트 접수 시간보다 이전이면 안된다
        if(eventForm.getStartDateTime().isBefore(eventForm.getEndEnrollmentDateTime())){
            errors.rejectValue("startDateTime","wrong.dateTime","모임 시작 일시를 정확히 입력하세요.");
        }
    }

    public void validateUpdateForm(EventForm eventForm, Event event, Errors errors) {
        if(eventForm.getLimitOfEnrollments()< event.getLimitOfEnrollments())
            errors.rejectValue("limitOfEnrollments","wrong.value","확인된 참가 신청보다 모집 인원수가 커야 합니다.");
    }
}
