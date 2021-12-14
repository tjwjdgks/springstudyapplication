package seo.study.studyspringapplication.infra.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.StaticResourceLocation;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import seo.study.studyspringapplication.modules.notification.NotificationInterceptor;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
@Configuration
// EnableWebMvc 주면 안됨 - spring mvc 자동설정 사용, 이 경우 기존의 spring boot에서 mvc에  추가적으로 사용하는 자동 설정 사용안함
public class WebConfig implements WebMvcConfigurer {
    private final NotificationInterceptor notificationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // static 리소스 요청에는 적용하지 않기. // spring boot StaticResourceLocation 에서 관리
        List<String> allStaticResourcesPath = Arrays.stream(StaticResourceLocation.values())
                .flatMap(StaticResourceLocation::getPatterns)
                .collect(toList());
        allStaticResourcesPath.add("/node_modules/**");
        allStaticResourcesPath.add("/favicon.ico");
        
        registry.addInterceptor(notificationInterceptor).excludePathPatterns(allStaticResourcesPath);
    }
}
