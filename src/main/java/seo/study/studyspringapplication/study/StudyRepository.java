package seo.study.studyspringapplication.study;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import seo.study.studyspringapplication.domain.Study;

@Transactional(readOnly = true) // 데이터 변경 작업은 서비스에 위임
public interface StudyRepository extends JpaRepository<Study,Long> {
    boolean existsByPath(String path);
    // load : 선언 Eager 나머지 기본 전략
    // fetch : 선언 Eager 나머지 lazy
    @EntityGraph(value = "Study.withAll",type = EntityGraph.EntityGraphType.LOAD)
    Study findByPath(String path);

    @EntityGraph(value = "Study.withTagAndManger", type = EntityGraph.EntityGraphType.LOAD)
    Study findStudyWithTagsByPath(String path);

    @EntityGraph(value = "Study.withZoneAndManger", type = EntityGraph.EntityGraphType.LOAD)
    Study findStudyWithZonesByPath(String path);

    @EntityGraph(value = "Study.withManagers", type = EntityGraph.EntityGraphType.FETCH)
    Study findStudyWithManagersByPath(String path);

    @EntityGraph(value = "Study.withMembers", type = EntityGraph.EntityGraphType.FETCH)
    Study findStudyWithMembersByPath(String path);

    Study findOnlyStudyByPath(String path);
}
