package net.wohlfart.ships.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@NamedQuery(
    name = EngineType.FIND_ENGINE_TYPE_BY_NAME,
    query = "FROM EngineType engineType WHERE engineType.name = :engineTypeName"
)
@Data
@Entity
@Table(name = "engine_type")
@EqualsAndHashCode(of = {"id"})
@NoArgsConstructor
public class EngineType {

    public static final String FIND_ENGINE_TYPE_BY_NAME = "FIND_ENGINE_TYPE_BY_NAME";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;    // used in the import CSVs

}
