package com.example.clienteventservice;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableJpaAuditing
@SecurityScheme(
        name = "auth",
        type = SecuritySchemeType.OAUTH2,
        in = SecuritySchemeIn.HEADER,
        flows = @OAuthFlows(
                clientCredentials = @OAuthFlow(
                        tokenUrl = "https://keycloak-fintrack.sythorng.site/auth/realms/USER_SERVICE/protocol/openid-connect/token"
                ),
                password = @OAuthFlow(
                        tokenUrl = "https://keycloak-fintrack.sythorng.site/auth/realms/USER_SERVICE/protocol/openid-connect/token"
                )
        )
)
public class ClientEventServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientEventServiceApplication.class, args);
    }
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
