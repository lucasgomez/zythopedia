package ch.fdb.zythopedia.entity;

import ch.fdb.zythopedia.enums.Strength;
import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "DRINK", uniqueConstraints = {@UniqueConstraint(name = "UK_DRINK__NAME_PRODUCER", columnNames = {"NAME", "PRODUCER_FK"})})
@Getter
@Setter
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Drink {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "PRODUCER_FK", foreignKey = @ForeignKey(name = "FK_DRINK__PRODUCER"))
    private Producer producer;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "ABV")
    private Double abv;

    @ManyToOne
    @JoinColumn(name = "COLOR_FK", foreignKey = @ForeignKey(name = "FK_DRINK__COLOR"))
    private Color color;

    @ManyToOne
    @JoinColumn(name = "STYLE_FK", foreignKey = @ForeignKey(name = "FK_DRINK__STYLE"))
    private Style style;

    @Enumerated(value = EnumType.STRING)
    private Strength sourness;

    @Enumerated(value = EnumType.STRING)
    private Strength bitterness;

    @Enumerated(value = EnumType.STRING)
    private Strength sweetness;

    @Enumerated(value = EnumType.STRING)
    private Strength hoppiness;

    @OneToMany(mappedBy = "drink")
    private List<BoughtDrink> boughtDrinks;
}
