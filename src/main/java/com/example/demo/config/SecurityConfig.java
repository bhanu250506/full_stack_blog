package com.example.demo.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource; // <-- CORRECT!

import java.util.Arrays; // <-- Import Arrays

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Allow OPTIONS requests globally
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // --- ADD/MODIFY THIS SECTION ---
                        // Specific public GET endpoints (including single group)
                        .requestMatchers(HttpMethod.GET,
                                "/api/posts",          // List posts
                                "/api/posts/{id}",     // Single post
                                "/api/posts/{postId}/comments", // Comments for a post
                                "/api/profiles/{email}", // User profile
                                "/api/groups",         // List groups
                                "/api/groups/{id}",    // Single group <<-- More specific rule
                                "/api/groups/{groupId}/posts", // Posts in a group
                                "/api/search/**"       // Search results
                        ).permitAll()
                        // --- END SECTION ---

                        // Auth and WS connection endpoints
                        .requestMatchers("/api/auth/**", "/ws/**").permitAll()

                        // All other requests must be authenticated
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ADD THIS BEAN: Configure CORS
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Allow requests from your React app's origin
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173")); // Adjust port if needed
        // Allow common HTTP methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Allow common headers
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        // Allow credentials (like cookies, though we use JWT)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Apply this configuration to all paths
        source.registerCorsConfiguration("/**", configuration);
        return (CorsConfigurationSource) source;
    }
}