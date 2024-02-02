package com.alberts.pizza.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableMethodSecurity(securedEnabled = true)//con esta notacion le decimos a Spring que controle las notaciones @Secured en los metodos de los services
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Autowired
    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    //el siguiente bean desabilitamos la configuración del CSR y habilitamos el cors
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable).cors(Customizer.withDefaults())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(customizeRequests -> {
                            customizeRequests
                                    //siempre es importante mantener el orden de los permisos debido a que debido al orden en el que se ejecutan es como restrige los accesos (del más debil al mas fuerte)
                                    //.requestMatchers(HttpMethod.GET,"/api/*").permitAll()con esta configuracion permitimos todas las peticiones GET para el endpoint definido
                                    .requestMatchers("/api/auth/**").permitAll()
                                    .requestMatchers("/api/customers/**").hasAnyRole("ADMIN", "CUSTOMER")
                                    .requestMatchers(HttpMethod.GET,"/api/pizzas/**").hasAnyRole("ADMIN", "CUSTOMER")//con esta configuracion permitimos que ciertos roles consuman el endpoints
                                    .requestMatchers(HttpMethod.POST,"/api/pizzas/**").hasRole("ADMIN")//con esta configuracion permitimos que ciertos roles consuman ciertos endpoints
                                    .requestMatchers(HttpMethod.PUT).hasRole("ADMIN")//con este metodo denegamos las peticiones put en todo el proyecto
                                    .requestMatchers("/api/orders/random").hasAuthority("random_order")
                                    .requestMatchers("/api/orders/**").hasRole("ADMIN")//unicamente este rol permite usar o limitar el uso del servicio a cierto tipo de roles
                                    //.requestMatchers(HttpMethod.PUT).denyAll()//con este metodo denegamos las peticiones put en todo el proyecto
                                    .anyRequest()
                                    .authenticated();

                        }
                ).addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)//aquí poneos mos filtros creados para la cadena de seguridad de spring
                ;

        return http.build();
    }


    //configuracion del usuarios en memoria usando el metodo de PasswordEnconder BCrypt
    /*@Bean
    public UserDetailsService memoryUsers(){
        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder().encode("admin"))
                .roles("ADMIN")
                .build();

        UserDetails customer = User.builder()
                .username("customer")
                .password(passwordEncoder().encode("customer123"))
                .roles("CUSTOMER")
                .build();

        return new InMemoryUserDetailsManager(admin, customer);
    }*/

    //configuracion para usar BCryptPasswordEncoder en nuestra clase
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
