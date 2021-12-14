package seo.study.studyspringapplication.modules.account;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
// anonnymousUser 일경우 null을 넣어주고 인증된 사용자 경우 account를 넣어준다
// AnonymousAuthenticationToken시 anonymousUser로 정의 되어있음
// AnonymousConfigurer에서 principal 정의 :  Object principal = "anonymousUser"
@AuthenticationPrincipal(expression = "#this == 'anonymousUser' ? null : account")
public @interface CurrentUser {
}
