package seo.study.studyspringapplication.runner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import seo.study.studyspringapplication.aop.TimeTest;
import seo.study.studyspringapplication.service.TestService;

@Component
public class AOPTest implements ApplicationRunner {
    @Autowired
    TestService testService;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        testService.realtest();
    }


}
