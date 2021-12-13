package seo.study.studyspringapplication.infra.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.NameTokenizers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {

    // bean으로 등록되었을 때 springsecurity가 password 인코더 사용한다
    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
    @Bean
    public ModelMapper modelMapper(){

        // 기본 modelMapper가 이해할 수 있도록 설정해주어야 한다
        // modelMapper는 유사한 이름도 저장하는 기능 //  default의 경우 네이밍 컨벤션 모두 적용하기 때문에 UNDERSCORE 만 적용 하도록 변경
        // http://modelmapper.org/user-manual/configuration/#matching-strategies
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setSourceNameTokenizer(NameTokenizers.UNDERSCORE)
                .setDestinationNameTokenizer(NameTokenizers.UNDERSCORE);
        return modelMapper;
    }
}
