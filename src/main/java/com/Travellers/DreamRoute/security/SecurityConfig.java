package com.Travellers.DreamRoute.security;

import com.Travellers.DreamRoute.security.jwt.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers(HttpMethod.GET, "/health").permitAll()
                                .requestMatchers(HttpMethod.GET, "/actuator/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/register").permitAll()
                                .requestMatchers(HttpMethod.POST, "/login").permitAll()
                                .requestMatchers(HttpMethod.GET, "/destinations").permitAll()
                                .requestMatchers(HttpMethod.GET, "/destinations/{id}").permitAll()
                                .requestMatchers(HttpMethod.GET, "/destinations/user/{id}").permitAll()
                                .requestMatchers(HttpMethod.POST, "/destinations").hasAnyRole("USER", "ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/destinations").hasAnyRole("USER", "ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/destinations").hasAnyRole("USER", "ADMIN")
                                .requestMatchers(HttpMethod.GET, "/users/all").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/users/{id}").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/users/username/{username}").hasAnyRole("USER", "ADMIN")
                                .requestMatchers(HttpMethod.POST, "/users/create").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/users/update/**").hasAnyRole("USER", "ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/users/delete/{id}").hasAnyRole("ADMIN", "USER")
                                .requestMatchers(HttpMethod.GET, "/roles").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/roles/{id}").hasRole("ADMIN")
                                .anyRequest().authenticated()
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .sessionManagement(manager-> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}