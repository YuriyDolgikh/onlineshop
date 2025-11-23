package org.onlineshop.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the security filter chain for the application, defining how requests are authenticated
     * and authorized. Includes configurations for CORS, CSRF, session management, request authorization,
     * exception handling, and JWT token-based authentication.
     *
     * @param http the {@link HttpSecurity} object used to configure the security settings.
     * @return a {@link SecurityFilterChain} object representing the application's security filter chain.
     * @throws Exception if an error occurs during the security configuration.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers("/").permitAll()
                        .requestMatchers("/index.html").permitAll()
                        .requestMatchers("/css/**").permitAll()
                        .requestMatchers("/js/**").permitAll()

                        .requestMatchers("/*.html").permitAll()

                        .requestMatchers("/v1/users/**").permitAll()
                        .requestMatchers("/v1/categories/**").permitAll()
                        .requestMatchers("/v1/products/**").hasAnyRole("ADMIN", "MANAGER", "USER")
                        .requestMatchers("/v1/carts/**").hasAnyRole("ADMIN", "MANAGER", "USER")
                        .requestMatchers("/v1/cartItems/**").hasAnyRole("ADMIN", "MANAGER", "USER")
                        .requestMatchers("/v1/favorites/**").hasAnyRole("ADMIN", "MANAGER", "USER")
                        .requestMatchers("/v1/orders/**").hasAnyRole("ADMIN", "MANAGER", "USER")
                        .requestMatchers("/v1/orderItems/**").hasAnyRole("ADMIN", "MANAGER", "USER")
                        .requestMatchers("/v1/statistics/**").hasRole("ADMIN")
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex.authenticationEntryPoint(new RestAuthEntryPoint())
                        .accessDeniedHandler(new RestAccessDeniedHandler())
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configures CORS (Cross-Origin Resource Sharing) settings for the application.
     * It allows all origins, methods, and headers to ensure that cross-origin requests
     * are handled accordingly.
     *
     * @return a {@link CorsConfigurationSource} object with the configured CORS settings.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
