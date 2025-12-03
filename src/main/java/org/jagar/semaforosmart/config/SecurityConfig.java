package org.jagar.semaforosmart.config;

import org.jagar.semaforosmart.service.DatabaseUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final DatabaseUserDetailsService userDetailsService;

    public SecurityConfig(DatabaseUserDetailsService userDetailsService){
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/img/**", "/fonts/**", "/", "/inicio", "/error").permitAll()
                        .requestMatchers("/admin/login").permitAll()
                        .requestMatchers("/invitado/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/admin/login")
                        .loginProcessingUrl("/admin/login")
                        .defaultSuccessUrl("/admin/evidencias", true)
                        .failureUrl("/admin/login?error")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .userDetailsService(userDetailsService);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new PasswordEncoder(){
            @Override
            public String encode(CharSequence rawPassword) {
                try {
                    MessageDigest digest = MessageDigest.getInstance("SHA-256");
                    byte[] hash = digest.digest(rawPassword.toString().getBytes(StandardCharsets.UTF_8));
                    StringBuilder hexString = new StringBuilder();
                    for (byte b : hash) {
                        String hex = Integer.toHexString(0xff & b);
                        if (hex.length() == 1) hexString.append('0');
                        hexString.append(hex);
                    }
                    return hexString.toString();
                } catch (NoSuchAlgorithmException e) {
                    throw new IllegalStateException("SHA-256 algorithm not found", e);
                }
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return encode(rawPassword).equalsIgnoreCase(encodedPassword);
            }
        };
    }
}
