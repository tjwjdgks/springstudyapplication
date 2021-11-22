package seo.study.studyspringapplication.study;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import seo.study.studyspringapplication.domain.Study;

@Transactional(readOnly = true) // 데이터 변경 작업은 서비스에 위임
public interface StudyRepository extends JpaRepository<Study,Long> {
    boolean existsByPath(String path);
}
