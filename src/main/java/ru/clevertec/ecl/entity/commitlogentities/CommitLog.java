package ru.clevertec.ecl.entity.commitlogentities;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

/**
 * Entity class for commit log node abstraction.
 *
 * See also {@link javax.persistence.Entity}.
 *
 * @author Olga Mailychko
 *
 */
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@Entity
@DynamicUpdate
@Table(name = "commit_log")
@EqualsAndHashCode(callSuper = false, exclude = {"actionTime", "performedOnNode", "jsonValue"})
@ToString
public class CommitLog extends AbstractEntity {
    @Column
    @Enumerated(EnumType.STRING)
    private Action action;
    @Column
    private String tableTitle;
    @Column(length = 500)
    private String jsonValue;
    @Column
    private LocalDateTime actionTime;
    @Column
    private int performedOnNode;
    @Column
    @Positive
    private long entityId;
}
