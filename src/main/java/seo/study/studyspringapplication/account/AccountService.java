package seo.study.studyspringapplication.account;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import seo.study.studyspringapplication.domain.Account;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;

    public void processNewAccount(SignUpForm signUpForm) {
        Account newAccount = saveNewAccount(signUpForm);
        newAccount.generateEmailToken();
        sendEmailCheck(newAccount);

    }

    private Account saveNewAccount(SignUpForm signUpForm) {
        Account account  = Account.builder()
                .email(signUpForm.getEmail())
                .nickname(signUpForm.getNickname())
                .password(signUpForm.getPassword()) // password  encoding 해야함
                .studyEnrollmentResultByWeb(true)
                .studyUpdatedByWeb(true)
                .build();
        Account newAccount = accountRepository.save(account);
        return newAccount;
    }

    private void sendEmailCheck(Account newAccount) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newAccount.getEmail());
        mailMessage.setSubject("스터디 가입인증 ");
        mailMessage.setText("/check-email-token?token=" + newAccount.getEmailCheckToken()
                +"&email="+ newAccount.getEmail());
        javaMailSender.send(mailMessage);
    }
}
