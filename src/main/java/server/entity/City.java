package server.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "City")
@NamedQueries({
        @NamedQuery(name = "City.getAll", query = "SELECT c FROM City c"),
        @NamedQuery(name = "City.getByName", query = "SELECT c FROM City c Where c.name = :value")
})
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Setter
    @Getter
    @Column(length = 200)
    private String name;
}
