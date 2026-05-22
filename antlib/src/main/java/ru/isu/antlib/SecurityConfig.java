package ru.isu.antlib;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http.csrf(CsrfConfigurer::disable)
                .authorizeHttpRequests(auth ->auth
                        .requestMatchers("/login","/register","/swagger-ui/**","/v3/api-docs*/**", "/js/**", "/css/**", "/checkUsername").permitAll()
                        .requestMatchers("/books/**").hasAnyAuthority("ROLE_USER")
                        .requestMatchers("/collections/**").hasAnyAuthority("ROLE_USER")
                        .requestMatchers("/library/**").hasAnyAuthority("ROLE_USER")
                        .requestMatchers("/profile/**").hasAnyAuthority("ROLE_USER")
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .loginPage("/login")
                        .defaultSuccessUrl("/books")
                        .failureUrl("/login?error=true")
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .deleteCookies("JSESSIONID"));
        return http.build();
    }
}
