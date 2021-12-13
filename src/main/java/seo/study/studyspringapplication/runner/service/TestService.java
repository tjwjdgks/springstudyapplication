package seo.study.studyspringapplication.runner.service;

import org.springframework.stereotype.Service;
import seo.study.studyspringapplication.runner.aop.TimeTest;

@Service
public class TestService {

    @TimeTest
    public void realtest(){
        System.out.println("TEST");
    }
}
