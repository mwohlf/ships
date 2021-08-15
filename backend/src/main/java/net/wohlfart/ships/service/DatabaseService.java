package net.wohlfart.ships.service;


import lombok.RequiredArgsConstructor;
import net.wohlfart.ships.dtos.TableDetailsResponse;
import net.wohlfart.ships.entities.*;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaDelete;
import javax.transaction.Transactional;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class DatabaseService {

    static final String ALL = "ALL";

    final EntityManager entityManager;

    // because we have foreighn key refereneces: the order of delete is important
    static final Class<?>[] ALL_ENTITIES = {
        Speed.class, Position.class, Engine.class, EngineType.class, Ship.class, Owner.class
    };

    @Transactional
    public void emptyTable(@Nullable String entityName) throws ClassNotFoundException {
        if (!StringUtils.hasText(entityName) || ALL.equalsIgnoreCase(entityName)) {
            Arrays.stream(ALL_ENTITIES)
                .forEach(this::emptyTable);
        } else {
            emptyTable(Class.forName(entityName));
        }
    }

    @Transactional
    public <T> void emptyTable(@NonNull Class<T> clazz) {
        CriteriaDelete<T> delete = entityManager.getCriteriaBuilder().createCriteriaDelete(clazz);
        delete.from(clazz);
        entityManager.createQuery(delete).executeUpdate();
    }

    @Transactional
    public TableDetailsResponse checkTableDetails() {
        TableDetailsResponse result = new TableDetailsResponse();
        Arrays.stream(ALL_ENTITIES)
            .forEach(
                entityName -> {
                    result.getTableDetails()
                        .add(new TableDetailsResponse.SingeTableDetails(
                            entityName.getSimpleName(),
                            entityManager.createQuery("SELECT count(e) FROM " + entityName.getSimpleName() + " e ", Long.class).getSingleResult()
                        ));
                }
            );
        return result;
    }
}
