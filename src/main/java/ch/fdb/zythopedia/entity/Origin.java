package ch.fdb.zythopedia.entity;

import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ORIGIN", uniqueConstraints = {
        @UniqueConstraint(name = "UK_ORIGIN__NAME", columnNames = {"NAME"}),
        @UniqueConstraint(name = "UK_ORIGIN__SHORT_NAME", columnNames = {"SHORT_NAME"})
})
@Getter
@Setter
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Origin implements NamedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "SHORT_NAME", nullable = false, length = 4)
    private String shortName;

    @Column(name = "FLAG")
    private String flag;

    @OneToMany(mappedBy = "origin")
    private List<Producer> producers = new ArrayList<>();
}
