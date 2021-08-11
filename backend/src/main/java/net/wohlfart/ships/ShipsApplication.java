package net.wohlfart.ships;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ShipsProperties.class)
public class ShipsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShipsApplication.class, args);
    }

}
