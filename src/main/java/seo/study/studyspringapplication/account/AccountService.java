package seo.study.studyspringapplication.account;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.NameTokenizers;
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
import seo.study.studyspringapplication.settings.form.NicknameForm;
import seo.study.studyspringapplication.settings.form.Notifications;
import seo.study.studyspringapplication.settings.form.Profile;

import java.util.List;

// UserDetailsService bean이 하나만 있으면 SpringSecurity가 자동으로 이것을 사용한다
@Service
@Transactional // traincation 없이 data 변경 하면 db 반영 안됨
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    // authenticationManager 빈으로 노출 되어 있지 않음(특정 설정 없이 빈으로 주입 못받는다)
    //private final AuthenticationManager authenticationManager;

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
                .studyCreatedByWeb(true)
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
    @Transactional(readOnly = true)
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

    public void completeSignUp(Account account) {
        account.completeSignUp();
        login(account);
    }
    // 영속성 detach 상태를 save를 호출하여 persistence 상태로 전환하여 data 저장한다
    public void updateProfile(Account account, Profile profile) {
        //source data를 destination으로 복사
        //source와 dist 이름 일치해야 함
        modelMapper.map(profile,account);
        accountRepository.save(account);
    }

    public void updatePassword(Account account, String newPassword) {
        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);
    }

    public void updateNotifications(Account account, Notifications notifications) {
        // 기본 modelMapper가 이해할 수 있도록 설정해주어야 한다
        // modelMapper는 유사한 이름도 저장하는 기능
        modelMapper.getConfiguration()
                .setSourceNameTokenizer(NameTokenizers.UNDERSCORE)
                .setDestinationNameTokenizer(NameTokenizers.UNDERSCORE);
        modelMapper.map(notifications,account);
        accountRepository.save(account);
    }

    public void updateNickname(Account account, NicknameForm nicknameForm) {
        modelMapper.map(nicknameForm,account);
        accountRepository.save(account);
        login(account);
    }

    public void sendLoginLink(Account account) {
        account.generateEmailToken();
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(account.getEmail());
        mailMessage.setSubject("스터디, 로그인 링크");
        mailMessage.setText("/login-by-email?token=" + account.getEmailCheckToken() +
                "&email=" + account.getEmail());
        javaMailSender.send(mailMessage);
    }
}
