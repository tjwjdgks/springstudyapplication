package seo.study.studyspringapplication.modules.study;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
// querydsl 사용
// StudyRepostiory에 적용, 이 인터페이스 구현체는 뒤에 Impl을 붙여야 한다 ex) StudyRepostiory(커스텀이름)Impl
@Transactional(readOnly = true)
public interface StudyRepositoryExtension {

    Page<Study> findByKeyword(String keyword, Pageable pageable);
}
