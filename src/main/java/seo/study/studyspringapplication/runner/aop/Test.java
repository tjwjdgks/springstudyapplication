package seo.study.studyspringapplication.runner.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class Test {

    @Around("@annotation(TimeTest)")
    public Object timeChecks(ProceedingJoinPoint pip) throws Throwable{
        long begin = System.currentTimeMillis();
        Object proceed = pip.proceed();
        System.out.println(System.currentTimeMillis()-begin);
        return proceed;
    }
}
