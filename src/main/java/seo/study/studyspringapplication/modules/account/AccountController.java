package seo.study.studyspringapplication.modules.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import seo.study.studyspringapplication.modules.account.form.SignUpForm;
import seo.study.studyspringapplication.modules.account.validator.SignUpFormVaildator;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final SignUpFormVaildator signUpFormVaildator;
    private final AccountService accountService;
    private final AccountRepository accountRepository;

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

        Account account = accountService.processNewAccount(signUpForm);
        accountService.login(account);
        return "redirect:/";
    }

    @GetMapping("/check-email-token")
    public String checkEmailToken(String token, String email, Model model){

        Account account = accountRepository.findByEmail(email);
        String view = "/account/checked-email";
        if(account == null){
            model.addAttribute("error", "wrong email");
            return view;
        }

        if(!account.isValidToken(token)){
            model.addAttribute("error", "wrong token");
            return view;
        }

        accountService.completeSignUp(account);
        model.addAttribute("numberOfUser",accountRepository.count());
        model.addAttribute("nickname", account.getNickname());

        return view;
    }
    @GetMapping("/check-email")
    public String checkEmail(@CurrentUser Account account, Model model){
        model.addAttribute("email",account.getEmail());
        return "account/check-email";
    }
    @GetMapping("/resend-confirm-email")
    public String resendConfirmEmail(@CurrentUser Account account, Model model){
        if(!account.canSendConfirmEmail()){
            model.addAttribute("error","인증 이메일은 1시간에 한번만 전송할 수 있습니다");
            model.addAttribute("email",account.getEmail());
            return "account/check-email";
        }
        accountService.sendSignUpConfirmEmail(account);
        return "redirect:/";
    }

    @GetMapping("/profile/{nickname}")
    public String viewProfile(@PathVariable String nickname, Model model, @CurrentUser Account account){

        Account byNickname  = accountService.getAccount(nickname);
        model.addAttribute("account",byNickname); // name 주지 않으면 해당 객체 class 이름에 camelcase 사용한다
        model.addAttribute("isOwner",byNickname.equals(account));
        return "account/profile";
    }

    @GetMapping("/email-login")
    public String emailLoginForm(){
        return "account/email-login";
    }

    @PostMapping("/email-login")
    public String sendEmailLoginLink(String email, Model model, RedirectAttributes attributes){
        Account account = accountRepository.findByEmail(email);
        if(account == null){
            model.addAttribute("error","유효한 이메일 주소가 아닙니다");
            return "account/email-login";
        }
        if(!account.canSendConfirmEmail()){
            model.addAttribute("error","이메일 로그인은 1시간 뒤에 사용할 수 있습니다");
            return "account/email-login";
        }
        accountService.sendLoginLink(account);
        attributes.addFlashAttribute("message","이메일 인증 메일을 발송했습니다");
        return "redirect:/email-login";
    }

    @GetMapping("/login-by-email")
    public String loginByEmail(String token, String email, Model model) {

        Account account = accountRepository.findByEmail(email);
        if (account == null || !account.isValidToken(token)) {
            model.addAttribute("error", "로그인할 수 없습니다.");
            return "account/logged-in-by-email";
        }

        accountService.login(account);
        return "account/logged-in-by-email";
    }

}