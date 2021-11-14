package seo.study.studyspringapplication;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.SpringVersion;

@SpringBootTest
class StudyspringapplicationApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    @DisplayName("스프링 버전확인")
    public void getVersion(){
        String version = SpringVersion.getVersion();
        System.out.println(version);
    }
}
