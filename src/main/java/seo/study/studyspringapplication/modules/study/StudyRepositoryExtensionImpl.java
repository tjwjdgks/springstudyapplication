package seo.study.studyspringapplication.modules.study;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import seo.study.studyspringapplication.modules.account.QAccount;
import seo.study.studyspringapplication.modules.tag.QTag;
import seo.study.studyspringapplication.modules.zone.QZone;

import java.util.List;
// querydsl 사용
public class StudyRepositoryExtensionImpl extends QuerydslRepositorySupport implements StudyRepositoryExtension {
    /**
     * Creates a new {@link QuerydslRepositorySupport} instance for the given domain type.
     *
     * @param domainClass must not be {@literal null}.
     */
    public StudyRepositoryExtensionImpl() {
        super(Study.class); // 도메인 type 넘겨주어야 함
    }

    @Override // page 적용 List 아닌 page
    public Page<Study> findByKeyword(String keyword, Pageable pageable) {
        QStudy study = QStudy.study;
        JPQLQuery<Study> query = from(study).where(study.published.isTrue()
                .and(study.title.containsIgnoreCase(keyword)
                        .or(study.tags.any().title.containsIgnoreCase(keyword))
                        .or(study.zones.any().localNameOfCity.containsIgnoreCase(keyword))))
                .leftJoin(study.tags, QTag.tag).fetchJoin()
                .leftJoin(study.zones, QZone.zone).fetchJoin()
                .leftJoin(study.members, QAccount.account).fetchJoin();

        // pageable 적용하기
        JPQLQuery<Study> pageableQuery = getQuerydsl().applyPagination(pageable, query);
        QueryResults<Study> fetchResults = pageableQuery.fetchResults(); // 페이지 정보가 포함되어 있음
        return new PageImpl<>(fetchResults.getResults(),pageable,fetchResults.getTotal());
    }
}
