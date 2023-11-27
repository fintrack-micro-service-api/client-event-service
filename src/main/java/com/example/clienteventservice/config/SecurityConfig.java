package com.example.clienteventservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.Collections;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilter(HttpSecurity http) throws Exception {

        http.
                csrf(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(authorize ->
                authorize
                        .requestMatchers("/fintrack-client-event-service/api/v1/clients/**").permitAll()
                        .requestMatchers("/fintrack-client-event-service/api/v1/**").permitAll()
                        .requestMatchers(HttpMethod.PUT,"/fintrack-client-event-service/api/v1/clients/").authenticated()
                        .requestMatchers(HttpMethod.GET,"/fintrack-client-event-service/api/v1/clients/").authenticated()
                        .requestMatchers(
                                "/fintrack-client-event-service/api/v1/file/clients/**",
                                "/fintrack-client-event-service/api/v1/file/clients/**",
                                "/api/v1/auth/clients/**",
                                "/fintrack-client-event-service/api/v1/bank/**",
                                "/fintrack-client-event-service/api/v1/customers/**",
                                "/fintrack-client-event-service/api/v1/transaction/**",
                                "/api/v1/transaction/**"

                        ).permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .anyRequest().authenticated()
        );
        http.oauth2ResourceServer(t -> {
            t.jwt(Customizer.withDefaults());
        });
        http.sessionManagement(t -> t.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();

    }


    @Bean
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(Collections.singletonList("*")); // Allows all origin
        config.setAllowedHeaders(
                Arrays.asList(
                        "X-Requested-With",
                        "Origin",
                        "Content-Type",
                        "Accept",
                        "Authorization",
                        "Access-Control-Allow-Credentials",
                        "Access-Control-Allow-Headers",
                        "Access-Control-Allow-Methods",
                        "Access-Control-Allow-Origin",
                        "Access-Control-Expose-Headers",
                        "Access-Control-Max-Age",
                        "Access-Control-Request-Headers",
                        "Access-Control-Request-Method",
                        "Age",
                        "Allow",
                        "Alternates",
                        "Content-Range",
                        "Content-Disposition",
                        "Content-Description"
                )
        );
        config.setAllowedMethods(
                Arrays.asList("GET", "POST", "PUT", "OPTIONS", "DELETE", "PATCH")
        );
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
