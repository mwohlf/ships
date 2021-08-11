package net.wohlfart.ships.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;


@OpenAPIDefinition(
        info = @Info(
                title = "Ships API",
                description = "" +
                        "The backend for the Ships Portal",
                version = "0.0.1"
        )
)
public class OpenAPIConfiguration {

    // see: http://localhost:8080/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config

}
