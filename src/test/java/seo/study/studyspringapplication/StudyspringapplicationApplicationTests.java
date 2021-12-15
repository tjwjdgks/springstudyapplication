package seo.study.studyspringapplication;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.SpringVersion;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;
import seo.study.studyspringapplication.infra.AbstractContainerBaseTest;
import seo.study.studyspringapplication.modules.account.AccountFactory;
import seo.study.studyspringapplication.modules.event.Event;
import seo.study.studyspringapplication.modules.event.EventFactory;
import seo.study.studyspringapplication.modules.event.EventRepository;
import seo.study.studyspringapplication.modules.study.StudyFactory;

import java.util.List;

import static org.springframework.transaction.support.TransactionSynchronizationManager.isCurrentTransactionReadOnly;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class StudyspringapplicationApplicationTests extends AbstractContainerBaseTest {

    @Autowired
    EventRepository eventRepository;
    @Autowired
    AccountFactory accountFactory;
    @Autowired
    StudyFactory studyFactory;
    @Autowired
    EventFactory eventFactory;
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
