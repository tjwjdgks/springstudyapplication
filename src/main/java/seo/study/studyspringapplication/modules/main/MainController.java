package seo.study.studyspringapplication.modules.main;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import seo.study.studyspringapplication.modules.account.CurrentUser;
import seo.study.studyspringapplication.modules.account.Account;

@Controller
public class MainController {

    @GetMapping("/")
    public String home(@CurrentUser Account account, Model model){
        if(account != null){
            model.addAttribute(account);
        }
        return "index";
    }
    @GetMapping("/login")
    public String login(){
        return "login";
    }
}
