package com.todocodeacademy.springsecurity.security.config;


import com.todocodeacademy.springsecurity.security.config.filter.JwtTokenValidator;
import com.todocodeacademy.springsecurity.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    //injecto dependencias de jwtUtils
    @Autowired
    private JwtUtils jwtUtils;




    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception{

        //definir endpoint que no necesita autentificacion .requestMatchers("/holanoseg").permitAll()
        //cualquier ruta que no coincida con la de arriba, debe ser autentificada  .anyRequest().authenticated()
        //pagina de inicio de sesion permitida para todos .formLogin().permitAll()



        return httpSecurity
                .csrf(csrf -> csrf.disable())
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(new JwtTokenValidator(jwtUtils), BasicAuthenticationFilter.class)//agrgo fltro dejwtUtils
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

        //return NoOpPasswordEncoder.getInstance();

        //encrypta contraseña

        return new BCryptPasswordEncoder(); //contraseña codificada
    }


}
