package ch.fdb.zythopedia.entity;

import javax.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

@Entity
@Table(name = "message")
@Getter
@Setter
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize=1)
    @Column(name = "ID")
    private Long id;
    @Column(name = "TITLE", nullable = false)
    private String title;
    @Column(name = "CONTENT", nullable = false)
    private String content;
}
