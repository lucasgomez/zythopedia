package ch.fdb.zythopedia.entity;

import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
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
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
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
