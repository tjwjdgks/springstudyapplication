package seo.study.studyspringapplication.service;

import org.springframework.stereotype.Service;
import seo.study.studyspringapplication.aop.TimeTest;

@Service
public class TestService {

    @TimeTest
    public void realtest(){
        System.out.println("TEST");
    }
}
