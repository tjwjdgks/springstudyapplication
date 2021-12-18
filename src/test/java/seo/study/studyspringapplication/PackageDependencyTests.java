package seo.study.studyspringapplication;


import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import seo.study.studyspringapplication.modules.event.Enrollment;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

// 해당 클래스가 들어가 있는 package(seo.study.studyspringapplication)를 검사하겠다.
@AnalyzeClasses(packagesOf = StudyspringapplicationApplication.class)
public class PackageDependencyTests {

    private static final String MAIN= "..modules.main..";
    private static final String STUDY = "..modules.study..";
    private static final String EVENT = "..modules.event..";
    private static final String ACCOUNT = "..modules.account..";
    private static final String TAG = "..modules.tag..";
    private static final String ZONE = "..modules.zone..";
    private static final String Notification = "..modules.notification..";

    @ArchTest
    ArchRule studyPackageRule = classes().that().resideInAPackage(STUDY) // 스터디 패키지 안에 들어 있는 클래스는
            .should().onlyBeAccessed().byClassesThat() // 스터디와 이벤트 클래스들에만 의해 접근 가능해야 한다
            .resideInAnyPackage(STUDY,EVENT,MAIN);

    @ArchTest
    ArchRule eventPackageRule = classes().that().resideInAPackage(EVENT) // 이벤트 패키지 안에 들어 있는 클래스는
            .should().accessClassesThat().resideInAnyPackage(STUDY,EVENT,ACCOUNT); // 스터디, 이벤트, ACCOUNT만 참조한다

    @ArchTest
    ArchRule accountPackageRule = classes().that().resideInAPackage(ACCOUNT)  // ACCOUNT 패키지 안에 들어 있는 클래스는
            .should().accessClassesThat().resideInAnyPackage(ACCOUNT,TAG,ZONE);  // ACCOUNT, TAG, ZONE 참조한다

    @ArchTest
    ArchRule notificationPackageRule = classes().that().resideInAPackage(Notification)
            .should().onlyBeAccessed().byClassesThat().resideInAnyPackage(Notification,ACCOUNT,STUDY, EVENT);

    // 사이클 검사
    @ArchTest
    ArchRule cycleCheck = slices().matching("seo.study.studyspringapplication.modules.(*)..") // 모듈 조각
            .should().beFreeOfCycles();
    // 모듈 참조 검사
    @ArchTest
    ArchRule modulesPackageRule = classes().that().resideInAPackage("..modules..")
            .should().onlyBeAccessed().byClassesThat().resideInAPackage("..modules..");



}
