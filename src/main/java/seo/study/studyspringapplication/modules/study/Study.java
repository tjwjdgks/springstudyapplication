package seo.study.studyspringapplication.modules.study;

import lombok.*;
import org.springframework.data.jpa.repository.EntityGraph;
import seo.study.studyspringapplication.modules.account.Account;
import seo.study.studyspringapplication.modules.account.UserAccount;
import seo.study.studyspringapplication.modules.tag.Tag;
import seo.study.studyspringapplication.modules.zone.Zone;

import javax.persistence.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

// many는 lazy one은 eager
@NamedEntityGraph(name = "Study.withAll",attributeNodes = {
        @NamedAttributeNode("tags"),
        @NamedAttributeNode("zones"),
        @NamedAttributeNode("managers"),
        @NamedAttributeNode("members")})
@NamedEntityGraph(name="Study.withTagAndManger",attributeNodes = {
        @NamedAttributeNode("tags"),
        @NamedAttributeNode("managers")})
@NamedEntityGraph(name="Study.withZoneAndManger",attributeNodes = {
        @NamedAttributeNode("zones"),
        @NamedAttributeNode("managers")})
@NamedEntityGraph(name="Study.withManagers", attributeNodes = {
        @NamedAttributeNode("managers")
})
@NamedEntityGraph(name="Study.withMembers",attributeNodes = {
        @NamedAttributeNode("members")
})
@NamedEntityGraph(name ="Study.withTagsAndZones", attributeNodes = {
  @NamedAttributeNode("zones"),
  @NamedAttributeNode("tags")
})
@Entity
@Setter @Getter @EqualsAndHashCode(of = "id")
@Builder @NoArgsConstructor @AllArgsConstructor
public class Study {

    @Id @GeneratedValue
    private Long id;

    @ManyToMany
    private Set<Account> managers = new HashSet<>();

    @ManyToMany
    private Set<Account> members = new HashSet<>();

    @Column(unique = true)
    private String path;

    private String title;

    private String shortDescription;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String fullDescription;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String image;

    @ManyToMany
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany
    private Set<Zone> zones = new HashSet<>();

    private LocalDateTime publishedDateTime;

    private LocalDateTime closedDateTime;

    private LocalDateTime recruitingUpdatedDateTime;

    private boolean recruiting;

    private boolean published;

    private boolean closed;

    private boolean useBanner;

    private int memberCount;

    public void addManager(Account account) {
        this.managers.add(account);
    }

    public boolean isJoinable(UserAccount userAccount){
        Account account = userAccount.getAccount();
        return this.isPublished() && this.isRecruiting()
                && !this.members.contains(account) && !this.managers.contains(account);
    }
    public boolean isMember(UserAccount userAccount){
        return this.members.contains(userAccount.getAccount());
    }
    public boolean isManager(UserAccount userAccount){
        return this.managers.contains(userAccount.getAccount());
    }
    public String getImage() {
        return image != null ? image : "/images/default_banner.png";
    }

    public void publish() {
        if(!this.closed && !this.published){
            this.published = true;
            this.publishedDateTime = LocalDateTime.now();
        }
        else{
            throw new RuntimeException("스터디를 공개할 수 없는 상태 입니다. 이미 스터디를 공개했거나 종료했습니다");
        }

    }

    public void close() {
        if(this.published && !this.closed){
            this.closed = true;
            this.closedDateTime = LocalDateTime.now();
        }
        else{
            throw new RuntimeException("스터디를 종료할 수 없는 상태 입니다. 이미 스터디를 공개하지 않았거나 이미 종료했습니다");

        }
    }

    public boolean canUpdateRecruiting() {

        return this.published && (this.recruitingUpdatedDateTime== null || this.recruitingUpdatedDateTime.isBefore(LocalDateTime.now().minusHours(1)));
    }

    public void startRecruit() {
        if(canUpdateRecruiting()){
            this.recruiting = true;
            this.recruitingUpdatedDateTime = LocalDateTime.now();
        }
        else{
            throw new RuntimeException("인원 모집을 시작할 수 없습니다. 스터디를 공개하거나 한 시간 뒤 다시 시도하세요.");
        }
    }

    public void stopRecruit() {
        if(canUpdateRecruiting()){
            this.recruiting = false;
            this.recruitingUpdatedDateTime = LocalDateTime.now();
        }
        else {
            throw new RuntimeException("인원 모집을 멈출 수 없습니다. 스터디를 공개하거나 한 시간 뒤 다시 시도하세요.");
        }
    }

    public boolean isRemovable() {
        return !this.published;
    }

    public void addMember(Account account) {
        this.getMembers().add(account);
        this.memberCount++;
    }
    public void removeMember(Account account) {
        this.getMembers().remove(account);
        this.memberCount--;
    }
    public String getEncodePath(String path){
        return URLEncoder.encode(path, StandardCharsets.UTF_8);
    }
    public String getEncodePath(){
        return URLEncoder.encode(this.path, StandardCharsets.UTF_8);
    }
    public boolean isManagedBy(Account account) {
        return this.getManagers().contains(account);
    }
}
