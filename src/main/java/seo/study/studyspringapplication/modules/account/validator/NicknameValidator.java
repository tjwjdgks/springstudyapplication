package seo.study.studyspringapplication.modules.account.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import seo.study.studyspringapplication.modules.account.AccountRepository;
import seo.study.studyspringapplication.modules.account.Account;
import seo.study.studyspringapplication.modules.account.form.NicknameForm;

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
