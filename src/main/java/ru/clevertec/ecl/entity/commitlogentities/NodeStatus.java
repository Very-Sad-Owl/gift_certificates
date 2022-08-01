package ru.clevertec.ecl.entity.commitlogentities;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * Entity class for node node's status abstraction.
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
@EqualsAndHashCode
@ToString
@Table(name = "node_status")
public class NodeStatus extends AbstractEntity {

    @NotBlank
    @Column(nullable = false, unique = true)
    private String nodeTitle;

    @Column(nullable = false)
    private LocalDateTime lastUpdated = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime recommendedToUpdateFrom;

    @Column(nullable = false)
    private boolean nodeStatus = true;
}
