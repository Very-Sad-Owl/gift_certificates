package ru.clevertec.ecl.entity.commitlogentities;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@Entity
@DynamicUpdate
public class NodeStatus extends AbstractCommitLog {

    @NotBlank
    @Column(nullable = false)
    private String nodeTitle;

    @Column(nullable = false)
    private LocalDateTime lastUpdated = LocalDateTime.now();

    @Column(nullable = false)
    private boolean nodeStatus = true;
}
