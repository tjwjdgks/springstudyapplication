package seo.study.studyspringapplication.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import seo.study.studyspringapplication.account.CurrentUser;
import seo.study.studyspringapplication.domain.Account;
import seo.study.studyspringapplication.domain.Event;
import seo.study.studyspringapplication.domain.Study;
import seo.study.studyspringapplication.event.form.EventForm;
import seo.study.studyspringapplication.event.validator.EventValidator;
import seo.study.studyspringapplication.study.StudyRepository;
import seo.study.studyspringapplication.study.StudyService;

import javax.validation.Valid;

@Controller
@RequestMapping("/study/{path}")
@RequiredArgsConstructor
public class EventController {

    private final StudyService studyService;
    private final EventService eventService;
    private final ModelMapper modelMapper;
    private final EventValidator eventValidator;
    private final EventRepository eventRepository;

    @InitBinder("eventForm")
    public void initBinder(WebDataBinder webDataBinder){
        webDataBinder.addValidators(eventValidator);
    }

    @GetMapping("/new-event")
    public String newEventForm(@CurrentUser Account account, @PathVariable String path, Model model){
        Study study = studyService.getStudyToUpdateStatus(account, path);
        model.addAttribute(study);
        model.addAttribute(account);
        model.addAttribute(new EventForm());
        return "event/form";
    }
    @PostMapping("/new-event")
    public String newEventSubmit(@CurrentUser Account account, @PathVariable String path, @Valid EventForm eventForm,
                                 Errors errors, Model model){
        Study study = studyService.getStudyToUpdateStatus(account, path);
        if(errors.hasErrors()){
            model.addAttribute(account);
            model.addAttribute(study);
            return "event/form";
        }
        Event event = eventService.createEvent(study, modelMapper.map(eventForm, Event.class), account);
        return "redirect:/study/" + study.getEncodePath(path) + "/events/" + event.getId();

    }
    @GetMapping("/events/{eventId}")
    public String getEvent(@CurrentUser Account account, @PathVariable String path,
                           @PathVariable Long eventId, Model model){
        model.addAttribute(account);
        model.addAttribute(eventRepository.findById(eventId).orElseThrow());
        model.addAttribute(studyService.getStudy(path));
        return "event/view";
    }
}
