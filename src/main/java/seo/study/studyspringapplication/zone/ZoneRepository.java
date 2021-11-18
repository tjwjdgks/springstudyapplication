package seo.study.studyspringapplication.zone;

import org.springframework.data.jpa.repository.JpaRepository;
import seo.study.studyspringapplication.domain.Zone;

public interface ZoneRepository extends JpaRepository<Zone,Long> {

    Zone findByCityAndProvince(String cityName, String provinceName);
}
