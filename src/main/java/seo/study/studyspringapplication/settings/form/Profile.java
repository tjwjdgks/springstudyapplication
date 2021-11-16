package seo.study.studyspringapplication.settings.form;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import seo.study.studyspringapplication.domain.Account;

@Data
// @NoArgsConstructor // ModelAttribute로 바인딩 할때 필요하다. // ModelAttribute는 default 객체 만들고 setter로 정보 주입해준다
// 현재 modelMapper 사용
public class Profile {

    @Length(max = 35)
    private String bio;

    @Length(max = 50)
    private String url;

    @Length(max = 50)
    private String occupation;

    @Length(max = 50)
    private String location;

    private String profileImage;

}
