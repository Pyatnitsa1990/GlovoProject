package com.example.glovoproject.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import static org.springframework.security.config.Customizer.withDefaults;


@Configuration
@EnableWebSecurity
@EnableWebMvc
public class SecurityConfig {
    @Scope("prototype")
    @Bean
    MvcRequestMatcher.Builder mvc(
            HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }
    /*
    *  https://spring.io/security/cve-2023-34035
    *
    *  do according to this article: https://stackoverflow.com/a/76825159
    * */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws  Exception{
        http.formLogin(withDefaults())
                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers(mvc.pattern(HttpMethod.POST, "/orders"),
                                         mvc.pattern("/products"))
                        .hasRole("ADMIN")
                        .anyRequest().authenticated());

        return http.build();
    }
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        UserDetails user = User.builder()
                .username("a")
                .password(encoder.encode("1"))
                .roles("USER")
                .build();

        UserDetails admin = User.builder()
                .username("b")
                .password(encoder.encode("2"))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user,admin);
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return  new BCryptPasswordEncoder();
    }
}
