package seo.study.studyspringapplication.domain;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter @Setter @EqualsAndHashCode(of="id")
@Builder @NoArgsConstructor @AllArgsConstructor
public class Tag {

    @GeneratedValue @Id
    private Long id;

    @Column(unique = true, nullable = false)
    private String title;
}
