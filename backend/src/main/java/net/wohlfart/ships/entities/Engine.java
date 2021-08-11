package net.wohlfart.ships.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NamedQueries({
        @NamedQuery(
                name = Engine.FIND_ENGINE_BY_ENGINE_ID,
                query = "FROM Engine engine WHERE engine.engineId = :engineId"
        )
})
@Data
@Entity
@Table(name = "engine")
@EqualsAndHashCode(of = {"id"})
@NoArgsConstructor
public class Engine {

    public static final String FIND_ENGINE_BY_ENGINE_ID = "FIND_ENGINE_BY_ENGINE_ID";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String engineId;

    @ManyToOne
    @JoinColumn(name = "engine_type_id")
    private EngineType engineType;

}
