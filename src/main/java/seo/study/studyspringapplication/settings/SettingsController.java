package seo.study.studyspringapplication.settings;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import seo.study.studyspringapplication.account.AccountService;
import seo.study.studyspringapplication.account.CurrentUser;
import seo.study.studyspringapplication.domain.Account;
import seo.study.studyspringapplication.domain.Tag;
import seo.study.studyspringapplication.domain.Zone;
import seo.study.studyspringapplication.settings.form.*;
import seo.study.studyspringapplication.settings.vaildator.NicknameValidator;
import seo.study.studyspringapplication.settings.vaildator.PasswordFormValidator;
import seo.study.studyspringapplication.tag.TagForm;
import seo.study.studyspringapplication.tag.TagRepository;
import seo.study.studyspringapplication.tag.TagService;
import seo.study.studyspringapplication.zone.ZoneForm;
import seo.study.studyspringapplication.zone.ZoneRepository;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class SettingsController {
    // 데이터 변경은 서비스에 위임한다
    private final AccountService accountService;
    private final ModelMapper modelMapper; // modelmapper
    private final NicknameValidator nicknameValidator;
    private final TagRepository tagRepository;
    private final ObjectMapper objectMapper;
    private final ZoneRepository zoneRepository;
    private final TagService tagService;

    @InitBinder("passwordForm")
    public void passwordFormInitBinder(WebDataBinder webDataBinder){
        webDataBinder.addValidators(new PasswordFormValidator());
    }
    @InitBinder("nicknameForm")
    public void nicknameFormInitBinder(WebDataBinder webDataBinder){
        webDataBinder.addValidators(nicknameValidator);
    }

    @GetMapping("/settings/profile")
    public String profileUpdateForm(@CurrentUser Account account, Model model){
        model.addAttribute(account);
        //model.addAttribute(new Profile(account));
        model.addAttribute(modelMapper.map(account, Profile.class)); // source로 destination 인스턴스 생성
        return "settings/profile";
    }
    // 사용하는 Account 객체는 session에 있는 상태 현재 영속성 상태 detach
    @PostMapping("/settings/profile")
    public String updateProfile(@CurrentUser Account account, @Valid @ModelAttribute Profile profile, Errors errors,
                                Model model, RedirectAttributes attributes){
        if(errors.hasErrors()){
            // model에 form을 채운 data와 errors의 정보도 자동으로 들어간다
            model.addAttribute(account);
            return "settings/profile";
        }
        accountService.updateProfile(account,profile);
        attributes.addFlashAttribute("message","프로필을 수정했습니다.");
        return "redirect:/settings/profile";
    }

    @GetMapping("/settings/password")
    public String passwordUpdateForm(@CurrentUser Account account, Model model){

        model.addAttribute(account);
        model.addAttribute(new PasswordForm());
        return "settings/password";
    }
    @PostMapping("/settings/password")
    public String updatePassword(@CurrentUser Account account, @Valid PasswordForm passwordForm, Errors errors,
                                 Model model, RedirectAttributes attributes){

        if(errors.hasErrors()){
            model.addAttribute(account);
            return "settings/password";
        }

        accountService.updatePassword(account,passwordForm.getNewPassword());
        attributes.addFlashAttribute("message", "패스워드를 변경했습니다.");
        return "redirect:/settings/password";

    }
    @GetMapping("/settings/notifications")
    public String notificationsUpdateForm(@CurrentUser Account account, Model model){
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account, Notifications.class));
        return "settings/notifications";
    }
    @PostMapping("/settings/notifications")
    public String updateNotifications(@CurrentUser Account account, @Valid Notifications notifications,Errors errors,
                                      Model model, RedirectAttributes attributes ){
        if(errors.hasErrors()){
            model.addAttribute(account);
            return "settings/notifications";
        }
        accountService.updateNotifications(account,notifications);
        attributes.addFlashAttribute("message","알림 설정을 변경했습니다");
        return "redirect:/settings/notifications";

    }

    @GetMapping("/settings/account")
    public String accountUpdateForm(@CurrentUser Account account, Model model){
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account, NicknameForm.class));
        return "settings/account";
    }
    @PostMapping("/settings/account")
    public String updateAccount(@CurrentUser Account account,@Valid NicknameForm nicknameForm, Errors errors,
                                RedirectAttributes attributes, Model model){
        if(errors.hasErrors()){
            model.addAttribute(account);
            return "settings/account";
        }

        accountService.updateNickname(account,nicknameForm);
        attributes.addFlashAttribute("message","닉네임을 수정했습니다.");
        return "redirect:/settings/account";
    }

    @GetMapping("/settings/tags")
    public String tagUpdateForm(@CurrentUser Account account, Model model) throws JsonProcessingException {
        model.addAttribute(account);
        Set<Tag> tags = accountService.getTags(account);
        model.addAttribute("tags",tags.stream().map(t->t.getTitle()).collect(Collectors.toList()));

        List<String> allTags = tagRepository.findAll().stream().map(t -> t.getTitle()).collect(Collectors.toList());
        // list to json ObjectMapper 사용하면 된다
        model.addAttribute("whitelist",objectMapper.writeValueAsString(allTags));
        return "settings/tags";
    }

    @PostMapping("/settings/tags/add")
    @ResponseBody
    public ResponseEntity addTag(@CurrentUser Account account, @RequestBody TagForm tagForm){
        String title = tagForm.getTagTitle();
        Tag tag = tagService.findOrCreateNew(title);
        accountService.addTag(account,tag);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/settings/tags/remove")
    @ResponseBody
    public ResponseEntity removeTag(@CurrentUser Account account, @RequestBody TagForm tagForm){
        String title = tagForm.getTagTitle();
        Tag tag = tagRepository.findByTitle(title);
        if(tag == null) {
            return ResponseEntity.badRequest().build();
        }

        accountService.removeTag(account,tag);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/settings/zones")
    public String zonesUpdateForm(@CurrentUser Account account, Model model) throws JsonProcessingException {
        model.addAttribute(account);

        Set<Zone> zones = accountService.getZones(account);
        model.addAttribute("zones",zones.stream().map(Zone::toString).collect(Collectors.toList()));

        List<String> allZones = zoneRepository.findAll().stream().map(Zone::toString).collect(Collectors.toList());
        model.addAttribute("whitelist",objectMapper.writeValueAsString(allZones));

        return "settings/zones";
    }

    @ResponseBody
    @PostMapping("/settings/zones/add")
    public ResponseEntity addZone(@CurrentUser Account account, @RequestBody ZoneForm zoneForm){

        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(),zoneForm.getProvinceName());
        if(zone == null){
            return ResponseEntity.badRequest().build();
        }
        accountService.addZone(account,zone);
        return ResponseEntity.ok().build();
    }
    @ResponseBody
    @PostMapping("/settings/zones/remove")
    public ResponseEntity removeZone(@CurrentUser Account account, @RequestBody ZoneForm zoneForm){
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(),zoneForm.getProvinceName());
        if(zone == null)
            return ResponseEntity.badRequest().build();

        accountService.removeZone(account,zone);
        return ResponseEntity.ok().build();
    }



}
