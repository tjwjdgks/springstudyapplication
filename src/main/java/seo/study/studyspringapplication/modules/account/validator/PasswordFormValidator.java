package seo.study.studyspringapplication.modules.account.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import seo.study.studyspringapplication.modules.account.form.PasswordForm;

public class PasswordFormValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return PasswordForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PasswordForm passwordForm = (PasswordForm) target;
        if(!passwordForm.getNewPassword().equals(passwordForm.getNewPasswordConfirm())){
            errors.rejectValue("newPassword","wrong.value","입력한 새 패스워드가 일치하지 않습니다.");
        }
    }
}
