package net.wohlfart.ships.service;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.wohlfart.ships.entities.*;
import net.wohlfart.ships.upload.UploadContent;
import net.wohlfart.ships.util.PostgresInitializer;


import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.jupiter.api.Disabled;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ResourceUtils;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;


@Testcontainers(disabledWithoutDocker = true)
@ContextConfiguration(
    initializers = PostgresInitializer.class
)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(SpringRunner.class)
public class UploadServiceTest {

    @Autowired
    public UploadService uploadService;

    @Autowired
    public EntityManager entityManager;

    UploadContent uploadContent(String filename) {
        try {
            ByteBuf buffer = Unpooled.copiedBuffer(Files.readAllBytes(ResourceUtils.getFile("classpath:" + filename).toPath()));
            return new UploadContent(filename, buffer);
        } catch (IOException ex) {
            throw new RuntimeException("failure", ex);
        }
    }

    void drop(Class<?> clazz) {
        entityManager.createQuery(" delete from " + clazz.getSimpleName() + " where 1 = 1").executeUpdate();
    }

    long count(Class<?> clazz) {
        return entityManager.createQuery("SELECT count(p) from " + clazz.getSimpleName() + " p", Long.class).getSingleResult().longValue();
    }

    /* this test takes 2 minutes...
    @Test
    public void testPositionDataUpload() {
        long started = System.currentTimeMillis();
        uploadService.uploadDatabaseContent(uploadContent("files/position_data.csv"));
        long time = System.currentTimeMillis() - started;

        assertEquals("0", String.valueOf(count(Engine.class)), "wrong Engine count");
        assertEquals("0", String.valueOf(count(EngineType.class)), "wrong EngineType count");
        assertEquals("0", String.valueOf(count(Owner.class)), "wrong Owner count");
        assertEquals("43212", String.valueOf(count(Position.class)), "wrong Position count");
        assertEquals("11", String.valueOf(count(Ship.class)), "wrong Ship count");
        assertEquals("43212", String.valueOf(count(Speed.class)), "wrong Speed count");
        System.out.printf("runtime: %d%n ms", time);  // 101221 ms here
    }
     */

    @Test
    public void testShipEnginesUpload() {
        long started = System.currentTimeMillis();
        uploadService.uploadDatabaseContent(uploadContent("files/ship_engines.csv"));
        long time = System.currentTimeMillis() - started;

        assertEquals("25", String.valueOf(count(Engine.class)), "wrong Engine count");
        assertEquals("3", String.valueOf(count(EngineType.class)), "wrong EngineType count");
        assertEquals("0", String.valueOf(count(Owner.class)), "wrong Owner count");
        assertEquals("43212", String.valueOf(count(Position.class)), "wrong Position count");
        assertEquals("13", String.valueOf(count(Ship.class)), "wrong Ship count");
        assertEquals("43212", String.valueOf(count(Speed.class)), "wrong Speed count");
        System.out.printf("runtime: %d%n ms", time);
    }

    @Test
    public void testShipsPerOwnerUpload() {
        long started = System.currentTimeMillis();
        uploadService.uploadDatabaseContent(uploadContent("files/ships_per_owner.csv"));
        long time = System.currentTimeMillis() - started;

        assertEquals("25", String.valueOf(count(Engine.class)), "wrong Engine count");
        assertEquals("3", String.valueOf(count(EngineType.class)), "wrong EngineType count");
        assertEquals("5", String.valueOf(count(Owner.class)), "wrong Owner count");
        assertEquals("43212", String.valueOf(count(Position.class)), "wrong Position count");
        assertEquals("13", String.valueOf(count(Ship.class)), "wrong Ship count");
        assertEquals("43212", String.valueOf(count(Speed.class)), "wrong Speed count");
        System.out.printf("runtime: %d%n ms", time);
    }

    @Test
    public void testInsertResponse01() {
        long started = System.currentTimeMillis();
        uploadService.uploadDatabaseContent(uploadContent("responses/response-01.json"));
        long time = System.currentTimeMillis() - started;

        assertEquals("0", String.valueOf(count(Engine.class)), "wrong Engine count");
        assertEquals("0", String.valueOf(count(EngineType.class)), "wrong EngineType count");
        assertEquals("0", String.valueOf(count(Owner.class)), "wrong Owner count");
        assertEquals("12", String.valueOf(count(Position.class)), "wrong Position count");
        assertEquals("1", String.valueOf(count(Ship.class)), "wrong Ship count");
        assertEquals("12", String.valueOf(count(Speed.class)), "wrong Speed count");
        System.out.printf("runtime: %d%n ms", time);
    }

}
