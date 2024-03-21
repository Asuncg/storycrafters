package es.asun.StoryCrafters.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    public static InMemoryUserDetailsManager inMemory;
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/css/**").permitAll()
                        .requestMatchers("/ayuda").permitAll()
                        .requestMatchers("/admin").hasRole("administrador")
                        .requestMatchers("/basic/**").hasRole("usuario")
                        .anyRequest().authenticated()
                )
                .httpBasic(withDefaults())
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/logginprocess")
                        .permitAll()
                )
                .logout((logout) -> logout.logoutSuccessUrl("/login?logout").permitAll());

        return http.build();
    }

    @Bean
    InMemoryUserDetailsManager userDetailsService() {
        @SuppressWarnings("deprecation")
        UserDetails user1 = User.withDefaultPasswordEncoder()
                .username("pepito")
                .password("pepito")
                .roles("usuario")
                .build();

        @SuppressWarnings("deprecation")
        UserDetails user2 = User.withDefaultPasswordEncoder()
                .username("pepita")
                .password("pepita")
                .roles("usuario","administrador")
                .build();

        inMemory = new InMemoryUserDetailsManager();
        inMemory.createUser(user1);
        inMemory.createUser(user2);
        return inMemory;
    }

}
