package com.example.EPQLProject.config;

import com.example.EPQLProject.security.JwtRequestFilter;
import com.example.EPQLProject.service.AppUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
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
@RequiredArgsConstructor
public class SecurityConfig {

    private final AppUserDetailsService appUserDetailsService;
    private final JwtRequestFilter jwtRequestFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        System.out.println("[SecurityConfig] Starting Security Filter Chain configuration...");

        httpSecurity
                .cors(cors -> {
                    System.out.println("[SecurityConfig] Enabling CORS configuration...");
                    corsConfigurationSource(); // Just to trigger log inside
                })
                .csrf(csrf -> {
                    System.out.println("[SecurityConfig] Disabling CSRF...");
                    csrf.disable();
                })
                .authorizeHttpRequests(auth -> {
                    System.out.println("[SecurityConfig] Setting request matchers...");
                    auth
                            .requestMatchers(
                                    "/api/v1.0/register/**",
                                    "/api/v1.0/login/**",
                                    "/api/v1.0/activate/**",
                                    "/api/v1.0/status/**",
                                    "/api/v1.0/health/**"
                            ).permitAll();

                    System.out.println("[SecurityConfig] All other requests require authentication.");
                    auth.anyRequest().authenticated();
                })
                .sessionManagement(session -> {
                    System.out.println("[SecurityConfig] Setting stateless session management.");
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        System.out.println("[SecurityConfig] Security Filter Chain setup complete.");
        return httpSecurity.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        System.out.println("[SecurityConfig] Configuring AuthenticationManager...");
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(appUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(authProvider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        System.out.println("[SecurityConfig] Initializing PasswordEncoder (BCrypt)...");
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        System.out.println("[SecurityConfig] Configuring CORS...");
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*")); // Replace with specific origins in prod
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        System.out.println("[SecurityConfig] CORS setup complete.");
        return source;
    }
}
