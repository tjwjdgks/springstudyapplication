package seo.study.studyspringapplication.runner;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.stereotype.Component;
import seo.study.studyspringapplication.aop.TimeTest;
import seo.study.studyspringapplication.service.TestService;

import java.util.List;

@Component
public class AOPTest implements ApplicationRunner {
    @Autowired
    TestService testService;
    @Autowired
    ObjectMapper objectMapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        testService.realtest();

    }


}
