package seo.study.studyspringapplication.account;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import seo.study.studyspringapplication.domain.Account;

import java.util.Collection;
import java.util.List;

// spring security의 user 정보와 우리 도메인의 user 정보와의 어댑터 객체
@Getter
public class UserAccount extends User {
    private Account account;
    public UserAccount(Account account) {
        super(account.getNickname(), account.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_USER")));
        this.account = account;
    }
}
