package net.wohlfart.ships.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Data
@Entity
@Table(name = "position")
@NoArgsConstructor
public class Position implements Serializable {

    @Id
    @ManyToOne
    @MapKeyColumn(name = "ship_id")
    private Ship ship;

    @Id
    @Column(columnDefinition= "TIMESTAMP WITH TIME ZONE")
    private Instant timestamp;

    private double latitude;

    private double longitude;

}
