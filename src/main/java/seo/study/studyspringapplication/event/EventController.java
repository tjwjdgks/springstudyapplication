package seo.study.studyspringapplication.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import seo.study.studyspringapplication.account.CurrentUser;
import seo.study.studyspringapplication.domain.Account;
import seo.study.studyspringapplication.domain.Enrollment;
import seo.study.studyspringapplication.domain.Event;
import seo.study.studyspringapplication.domain.Study;
import seo.study.studyspringapplication.event.form.EventForm;
import seo.study.studyspringapplication.event.validator.EventValidator;
import seo.study.studyspringapplication.study.StudyRepository;
import seo.study.studyspringapplication.study.StudyService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.*;

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
    @GetMapping("/events")
    public String viewStudyEvents(@CurrentUser Account account, @PathVariable String path, Model model){
        Study study = studyService.getStudy(path);
        model.addAttribute(study);
        model.addAttribute(account);
        List<Event> events = eventRepository.findByStudyOrderByStartDateTime(study);
        Map<Boolean, List<Event>> isOldEventOrNewEventsMap = events.stream().collect(partitioningBy(event -> event.getEndDateTime().isBefore(LocalDateTime.now())));
        List<Event> oldEvents = isOldEventOrNewEventsMap.get(true);
        List<Event> newEvents = isOldEventOrNewEventsMap.get(false);
        model.addAttribute("oldEvents",oldEvents);
        model.addAttribute("newEvents",newEvents);
        return "study/events";

    }
    @GetMapping("/events/{eventId}/edit")
    public String editFormEvents(@CurrentUser Account account,@PathVariable String path, @PathVariable Long eventId, Model model){
        Study study = studyService.getStudyToUpdate(account, path);
        Event event = eventRepository.findById(eventId).orElseThrow();
        model.addAttribute(study);
        model.addAttribute(account);
        model.addAttribute(event);
        model.addAttribute(modelMapper.map(event,EventForm.class));
        return "event/update-form";
    }
    @PostMapping("/events/{eventId}/edit")
    public String editEvents(@CurrentUser Account account, @PathVariable String path, @PathVariable Long eventId,
                             @Valid EventForm eventForm, Errors errors, Model model){
        Study study = studyService.getStudyToUpdate(account, path);
        Event event = eventRepository.findById(eventId).orElseThrow();
        eventForm.setEventType(event.getEventType());
        eventValidator.validateUpdateForm(eventForm,event,errors);

        if(errors.hasErrors()){
            model.addAttribute(account);
            model.addAttribute(study);
            model.addAttribute(event);
            return "event/update-form";
        }
        eventService.updateEvent(event,eventForm);
        return "redirect:/study/" + study.getEncodePath(path) +  "/events/" + event.getId();
    }
    @DeleteMapping("/events/{eventId}")
    public String deleteEvent(@CurrentUser Account account, @PathVariable String path, @PathVariable Long eventId){
        Study study = studyService.getStudyToUpdateStatus(account, path);
        eventRepository.delete(eventRepository.findById(eventId).orElseThrow());
        return "redirect:/study/" + study.getEncodePath(path) + "/events";

    }
    @PostMapping("/events/{eventId}/enroll")
    public String enrollEvent(@CurrentUser Account account, @PathVariable String path, @PathVariable Long eventId){
        Study study = studyService.getStudyToEnroll(path);
        eventService.newEnrollment(eventRepository.findById(eventId).orElseThrow(),account);
        return "redirect:/study/" + study.getEncodePath(path) + "/events/" + eventId;
    }
    @PostMapping("/events/{eventId}/leave")
    public String leaveEvent(@CurrentUser Account account, @PathVariable String path, @PathVariable Long eventId){
        Study study = studyService.getStudyToEnroll(path);
        eventService.cancelEnrollment(eventRepository.findById(eventId).orElseThrow(),account);
        return "redirect:/study/" + study.getEncodePath(path) + "/events/" + eventId;
    }
    @GetMapping("/events/{eventId}/enrollments/{enrollmentId}/accept")
    public String acceptEnrollment(@CurrentUser Account account,@PathVariable String path, @PathVariable("eventId") Event event,
                                   @PathVariable("enrollmentId") Enrollment enrollment){
        Study study = studyService.getStudyToUpdate(account,path);
        eventService.acceptEnrollment(event,enrollment);
        return "redirect:/study/" + study.getEncodePath(path) + "/events/" + event.getId();

    }
    @GetMapping("/events/{eventId}/enrollments/{enrollmentId}/reject")
    public String rejectEnrollment(@CurrentUser Account account,@PathVariable String path, @PathVariable("eventId") Event event,
                                   @PathVariable("enrollmentId") Enrollment enrollment){
        Study study = studyService.getStudyToUpdate(account,path);
        eventService.rejectEnrollment(event,enrollment);
        return "redirect:/study/" + study.getEncodePath(path) + "/events/" + event.getId();

    }
    @GetMapping("/events/{eventId}/enrollments/{enrollmentId}/checkin")
    public String checkInEnrollment(@CurrentUser Account account,@PathVariable String path, @PathVariable("eventId") Event event,
                                    @PathVariable("enrollmentId") Enrollment enrollment){
        Study study = studyService.getStudyToUpdate(account,path);
        eventService.checkInEnrollment(event,enrollment);
        return "redirect:/study/" + study.getEncodePath(path) + "/events/" + event.getId();

    }
    @GetMapping("/events/{eventId}/enrollments/{enrollmentId}/cancel-checkin")
    public String cancelCheckInEnrollment(@CurrentUser Account account,@PathVariable String path, @PathVariable("eventId") Event event,
                                          @PathVariable("enrollmentId") Enrollment enrollment){

        Study study = studyService.getStudyToUpdate(account,path);
        eventService.cancelCheckInEnrollment(event,enrollment);
        return "redirect:/study/" + study.getEncodePath(path) + "/events/" + event.getId();
    }
}
