package ch.fdb.zythopedia.entity;

import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Entity
@Table(
        name = "SERVICE",
        uniqueConstraints = {@UniqueConstraint(
                name = "UK_SERVICE__BOUGHT_DRINK_VOLUME_IN_CL",
                columnNames = {"BOUGHT_DRINK_FK", "VOLUME_IN_CL"})})
@Getter
@Setter
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "ID")
    private Long id;

    @Column(name = "SELLING_PRICE")
    private Double sellingPrice;

    @Column(name = "VOLUME_IN_CL", nullable = false)
    private Long volumeInCl;

    @ManyToOne
    @JoinColumn(name = "BOUGHT_DRINK_FK", nullable = false, foreignKey = @ForeignKey(name = "FK_SERVICE__BOUGHT_DRINK"))
    private BoughtDrink boughtDrink;
}
