package com.example.userservice.service;

import com.example.userservice.model.AccountDetails;
import com.example.userservice.model.UserRole;
import com.example.userservice.repository.AccountDetailsRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@AllArgsConstructor
public class AuthenticationConfig {
    private final AccountDetailsRepository accountDetailsRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.httpBasic(Customizer.withDefaults())
                        .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.cors(Customizer.withDefaults());

        http.csrf(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests((authorize) -> authorize
                .requestMatchers(HttpMethod.POST, "/account/login").permitAll()
                .requestMatchers(HttpMethod.POST,
                        "/account/register/user",
                        "/account/register/admin").permitAll()
                .requestMatchers(HttpMethod.GET, "/authorize/user").hasRole(UserRole.USER.name())
                .requestMatchers(HttpMethod.GET, "/authorize/admin").hasRole(UserRole.ADMINISTRATOR.name())
                .requestMatchers(HttpMethod.GET, "/authorize/all").authenticated()
                .anyRequest().permitAll());

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            AccountDetails accountDetails = accountDetailsRepository.findByEmail(username);
            if (accountDetails == null) {
                throw new UsernameNotFoundException(username);
            }
            return accountDetails;
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authProvider(UserDetailsService userDetailsService, PasswordEncoder encoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(encoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
