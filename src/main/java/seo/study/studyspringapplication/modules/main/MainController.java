package seo.study.studyspringapplication.modules.main;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import seo.study.studyspringapplication.modules.account.AccountRepository;
import seo.study.studyspringapplication.modules.account.CurrentUser;
import seo.study.studyspringapplication.modules.account.Account;
import seo.study.studyspringapplication.modules.event.EnrollmentRepository;
import seo.study.studyspringapplication.modules.study.Study;
import seo.study.studyspringapplication.modules.study.StudyRepository;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MainController {

    private final StudyRepository studyRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AccountRepository accountRepository;

    @GetMapping("/")
    public String home(@CurrentUser Account account, Model model){
        if(account != null){
            Account accountLoaded = accountRepository.findAccountWithTagsAndZonesById(account.getId());
            model.addAttribute("enrollmentList",enrollmentRepository.findEventAndStudyWithEnrollmentByAccountAndAcceptedOrderByEnrolledAtDesc(account,true));
            model.addAttribute("studyList",studyRepository.findRecent9StudyByAccountTagsAndAccountZones(accountLoaded.getTags(),accountLoaded.getZones()));
            model.addAttribute("studyManagerOf", studyRepository.findFirst5ByManagersContainingAndClosedOrderByPublishedDateTimeDesc(account,false));
            model.addAttribute("studyMemberOf", studyRepository.findFirst5ByMembersContainingAndClosedOrderByPublishedDateTimeDesc(account,false));
            model.addAttribute("account",accountLoaded);
            return "index-login";
        }
        List<Study> studyList = studyRepository.findFirst9ByPublishedAndAndClosedOrderByPublishedDateTimeDesc(true, false);
        model.addAttribute("studyList",studyList);
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
