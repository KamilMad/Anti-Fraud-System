package com.example.AntiFraudSystem.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration{


    private final JDBCUserDetailsService userService;

    @Autowired
    public SecurityConfiguration(JDBCUserDetailsService userService) {
        this.userService = userService;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .cors().disable()
                .csrf(csrf -> {
                    csrf.disable();
                })
                .authorizeHttpRequests(auth ->{
                    auth.requestMatchers(HttpMethod.GET,"/api/auth/list/**").hasAnyAuthority("ADMINISTRATOR", "SUPPORT");
                    auth.requestMatchers(HttpMethod.POST,"/api/antifraud/transaction/**").hasAuthority("MERCHANT");auth.requestMatchers(HttpMethod.PUT,"/api/auth/role/**").hasAnyAuthority("ADMINISTRATOR");auth.requestMatchers(HttpMethod.DELETE, "/api/auth/user/**").hasAuthority("ADMINISTRATOR");
                    auth.requestMatchers(HttpMethod.PUT,"/api/auth/access/**").hasAuthority("ADMINISTRATOR");
                    auth.requestMatchers(HttpMethod.GET,"/api/auth/user/{username}").authenticated();
                    auth.requestMatchers(HttpMethod.POST, "/api/auth/user").permitAll();
                    auth.requestMatchers("/actuator/shutdown").permitAll();
                })
                .headers().frameOptions().disable()
                .and()
                .formLogin().permitAll()
                .and()
                .logout().permitAll()
                .and()
                .httpBasic(Customizer.withDefaults())
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .requestMatchers(new AntPathRequestMatcher("/h2-console/**"));
    }

}
