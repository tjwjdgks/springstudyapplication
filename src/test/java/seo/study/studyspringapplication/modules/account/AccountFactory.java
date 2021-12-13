package seo.study.studyspringapplication.modules.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AccountFactory {
    @Autowired AccountRepository accountRepository;

    public Account createAccount(String nickname){
        Account test = new Account();
        test.setNickname(nickname);
        test.setPassword("12345678");
        test.setEmail(nickname+"@email.com");
        Account save = accountRepository.save(test);
        return save;
    }
    public Account createAccountBeforeTime(String nickname, LocalDateTime localDateTime){
        Account test= new Account();
        test.setNickname(nickname);
        test.setPassword("12345678");
        test.setEmail(nickname+"@email.com");
        test.setEmailTokenGeneratedAt(localDateTime);
        Account save = accountRepository.save(test);
        return save;
    }
}
