package seo.study.studyspringapplication.study;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import seo.study.studyspringapplication.account.CurrentUser;
import seo.study.studyspringapplication.domain.Account;
import seo.study.studyspringapplication.domain.Study;
import seo.study.studyspringapplication.study.form.StudyDescriptionForm;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/study/{path}/settings")
@RequiredArgsConstructor
public class StudySettingController {

    private final StudyService studyService;
    private final ModelMapper modelMapper;

    @GetMapping("/description")
    public String studyDescriptionForm(@CurrentUser Account account, @PathVariable String path, Model model){
        model.addAttribute(account);
        Study study = studyService.getStudyToUpdate(account,path);
        model.addAttribute(study);
        model.addAttribute(modelMapper.map(study,StudyDescriptionForm.class));
        return "study/settings/description";
    }
    @PostMapping("/description")
    public String updateStudyInfo(@CurrentUser Account account, @PathVariable String path,
                                  @Valid StudyDescriptionForm studyDescriptionForm, Errors errors,
                                  Model model, RedirectAttributes redirectAttributes){

        Study study = studyService.getStudyToUpdate(account,path);
        if(errors.hasErrors()){
            model.addAttribute(account);
            model.addAttribute(study);
            return "study/settings/description";
        }

        studyService.updateStudyDescription(study, studyDescriptionForm);
        redirectAttributes.addFlashAttribute("message","스터디 소개를 수정했습니다");
        return "redirect:/study/"+getPath(path)+"/settings/description";

    }
    @GetMapping("/banner")
    public String studyBanner(@CurrentUser Account account,@PathVariable String path, Model model){
        Study study = studyService.getStudyToUpdate(account, path);
        model.addAttribute(study);
        model.addAttribute(account);
        return "study/settings/banner";
    }
    @PostMapping("/banner")
    public String studyBannerUpdate(@CurrentUser Account account, @PathVariable String path,
                                    String image,RedirectAttributes redirectAttributes){
        Study study = studyService.getStudyToUpdate(account, path);
        studyService.updateStudyImage(study,image);
        redirectAttributes.addFlashAttribute("message", "스터디 이미지를 수정했습니다.");
        return "redirect:/study/" + getPath(path) + "/settings/banner";
    }
    @PostMapping("/banner/enable")
    public String enableStudyBanner(@CurrentUser Account account,@PathVariable String path){
        Study study = studyService.getStudyToUpdate(account, path);
        studyService.enableStudyBanner(study);
        return "redirect:/study/" + getPath(path) + "/settings/banner";
    }
    @PostMapping("/banner/disable")
    public String disableStudyBanner(@CurrentUser Account account,@PathVariable String path){
        Study study = studyService.getStudyToUpdate(account, path);
        studyService.disableStudyBanner(study);
        return "redirect:/study/" + getPath(path) + "/settings/banner";
    }
    private String getPath(String path){
        return URLEncoder.encode(path, StandardCharsets.UTF_8);
    }

}
