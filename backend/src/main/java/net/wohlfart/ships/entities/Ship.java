package net.wohlfart.ships.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@NamedQuery(
    name = Ship.FIND_SHIP_BY_NAME,
    query = "FROM Ship ship WHERE ship.name = :shipName"
)
@NamedQuery(
    name = Ship.FIND_SHIP_BY_MARINE_TRAFFIC_ID,
    query = "FROM Ship ship WHERE ship.marineTrafficId = :marineTrafficId"
)
@NamedQuery(
    name = Ship.FIND_SHIP_BY_NAME_OR_MMSI,
    query = "FROM Ship ship WHERE ship.name = :shipName OR ship.mmsi = :mmsi"
)
@Data
@Entity
@Table(name = "ship")
@EqualsAndHashCode(of = {"id", "name"})
@NoArgsConstructor
public class Ship {

    public static final String FIND_SHIP_BY_NAME = "FIND_SHIP_BY_NAME";
    public static final String FIND_SHIP_BY_MARINE_TRAFFIC_ID = "FIND_SHIP_BY_MARINE_TRAFFIC_ID";
    public static final String FIND_SHIP_BY_NAME_OR_MMSI = "FIND_SHIP_BY_NAME_OR_MMSI";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Owner owner;

    @OneToMany
    @JoinColumn(name = "ship_id")
    private Set<Engine> engines = new HashSet<>();

    // -- different ways to ID the ship

    @Column(unique = true)
    private String name;    // used in the import CSVs

    @Column(unique = true)
    private Long mmsi;   // Maritime Mobile Service Identity

    @Column(unique = true, name = "marine_traffic_id")
    private String marineTrafficId; // uniquely assigned ID by MarineTraffic for the subject vessel

    private Long imo;    // International Maritime Organization

}
