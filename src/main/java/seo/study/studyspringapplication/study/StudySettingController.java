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

    private String getPath(String path){
        return URLEncoder.encode(path, StandardCharsets.UTF_8);
    }

}
