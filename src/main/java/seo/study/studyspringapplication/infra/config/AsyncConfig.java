package seo.study.studyspringapplication.infra.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/*
에러를 처리하는 핸들러 등록할 수 있다, AsyncConfigurer 상속
 EnableAsync 애노테이션 기본 Async 등록 : SimpleAsyncTaskExecutor 매번 새로운 스레드에서 실행(스래드를 새로 만듬, 스래드 재사용하지 않음 비효율적)
 thread풀에다 스레드를 만들어 놓고 쓰는 것이 효율적이다
 */

@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    // Executor
    // 효율적인 Executor로 전환
    @Override
    public Executor getAsyncExecutor() {
        // java의 a ThreadPoolExecutor를 사용한 Executor
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        int processors = Runtime.getRuntime().availableProcessors(); // 프로세서 갯수
        log.info("creating pool with core count {}",processors);
        // cpu에 따라 값들이 달라 질수 있음
        executor.setCorePoolSize(processors);
        executor.setMaxPoolSize(processors*2);
        // 메모리에 따라 값들이 달라 질수 있음
        executor.setQueueCapacity(50);
        // 이상적인 thread 수(setCorePoolSize 수)를 넘은 thread들을 언제 까지 살리다가 정리할 것인지
        executor.setKeepAliveSeconds(60);
        // 이름을 줄수 있음
        executor.setThreadNamePrefix("AsyncExecutor-");
        // 초기화 호출해주어야 한다
        executor.initialize();
        return executor;
    }

    // Exception 핸들러
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return AsyncConfigurer.super.getAsyncUncaughtExceptionHandler();
    }
}
