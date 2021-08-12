package net.wohlfart.ships.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@NamedQueries({
        @NamedQuery(
                name = Owner.FIND_OWNER_BY_NAME,
                query = "FROM Owner owner WHERE owner.name = :ownerName"
        )
})
@Data
@Entity
@NoArgsConstructor
@Table(name = "owner")
@EqualsAndHashCode(of = {"id", "name"})
public class Owner {

    public static final String FIND_OWNER_BY_NAME = "FIND_OWNER_BY_NAME";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;    // used in the import CSVs

    @OneToMany
    @JoinColumn(name = "owner_id")
    private Set<Ship> ships = new HashSet<>();

}
