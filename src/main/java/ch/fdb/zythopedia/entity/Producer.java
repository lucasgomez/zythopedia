package ch.fdb.zythopedia.entity;

import javax.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

@Entity
@Table(name = "PRODUCER", uniqueConstraints = {@UniqueConstraint(name = "UK_PRODUCER__NAME", columnNames = {"NAME"})})
@Getter
@Setter
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Producer implements NamedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize=1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "ORIGIN_FK", foreignKey = @ForeignKey(name = "FK_PRODUCER_ORIGIN__ORIGIN"))
    private Origin origin;

    @OneToMany(mappedBy = "producer")
    private List<Drink> drinks;

}
