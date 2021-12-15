package seo.study.studyspringapplication.modules.main;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import seo.study.studyspringapplication.modules.account.CurrentUser;
import seo.study.studyspringapplication.modules.account.Account;
import seo.study.studyspringapplication.modules.study.Study;
import seo.study.studyspringapplication.modules.study.StudyRepository;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final StudyRepository studyRepository;

    @GetMapping("/")
    public String home(@CurrentUser Account account, Model model){
        if(account != null){
            model.addAttribute(account);
        }
        return "index";
    }
    @GetMapping("/login")
    public String login(){
        return "login";
    }
    @GetMapping("/search/study") // 페이지 주기, pageable 파라미터 size, page, sort
    public String searchStudy(@PageableDefault(size = 9, page = 0, sort="publishedDateTime", direction = Sort.Direction.DESC)
                                          Pageable pageable, String keyword, Model model){
        Page<Study> studyPage = studyRepository.findByKeyword(keyword, pageable);
        // 이름을 주지 않는 경우 비어있는 Collect이 들어오면 무시한다,
        // 이름이 주어지는 경우는 빈 Collection 이라도 넘어간다
        model.addAttribute("studyPage",studyPage);
        model.addAttribute("keyword",keyword);
        model.addAttribute("sortProperty",
                pageable.getSort().toString().contains("publishedDateTime") ? "publishedDateTime" : "memberCount");
        return "search";
    }
}
