package seo.study.studyspringapplication.account;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor // private final type 생성자 만들어준다
public class SignUpFormVaildator implements Validator {

    // field 주입보다는 생성자 주입을 사용해라
    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        // SignUpForm 인스턴스 검사
        return clazz.isAssignableFrom(SignUpForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {

        SignUpForm signUpForm = (SignUpForm) target;
        if(accountRepository.existsByEmail(signUpForm.getEmail())){
            errors.rejectValue("email","invalid.email", new Object[]{signUpForm.getEmail()},"이미 사용중인 이메일 입니다");
        }
        if(accountRepository.existsByNickname(signUpForm.getEmail())){
            errors.rejectValue("email","invalid.email", new Object[]{signUpForm.getNickname()},"이미 사용중인 닉네임 입니다");
        }

    }
}
