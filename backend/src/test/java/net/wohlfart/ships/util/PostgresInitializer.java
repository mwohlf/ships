package net.wohlfart.ships.util;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    static PostgreSQLContainer<?> CONTAINER;

    static {
        CONTAINER = new PostgreSQLContainer<>("postgres:11")
            .withDatabaseName("testdb")
            .withUsername("testdb")
            .withPassword("testdb");
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        CONTAINER.start();
        applicationContext.addApplicationListener(event -> {
            if (event instanceof ContextClosedEvent) {
                CONTAINER.stop();
            }
        });

        TestPropertyValues
            .of("spring.datasource.url=" + CONTAINER.getJdbcUrl())
            .and("spring.datasource.password=" + CONTAINER.getPassword())
            .and("spring.datasource.username=" + CONTAINER.getUsername())
            .applyTo(applicationContext);
    }

}
