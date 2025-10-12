package com.matchalab.trip_todo_api.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtIssuerAuthenticationManagerResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Value("#{'${cors.allowed-origins}'.split(',')}")
    private List<String> allowedOrigins;

    @Value("${spring.security.oauth2.resourceserver.jwt.kakao.issuer-uri}")
    private String kakaoIssuerUri;

    @Value("${spring.security.oauth2.resourceserver.jwt.google.issuer-uri}")
    private String googleIssuerUri;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/multitenancy.html#_resolving_the_tenant_by_claim

        JwtIssuerAuthenticationManagerResolver authenticationManagerResolver = JwtIssuerAuthenticationManagerResolver
                .fromTrustedIssuers(kakaoIssuerUri, googleIssuerUri);

        http
                .csrf((csrf) -> csrf
                        .ignoringRequestMatchers("/**"))
                // if Spring MVC is on classpath and no CorsConfigurationSource is provided,
                // Spring Security will use CORS configuration provided to Spring MVC
                .cors(Customizer.withDefaults())
                /* https://spring.io/guides/gs/securing-web */
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/**").permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .authenticationManagerResolver(authenticationManagerResolver));

        // .oauth2ResourceServer(oauth2 -> oauth2
        // .jwt(jwt -> jwt
        // .jwkSetUri("https://idp.example.com/.well-known/jwks.json")
        // )
        // )
        ;
        // .oauth2Login(Customizer.withDefaults());
        // .oauth2Login();
        /*
         * https://docs.spring.io/spring-security/reference/servlet/exploits/csrf.html#
         * csrf-token-repository-cookie
         */
        // .csrf((csrf) -> csrf
        // .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedOrigins);

        configuration.setAllowCredentials(true);
        // configuration.setAllowedOrigins(List.of("http://localhost:8081"));
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList("Location"));
        // configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}