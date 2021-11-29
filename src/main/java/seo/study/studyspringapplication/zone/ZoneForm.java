package seo.study.studyspringapplication.zone;

import lombok.Data;
import seo.study.studyspringapplication.domain.Zone;

@Data
public class ZoneForm {

    private String zoneName;

    public String getCityName(){
        return zoneName.substring(0, zoneName.indexOf("("));
    }
    public String getProvinceName(){
        return zoneName.substring(zoneName.indexOf("/")+1);
    }
    public String getLocalNameOfCity(){
        return zoneName.substring(zoneName.indexOf("(")+1,zoneName.indexOf(")"));
    }
    public Zone getZone(){
        return Zone.builder()
                .city(this.getCityName())
                .localNameOfCity(this.getLocalNameOfCity())
                .province(this.getProvinceName())
                .build();
    }
}
