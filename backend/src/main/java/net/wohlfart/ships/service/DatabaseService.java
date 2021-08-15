package net.wohlfart.ships.service;


import lombok.RequiredArgsConstructor;
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

    final EntityManager entityManager;

    final static Class<?>[] ALL_ENTITIES = {
        Engine.class, EngineType.class, Position.class, Ship.class, Speed.class, Owner.class
    };

    @Transactional
    public void emptyTable(@Nullable String entityName) throws ClassNotFoundException {
        if (!StringUtils.hasText(entityName)) {
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

}
