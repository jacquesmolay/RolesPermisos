package com.todocodeacademy.springsecurity.security.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {



    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception{

        //definir endpoint que no necesita autentificacion .requestMatchers("/holanoseg").permitAll()
        //cualquier ruta que no coincida con la de arriba, debe ser autentificada  .anyRequest().authenticated()
        //pagina de inicio de sesion permitida para todos .formLogin().permitAll()



        return httpSecurity
                .csrf(csrf -> csrf.disable())
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(http-> {
                    http.requestMatchers(HttpMethod.GET, "/holanoseg").permitAll();
                    http.requestMatchers(HttpMethod.GET, "/holaseg").hasAuthority("READ");
                    http.anyRequest().denyAll();
                })
                .build();

    }




    //encargado de dministrar el proceso de autentificacion
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    //creamos authentication provider
    //Agregamos el user Details Service como parámetro, que es la interface que se implemento en UserDetailsServiceImp
    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        //sacamos el anterior, el lógico y agregamos el nuevo
        //  provider.setUserDetailsService(userDetailsService());
        provider.setUserDetailsService(userDetailsService);


        return provider;
    }

    //passworn va como texto plano, no recomendable
    @Bean
    public PasswordEncoder passwordEncoder(){

        return NoOpPasswordEncoder.getInstance();
    }

    /*
    //detalles de usuario
    @Bean
    public UserDetailsService userDetailsService(){

        //list generica para cuardar detalles de usuario
        List userDetailsList=new ArrayList<>();

        //defino detalles de usuario
        userDetailsList.add(User.withUsername("todocode")
                .password("1234")
                .roles("ADMIN")
                .authorities("CREATE","READ","UPDATE","DELETE")
                .build());

        userDetailsList.add(User.withUsername("seguidor")
                .password("1234")
                .roles("USER")
                .authorities("READ")
                .build());

        userDetailsList.add(User.withUsername("actualizador")
                .password("1234")
                .roles("USER")
                .authorities("UPDATE")
                .build());

        //devuelve detalles en memoria
        return new InMemoryUserDetailsManager(userDetailsList);

    }*/


}
