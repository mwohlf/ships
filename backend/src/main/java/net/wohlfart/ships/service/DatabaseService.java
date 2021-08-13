package net.wohlfart.ships.service;


import lombok.RequiredArgsConstructor;
import net.wohlfart.ships.entities.*;
import net.wohlfart.ships.upload.UploadContent;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.transaction.Transactional;
import java.awt.print.Book;

@Service
@RequiredArgsConstructor
public class DatabaseService {

    final EntityManager entityManager;

    @Transactional
    public void emptyDatabase() {
        new Cleaner<>(Engine.class) . run();
        new Cleaner<>(EngineType.class) . run();
        new Cleaner<>(Position.class) . run();
        new Cleaner<>(Ship.class) . run();
        new Cleaner<>(Speed.class) . run();
        new Cleaner<>(Owner.class) . run();
    }

    class Cleaner<T> {

        private final Class<T> clazz;

        public Cleaner(Class<T> clazz) {
            this.clazz = clazz;
        }

        public void run() {
            CriteriaDelete<T> delete = entityManager.getCriteriaBuilder().createCriteriaDelete(clazz);
            delete.from(clazz);
            entityManager.createQuery(delete).executeUpdate();
        }
    }

}
