package seo.study.studyspringapplication.account;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AccountController {
    @GetMapping("/sign-up")
    public String signUpForm(Model model){
        return "account/sign-up";
    }
    @PostMapping("/sign-up")
    public String createAccount(){
        return "account/sign-up";
    }
}
