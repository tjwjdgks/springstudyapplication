package seo.study.studyspringapplication.settings.vaildator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import seo.study.studyspringapplication.account.AccountRepository;
import seo.study.studyspringapplication.domain.Account;
import seo.study.studyspringapplication.settings.form.NicknameForm;

@Component
@RequiredArgsConstructor
public class NicknameValidator implements Validator {

    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return NicknameForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        NicknameForm nicknameForm = (NicknameForm) target;
        Account byNickname = accountRepository.findByNickname(nicknameForm.getNickname());
        if(byNickname != null){
            errors.rejectValue("nickname","wrong.value","입력하신 닉네임을 사용할 수 없습니다");
        }
    }
}
