package seo.study.studyspringapplication.modules.study;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import seo.study.studyspringapplication.modules.account.Account;

import java.util.List;

@Transactional(readOnly = true) // 데이터 변경 작업은 서비스에 위임
public interface StudyRepository extends JpaRepository<Study,Long>, StudyRepositoryExtension {

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

    @EntityGraph(value = "Study.withTagsAndZones", type = EntityGraph.EntityGraphType.FETCH)
    Study findStudyWithTagsAndZonesById(Long id);

    @EntityGraph(attributePaths = {"members","managers"})
    Study findStudyWithManagersAndMembersById(Long id);

    List<Study> findFirst9ByPublishedAndAndClosedOrderByPublishedDateTimeDesc(boolean published, boolean closed);

    List<Study> findFirst5ByManagersContainingAndClosedOrderByPublishedDateTimeDesc(Account account, boolean closed);

    List<Study> findFirst5ByMembersContainingAndClosedOrderByPublishedDateTimeDesc(Account account, boolean closed);
}
