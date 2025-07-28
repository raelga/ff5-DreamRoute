package com.Travellers.DreamRoute.security;

import com.Travellers.DreamRoute.security.jwt.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers(HttpMethod.POST, "/register").permitAll()
                                .requestMatchers(HttpMethod.POST, "/login").permitAll()
                                .requestMatchers(HttpMethod.GET, "/destinations").permitAll()
                                .requestMatchers(HttpMethod.GET, "/destinations/{id}").permitAll()
                                .requestMatchers(HttpMethod.GET, "/destinations/user/{id}").permitAll()
                                .requestMatchers(HttpMethod.POST, "/destinations").hasRole("USER")
                                .requestMatchers(HttpMethod.GET, "/users/all").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/users/{id}").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/users/{username}").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.POST, "/users/create").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/users/delete/{id}").hasAnyRole("ADMIN", "USER")
                                .requestMatchers(HttpMethod.GET, "/roles").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/roles/{id}").hasRole("ADMIN")
//                              .anyRequest().authenticated()
                                .anyRequest().permitAll()
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
