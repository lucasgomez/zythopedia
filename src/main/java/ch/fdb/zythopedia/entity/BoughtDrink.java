package ch.fdb.zythopedia.entity;

import ch.fdb.zythopedia.enums.Availability;
import ch.fdb.zythopedia.enums.ServiceMethod;
import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "BOUGHT_DRINK", uniqueConstraints = {@UniqueConstraint(name = "UK_BOUGHT_DRINK__CODE_EDITION_FK", columnNames = {"CODE", "EDITION_FK"})})
@Getter
@Setter
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoughtDrink {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "ID")
    private Long id;

    @Column(name = "CODE")
    private String code;

    @ManyToOne
    @JoinColumn(name = "EDITION_FK", nullable = false, foreignKey = @ForeignKey(name = "FK_BOUGHT_DRINK__EDITION"))
    private Edition edition;

    @ManyToOne
    @JoinColumn(name = "DRINK_FK", nullable = false, foreignKey = @ForeignKey(name = "FK_BOUGHT_DRINK__DRINK"))
    private Drink drink;

    @Column(name = "BUYING_PRICE", nullable = false)
    private Double buyingPrice;

    @Column(name = "VOLUME_IN_CL")
    private Long volumeInCl;

    @Column(name = "SERVICE_METHOD", nullable = false)
    @Enumerated(EnumType.STRING)
    private ServiceMethod serviceMethod;

    @Column(name = "AVAILABILITY", nullable = false)
    @Enumerated(EnumType.STRING)
    private Availability availability;

    @Column(name = "RETURNABLE")
    private Boolean returnable;

    @OneToMany( mappedBy = "boughtDrink")
    private List<Service> services;

    @Transient
    public boolean isAvailable() {
        return Availability.AVAILABLE.equals(availability);
    }
}
