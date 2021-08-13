package net.wohlfart.ships.entities;


import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Date;

@Data
@Entity
@Table(name = "speed")
@NoArgsConstructor
public class Speed implements Serializable {

    @Id
    @ManyToOne
    @MapKeyColumn(name = "ship_id")
    private Ship ship;

    @Id
    private Instant timestamp;

    // 1 m/s = 1.94384 knots
    // 10 knots = 18.52 km/h
    private float knots;

}
