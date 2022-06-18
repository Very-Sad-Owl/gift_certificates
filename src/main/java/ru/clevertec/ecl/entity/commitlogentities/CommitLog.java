package ru.clevertec.ecl.entity.commitlogentities;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@Entity
@DynamicUpdate
public class CommitLog extends AbstractCommitLog {
    @Column
    @Enumerated(EnumType.STRING)
    private Action action;
    @Column
    private String tableTitle;
    @Column
    private String jsonValue;
    @Column
    private LocalDateTime actionTime;
}
