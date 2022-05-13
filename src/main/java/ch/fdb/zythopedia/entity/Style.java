package ch.fdb.zythopedia.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Entity
@Table(name = "style", uniqueConstraints = {@UniqueConstraint(name = "UK_STYLE__NAME", columnNames = {"NAME"})})
@Getter
@Setter
@Accessors(chain = true)
public class Style {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "ID")
    private long id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

}
