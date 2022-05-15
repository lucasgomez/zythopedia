package ch.fdb.zythopedia.entity;

import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Entity
@Table(name = "EDITION", uniqueConstraints = {@UniqueConstraint(name = "UK_EDITION__NAME", columnNames = {"NAME"})})
@Getter
@Setter
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Edition {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME", nullable = false)
    private Long name;

}
