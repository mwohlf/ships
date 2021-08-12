package net.wohlfart.ships.entities;


import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.Instant;
import java.util.Date;

@Data
@Entity
@Table(name = "position")
@NoArgsConstructor
public class Position implements Serializable {

    @Id
    private Long shipId;

    @Id
    private Instant timestamp;

    private float latitude;

    private float longitude;

}
