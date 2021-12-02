package seo.study.studyspringapplication.study;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import seo.study.studyspringapplication.account.CurrentUser;
import seo.study.studyspringapplication.domain.Account;
import seo.study.studyspringapplication.domain.Study;
import seo.study.studyspringapplication.domain.Tag;
import seo.study.studyspringapplication.domain.Zone;
import seo.study.studyspringapplication.study.form.StudyDescriptionForm;
import seo.study.studyspringapplication.tag.TagForm;
import seo.study.studyspringapplication.tag.TagRepository;
import seo.study.studyspringapplication.tag.TagService;
import seo.study.studyspringapplication.zone.ZoneForm;
import seo.study.studyspringapplication.zone.ZoneRepository;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/study/{path}/settings")
@RequiredArgsConstructor
public class StudySettingController {

    private final StudyService studyService;
    private final ModelMapper modelMapper;
    private final TagRepository tagRepository;
    private final ObjectMapper objectMapper;
    private final TagService tagService;
    private final ZoneRepository zoneRepository;

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
    @GetMapping("/tags")
    public String studyTagForm(@CurrentUser Account account, @PathVariable String path, Model model) throws JsonProcessingException {

        Study study = studyService.getStudyToUpdate(account,path);
        model.addAttribute(account);
        model.addAttribute(study);
        model.addAttribute("tags", study.getTags().stream().map(tag->tag.getTitle())
                .collect(Collectors.toList()));
        List<String> allTagTitles = tagRepository.findAll().stream().map(tag -> tag.getTitle()).collect(Collectors.toList());
        model.addAttribute("whitelist",objectMapper.writeValueAsString(allTagTitles));
        return "study/settings/tags";
    }
    @PostMapping("/tags/add")
    @ResponseBody
    public ResponseEntity addTags(@CurrentUser Account account, @PathVariable String path, @RequestBody TagForm tagForm){

        Study study = studyService.getStudyToUpdateTag(account, path);
        Tag tag = tagService.findOrCreateNew(tagForm.getTagTitle());
        studyService.addTag(study, tag);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/tags/remove")
    @ResponseBody
    public ResponseEntity removeTags(@CurrentUser Account account, @PathVariable String path, @RequestBody TagForm tagForm){
        Study study = studyService.getStudyToUpdateTag(account, path);
        Tag tag = tagRepository.findByTitle(tagForm.getTagTitle());
        if(tag == null)
            return ResponseEntity.badRequest().build();

        studyService.removeTag(study,tag);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/zones")
    public String studyZoneForm(@CurrentUser Account account, @PathVariable String path, Model model) throws JsonProcessingException {

        Study study = studyService.getStudyToUpdate(account, path);
        model.addAttribute(study);
        model.addAttribute(account);
        model.addAttribute("zones",study.getZones().stream().map(zone->zone.toString()).collect(Collectors.toList()));
        List<String> allZones = zoneRepository.findAll().stream().map(zone->zone.toString()).collect(Collectors.toList());
        model.addAttribute("whitelist",objectMapper.writeValueAsString(allZones));
        return "study/settings/zones";
    }
    @PostMapping("/zones/add")
    @ResponseBody
    public ResponseEntity addZones(@CurrentUser Account account, @PathVariable String path, @RequestBody ZoneForm zoneForm){
        Study study = studyService.getStudyToUpdateZone(account, path);
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
        if(zone == null)
            return ResponseEntity.badRequest().build();
        studyService.addZone(study,zone);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/zones/remove")
    @ResponseBody
    public ResponseEntity removeZones(@CurrentUser Account account, @PathVariable String path, @RequestBody ZoneForm zoneForm){
        Study study = studyService.getStudyToUpdateZone(account, path);
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
        if(zone == null)
            return ResponseEntity.badRequest().build();
        studyService.removeZone(study,zone);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/study")
    public String updateStudyForm(@CurrentUser Account account, @PathVariable String path, Model model){
        Study study = studyService.getStudyToUpdate(account,path);
        model.addAttribute(account);
        model.addAttribute(study);
        return "study/settings/study";
    }
    @PostMapping("/study/publish")
    public String updateStudyPublish(@CurrentUser Account account, @PathVariable String path,RedirectAttributes attributes){
        Study study = studyService.getStudyToUpdateStatus(account, path);
        studyService.publish(study);
        attributes.addFlashAttribute("message","스터디를 공개했습니다.");
        return "redirect:/study/" +getPath(path) + "/settings/study";
    }
    @PostMapping("/study/close")
    public String updateStudyClose(@CurrentUser Account account, @PathVariable String path,RedirectAttributes attributes){
        Study study = studyService.getStudyToUpdateStatus(account, path);
        studyService.close(study);
        attributes.addFlashAttribute("message","스터디를 종료했습니다");
        return "redirect:/study/" +getPath(path) + "/settings/study";
    }
    @PostMapping("/recruit/start")
    public String startRecuit(@CurrentUser Account account, @PathVariable String path, RedirectAttributes attributes){
        Study study = studyService.getStudyToUpdateStatus(account,path);
        if(!study.canUpdateRecruiting()){
            attributes.addFlashAttribute("message","1시간 안에 인원 모집 설정을 여러번 변경할 수 없습니다.");
            return "redirect:/study/" +getPath(path) + "/settings/study";
        }
        studyService.startRecruit(study);
        attributes.addFlashAttribute("message","인원 모집을 시작합니다.");
        return "redirect:/study/" +getPath(path) + "/settings/study";
    }
    @PostMapping("/recruit/stop")
    public String stopRecuit(@CurrentUser Account account, @PathVariable String path, RedirectAttributes attributes){
        Study study = studyService.getStudyToUpdateStatus(account,path);
        if(!study.canUpdateRecruiting()){
            attributes.addFlashAttribute("message","1시간 안에 인원 모집 설정을 여러번 변경할 수 없습니다.");
            return "redirect:/study/" +getPath(path) + "/settings/study";
        }
        studyService.stopRecruit(study);
        attributes.addFlashAttribute("message","인원 모집을 종료합니다.");
        return "redirect:/study/" +getPath(path) + "/settings/study";
    }
    @PostMapping("/study/path")
    public String updateStudyPath(@CurrentUser Account account, @PathVariable String path, String newPath, RedirectAttributes attributes, Model model){
        Study study = studyService.getStudyToUpdateStatus(account,path);
        if(!studyService.isValidPath(newPath)){
            model.addAttribute("studyPathError", "해당 스터디 경로는 사용할 수 없습니다. 다른 값을 입력하세요.");
            model.addAttribute(account);
            model.addAttribute(study);
            return "study/settings/study";
        }
        studyService.updateStudyPath(study,newPath);
        attributes.addFlashAttribute("message", "스터디 경로를 수정했습니다.");
        return "redirect:/study/" + getPath(newPath) + "/settings/study";
    }
    @PostMapping("/study/title")
    public String updateStudyTitle(@CurrentUser Account account, @PathVariable String path, String newTitle, RedirectAttributes attributes, Model model){
        Study study = studyService.getStudyToUpdateStatus(account,path);
        if(!studyService.isValidTitle(newTitle)){
            model.addAttribute("studyTitleError", "스터디 이름을 사용할 수 없습니다. 다시 입력하세요.");
            model.addAttribute(account);
            model.addAttribute(study);
            return "study/settings/study";
        }
        studyService.updateStudyTitle(study,newTitle);
        attributes.addFlashAttribute("message", "스터디 이름을 수정했습니다.");
        return "redirect:/study/" + getPath(path) + "/settings/study";
    }
    @PostMapping("/study/remove")
    public String removeStudy(@CurrentUser Account account, @PathVariable String path){
        Study study = studyService.getStudyToUpdateStatus(account,path);
        studyService.remove(study);
        return "redirect:/";
    }
    private String getPath(String path){
        return URLEncoder.encode(path, StandardCharsets.UTF_8);
    }

}
