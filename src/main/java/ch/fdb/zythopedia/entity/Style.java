package ch.fdb.zythopedia.entity;

import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "STYLE", uniqueConstraints = {@UniqueConstraint(name = "UK_STYLE__NAME", columnNames = {"NAME"})})
@Getter
@Setter
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Style {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "ID")
    private long id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @ManyToOne
    @JoinColumn(name = "PARENT_FK", foreignKey = @ForeignKey(name = "FK_STYLE_PARENT"))
    private Style parent;

    @OneToMany(mappedBy = "parent")
    private List<Style> children = new ArrayList<>();

    @OneToMany(mappedBy = "style")
    private List<Drink> drinks = new ArrayList<>();

}
