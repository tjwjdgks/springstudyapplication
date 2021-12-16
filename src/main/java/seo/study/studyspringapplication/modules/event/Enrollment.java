package seo.study.studyspringapplication.modules.event;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import seo.study.studyspringapplication.modules.account.Account;

import javax.persistence.*;
import java.time.LocalDateTime;


@NamedEntityGraph(
        name = "Enrollment.withEventAndStudy",
        attributeNodes = {
                @NamedAttributeNode(value = "event",subgraph = "study")
        },
        subgraphs = @NamedSubgraph(name = "study",attributeNodes = @NamedAttributeNode("study"))
)
@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
public class Enrollment {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private Event event;

    @ManyToOne
    private Account account;

    private LocalDateTime enrolledAt;

    private boolean accepted;

    private boolean attended;
}
