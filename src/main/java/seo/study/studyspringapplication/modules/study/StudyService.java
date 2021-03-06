package seo.study.studyspringapplication.modules.study;


import lombok.RequiredArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import seo.study.studyspringapplication.modules.account.Account;
import seo.study.studyspringapplication.modules.study.event.StudyCreatedEvent;
import seo.study.studyspringapplication.modules.study.event.StudyUpdateEvent;
import seo.study.studyspringapplication.modules.tag.Tag;
import seo.study.studyspringapplication.modules.tag.TagRepository;
import seo.study.studyspringapplication.modules.zone.Zone;
import seo.study.studyspringapplication.modules.study.form.StudyDescriptionForm;
import seo.study.studyspringapplication.modules.zone.ZoneRepository;

import java.util.HashSet;
import java.util.Random;

import static seo.study.studyspringapplication.modules.study.form.StudyForm.VALID_PATH_PATTERN;

@Service
@Transactional // 모든 퍼블릭 메소드에 Transactional 효과
@RequiredArgsConstructor
public class StudyService {

    private final StudyRepository repository;
    private final ModelMapper modelMapper;
    private final ApplicationEventPublisher eventPublisher;
    // test 용
    private final ZoneRepository zoneRepository;

    public Study creatNewStudy(Study study, Account account) {
        Study newStudy = repository.save(study);
        newStudy.addManager(account);
        return newStudy;
    }
    public Study getStudyToUpdate(Account account, String path){
        Study study = this.getStudy(path);
        checkIfManger(account, study);
        return study;
    }
    public Study getStudy(String path){
        Study study = this.repository.findByPath(path);
        checkIfExistingStudy(path, study);
        return study;
    }

    public void updateStudyDescription(Study study, StudyDescriptionForm studyDescriptionForm) {
        modelMapper.map(studyDescriptionForm, study);
        eventPublisher.publishEvent(new StudyUpdateEvent(study,"스터디 소개를 수정했습니다"));
    }

    public void enableStudyBanner(Study study) {
        study.setUseBanner(true);
    }

    public void disableStudyBanner(Study study) {
        study.setUseBanner(false);
    }

    public void updateStudyImage(Study study, String image) {
        study.setImage(image);
    }

    public void addTag(Study study, Tag tag) {
        study.getTags().add(tag);
    }

    public void removeTag(Study study, Tag tag) {
        study.getTags().remove(tag);
    }
    public void addZone(Study study, Zone zone){
        study.getZones().add(zone);
    }

    public void removeZone(Study study, Zone zone) {
        study.getZones().remove(zone);
    }

    public Study getStudyToUpdateTag(Account account, String path) {
        Study study = repository.findStudyWithTagsByPath(path);
        checkIfExistingStudy(path,study);
        checkIfManger(account,study);
        return study;
    }

    public Study getStudyToUpdateZone(Account account, String path) {
        Study study = repository.findStudyWithZonesByPath(path);
        checkIfExistingStudy(path,study);
        checkIfManger(account,study);
        return study;
    }

    public Study getStudyToUpdateStatus(Account account, String path) {
        Study study = repository.findStudyWithManagersByPath(path);
        checkIfExistingStudy(path,study);
        checkIfManger(account,study);
        return study;
    }
    public Study getStudyToEnroll(String path) {
        Study study = repository.findOnlyStudyByPath(path);
        checkIfExistingStudy(path, study);
        return study;
    }
    private void checkIfManger(Account account, Study study) {
        if(!study.isManagedBy(account)){
            throw new AccessDeniedException("해당 기능을 사용할 수 없습니다");
        }
    }
    private void checkIfExistingStudy(String path, Study study) {
        if(study == null){
            throw new IllegalArgumentException(path +"에 해당하는 스터디가 없습니다");
        }
    }

    public void publish(Study study) {
        study.publish();
        this.eventPublisher.publishEvent(new StudyCreatedEvent(study));
    }

    public void close(Study study) {
        study.close();
        eventPublisher.publishEvent(new StudyUpdateEvent(study,"스터디 종료 했습니다"));

    }

    public void startRecruit(Study study) {
        study.startRecruit();
        eventPublisher.publishEvent(new StudyUpdateEvent(study,"스터디 인원 모집을 시작했습니다"));

    }

    public void stopRecruit(Study study) {
        study.stopRecruit();
        eventPublisher.publishEvent(new StudyUpdateEvent(study,"스터디 인원 모집을 중단했습니다"));

    }

    public boolean isValidPath(String newPath) {
        if(!newPath.matches(VALID_PATH_PATTERN)){
            return false;
        }
        return !repository.existsByPath(newPath);
    }
    public void updateStudyPath(Study study, String newPath) {
        study.setPath(newPath);
    }

    public boolean isValidTitle(String newTitle) {
        return newTitle.length()<=50;
    }

    public void updateStudyTitle(Study study, String newTitle) {
        study.setTitle(newTitle);
    }

    public void remove(Study study) {
        if(study.isRemovable()){
            repository.delete(study);
        }
        else{
            throw new IllegalArgumentException("스터디를 삭제할 수 없습니다.");
        }
    }

    public void addMember(Study study, Account account) {
        study.addMember(account);
    }

    public void removeMember(Study study, Account account) {
        study.removeMember(account);
    }

    public void generateTestStudies(Account account) {
        for(int i=0; i<30;i++){
            String radomvalue = RandomString.make(5);
            Study study = Study.builder()
                    .title("테스트 스터디" + radomvalue)
                    .path("test-" + i)
                    .shortDescription("테스트용 스터디 입니다")
                    .fullDescription("test")
                    .tags(new HashSet<>())
                    .zones(new HashSet<>())
                    .managers(new HashSet<>())
                    .build();
            study.publish();
            Study newStudy = this.creatNewStudy(study, account);
            //Andong,안동시,North Gyeongsang
            Zone zone = zoneRepository.findByCityAndProvince("Andong","North Gyeongsang");
            newStudy.getZones().add(zone);

        }
    }
}
