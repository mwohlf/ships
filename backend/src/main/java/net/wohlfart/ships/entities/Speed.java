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
@Table(name = "speed")
@NoArgsConstructor
public class Speed implements Serializable {

    @Id
    private Long shipId;

    @Id
    private Instant timestamp;

    private int speed;

}
