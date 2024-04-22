package ch.fdb.zythopedia.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

@Entity
@Table(name = "STYLE", uniqueConstraints = {@UniqueConstraint(name = "UK_STYLE__NAME", columnNames = {"NAME"})})
@Getter
@Setter
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Style implements NamedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize=1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @ManyToOne
    @JoinColumn(name = "PARENT_FK", foreignKey = @ForeignKey(name = "FK_STYLE_PARENT"))
    private Style parent;

    @OneToMany(mappedBy = "parent")
    private List<Style> children;

    @OneToMany(mappedBy = "style")
    private List<Drink> drinks;

}
