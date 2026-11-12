
package com.pawan.moviebooking;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI movieBookingOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("Movie Booking API")
                        .description(
                            "REST APIs for movie discovery and movie ticket booking."
                        )
                        .version("1.0.0"));
    }
}
