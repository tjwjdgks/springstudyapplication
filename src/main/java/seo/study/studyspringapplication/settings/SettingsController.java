package seo.study.studyspringapplication.settings;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import seo.study.studyspringapplication.account.AccountService;
import seo.study.studyspringapplication.account.CurrentUser;
import seo.study.studyspringapplication.domain.Account;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class SettingsController {
    // 데이터 변경은 서비스에 위임한다
    private final AccountService accountService;


    @GetMapping("/settings/profile")
    public String profileUpdateForm(@CurrentUser Account account, Model model){
        model.addAttribute(account);
        model.addAttribute(new Profile(account));

        return "settings/profile";
    }
    // 사용하는 Account 객체는 session에 있는 상태 현재 영속성 상태 detach
    @PostMapping("/settings/profile")
    public String updateProfile(@CurrentUser Account account, @Valid @ModelAttribute Profile profile, Errors errors,
                                Model model, RedirectAttributes attributes){
        if(errors.hasErrors()){
            // model에 form을 채운 data와 errors의 정보도 자동으로 들어간다
            model.addAttribute(account);
            return "settings/profile";
        }
        accountService.updateProfile(account,profile);
        attributes.addFlashAttribute("message","프로필을 수정했습니다.");
        return "redirect:/settings/profile";
    }
}
