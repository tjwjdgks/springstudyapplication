package seo.study.studyspringapplication.infra;

import org.testcontainers.containers.PostgreSQLContainer;

// 싱글톤 방법으로 모든 테스트들에서 컨테이너 공유
public abstract class AbstractContainerBaseTest {

    static final PostgreSQLContainer POSTGRE_SQL_CONTAINER;
    static{
        POSTGRE_SQL_CONTAINER = new PostgreSQLContainer(PostgreSQLContainer.IMAGE);
        POSTGRE_SQL_CONTAINER.start();
    }
}
