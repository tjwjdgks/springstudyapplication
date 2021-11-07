package seo.study.studyspringapplication.account;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import seo.study.studyspringapplication.domain.Account;

import java.util.List;

// UserDetailsService bean이 하나만 있으면 SpringSecurity가 자동으로 이것을 사용한다
@Service
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {
    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;

    // authenticationManager 빈으로 노출 되어 있지 않음(특정 설정 없이 빈으로 주입 못받는다)
    //private final AuthenticationManager authenticationManager;

    @Transactional
    public Account processNewAccount(SignUpForm signUpForm) {
        Account newAccount = saveNewAccount(signUpForm);
        newAccount.generateEmailToken();
        sendSignUpConfirmEmail(newAccount);
        return newAccount;
    }
    // save안에서 트랜잭션이므로 entity persist but 트랜잭션 나오면 detach 상태이므로 트랜잭션 상태를 유지해야한다
    private Account saveNewAccount(SignUpForm signUpForm) {
        Account account  = Account.builder()
                .email(signUpForm.getEmail())
                .nickname(signUpForm.getNickname())
                .password(passwordEncoder.encode(signUpForm.getPassword())) // password  encoding 해야함
                .studyEnrollmentResultByWeb(true)
                .studyUpdatedByWeb(true)
                .build();
        Account newAccount = accountRepository.save(account);
        return newAccount;
    }

    public void sendSignUpConfirmEmail(Account newAccount) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newAccount.getEmail());
        mailMessage.setSubject("스터디 가입인증 ");
        mailMessage.setText("/check-email-token?token=" + newAccount.getEmailCheckToken()
                +"&email="+ newAccount.getEmail());
        javaMailSender.send(mailMessage);
    }

    public void login(Account account) {
        // entity manager 생성자로 사용하는 방법, 비 정석
        // 인코딩하는 비밀번호를 사용할 수 있음
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new UserAccount(account),
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(token);

        //정석적인 방법
        // plain text 비밀번호를 사용해야함
//        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username,password);
//        Authentication authentication = authenticationManager.authenticate(token);
//        SecurityContext context = SecurityContextHolder.getContext();
//        context.setAuthentication(authentication);

    }
    // form 로그인을 위해 UserDetailService 구현
    @Override
    public UserDetails loadUserByUsername(String emailOrNickname) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(emailOrNickname);
        if(account == null){
            account = accountRepository.findByNickname(emailOrNickname);
        }
        if(account == null)
            throw new UsernameNotFoundException(emailOrNickname);

        // principal 객체 넘겨 준다 현재는 User확장한 UserAccount
        return new UserAccount(account) ;
    }
}
