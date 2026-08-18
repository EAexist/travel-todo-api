package com.matchalab.travel_todo_api.config.security;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.matchalab.travel_todo_api.service.UserAccountService;
import java.util.Collections;
import java.util.List;
import javax.sql.DataSource;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.server.resource.authentication.JwtIssuerAuthenticationManagerResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@ConfigurationProperties(prefix = "app.cors")
public class WebSecurityConfig {

  private static final String ANONYMOUS_AUTH_PATH = "/auth/web-browser";
  private static final String ADMIN_AUTH_PATH = "/auth/admin";
  @Setter private List<String> allowedOrigins;
  @Value("${spring.security.oauth2.resourceserver.jwt.kakao.issuer-uri}")
  private String kakaoIssuerUri;
  @Value("${spring.security.oauth2.resourceserver.jwt.google.issuer-uri}")
  private String googleIssuerUri;
  @Autowired private DataSource dataSource;

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    // https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/multitenancy.html#_resolving_the_tenant_by_claim

    JwtIssuerAuthenticationManagerResolver authenticationManagerResolver =
        JwtIssuerAuthenticationManagerResolver.fromTrustedIssuers(kakaoIssuerUri, googleIssuerUri);

    http.securityMatcher(request -> request.getHeader("X-Device-Type") != null)
        .csrf((csrf) -> csrf.ignoringRequestMatchers("/**"))
        // if Spring MVC is on classpath and no CorsConfigurationSource is provided,
        // Spring Security will use CORS configuration provided to Spring MVC
        .cors(Customizer.withDefaults())
        /* https://spring.io/guides/gs/securing-web */
        .authorizeHttpRequests(
            (requests) -> requests.requestMatchers("/**").permitAll().anyRequest().authenticated())
        .oauth2ResourceServer(
            oauth2 -> oauth2.authenticationManagerResolver(authenticationManagerResolver));

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
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
    configuration.setAllowedHeaders(
        List.of("Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin"));
    configuration.setExposedHeaders(List.of("Location"));
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  @Bean
  SecurityFilterChain webBrowserRequestSecurityFilterChain(
      HttpSecurity http,
      AnonymousUserLoginFilter anonymousUserLoginFilter,
      AdminLoginFilter adminLoginFilter)
      throws Exception {
    http
        // .csrf((csrf) -> csrf
        // .ignoringRequestMatchers("/**"))
        .csrf(AbstractHttpConfigurer::disable)
        .cors(Customizer.withDefaults())
        .addFilterAt(anonymousUserLoginFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterAfter(adminLoginFilter, AnonymousUserLoginFilter.class)
        .authorizeHttpRequests(
            (authorize) ->
                authorize
                    .requestMatchers("/actuator/health/**")
                    .permitAll()
                    .requestMatchers("/auth/**")
                    .permitAll()
                    //
                    // .requestMatchers("/proxy/place/autocomplete/json").permitAll()
                    .anyRequest()
                    .authenticated())
        .sessionManagement(session -> session.maximumSessions(1))
        .exceptionHandling(
            exceptions ->
                exceptions.authenticationEntryPoint(
                    new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));
    return http.build();
  }

  @Bean
  AuthenticationManager authenticationManager(UserDetailsService userDetailsService) {
    UsernameOnlyAuthenticationProvider authenticationProvider =
        new UsernameOnlyAuthenticationProvider(userDetailsService);

    return new ProviderManager(authenticationProvider);
  }

  @Bean
  AnonymousUserLoginFilter anonymousUserLoginFilter(
      UserAccountService userAccountService, UserDetailsService userDetailsService) {
    return new AnonymousUserLoginFilter(
        userAccountService, userDetailsService, ANONYMOUS_AUTH_PATH);
  }

  @Bean
  public GoogleIdTokenVerifier googleIdTokenVerifier(
      @Value("${app.google.web-client-id}") String webClientId) {
    return new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
        .setAudience(Collections.singletonList(webClientId))
        .build();
  }

  @Bean
  AdminLoginFilter adminLoginFilter(
      UserAccountService userAccountService,
      UserDetailsService userDetailsService,
      GoogleIdTokenVerifier verifier) {
    return new AdminLoginFilter(userAccountService, userDetailsService, verifier, ADMIN_AUTH_PATH);
  }
}
