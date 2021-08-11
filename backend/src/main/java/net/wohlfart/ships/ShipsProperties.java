package net.wohlfart.ships;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "net.wohlfart.ships")
public class ShipsProperties {

}
