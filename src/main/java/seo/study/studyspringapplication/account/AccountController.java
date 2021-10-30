package seo.study.studyspringapplication.account;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import seo.study.studyspringapplication.domain.Account;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final SignUpFormVaildator signUpFormVaildator;
    private final AccountService accountService;
    // signUpForm을 받을 때
    // type의 camel case로 따라간다 SiginUpForm
    @InitBinder("signUpForm")
    public void initBinder(WebDataBinder webDataBinder){
        webDataBinder.addValidators(signUpFormVaildator);
    }

    @GetMapping("/sign-up")
    public String signUpForm(Model model){
        model.addAttribute("signUpForm",new SignUpForm());
        return "account/sign-up";
    }
    // 사용자가 클라이언트에서 뚫고 들어올수 있기 때문에 검증 한번 더 해야한다.
    // refactoring 너무 많은 역할을 가지고 있으므로 역할을 분리한다. 
    @PostMapping("/sign-up")
    public String createAccount(@Valid @ModelAttribute SignUpForm signUpForm, Errors errors){
        if(errors.hasErrors()){
            return "account/sign-up";
        }

        accountService.processNewAccount(signUpForm);
        return "redirect:/";
    }
}
