package seo.study.studyspringapplication.runner;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import seo.study.studyspringapplication.runner.service.TestService;

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
