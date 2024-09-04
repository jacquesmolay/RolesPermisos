package com.todocodeacademy.springsecurity.services;

import com.todocodeacademy.springsecurity.dto.AuthLoginRequestDTO;
import com.todocodeacademy.springsecurity.dto.AuthResponseDTO;
import com.todocodeacademy.springsecurity.model.UserSec;
import com.todocodeacademy.springsecurity.repository.IUserRepository;
import com.todocodeacademy.springsecurity.utils.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
//UserDetailsService interfce de spring security

@Service
public class UserDetailsServiceImp implements UserDetailsService {

//necesito acceder a los usuarios, por lo que instacio al servicio de usuarios
    @Autowired
    private IUserRepository userRepo;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    //metodo que implementa la interface UserDetailsService
    @Override
    public UserDetails loadUserByUsername (String username) throws UsernameNotFoundException {

        //tenemos User sec y necesitamos devolver UserDetails
        //traemos el usuario de la bd con el metodo que busca por nombre creado con el repositorio de UserSec
        UserSec userSec = userRepo.findUserEntityByUsername(username)
                //indico que excepcion debe arrojar
                .orElseThrow(() -> new UsernameNotFoundException("El usuario " + username + " no fue encontrado"));

        //con SimpleGrantedAuthority Spring Security maneja permisos
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();

        //traer roles y convertirlos en SimpleGrantAuthrity, con stream hago una secuencia de elementos y darles iertos formato
        userSec.getRolesList().stream()
                //flatMap de los roles traigo los roles y esa lista de permisos le doi una secuencia para recorrerla
                .flatMap(role -> role.getPermissionsList().stream()) //acá recorro los permisos de los roles
                //por cda permiso que voy encontrando lo agregoa al authorityList
                .forEach(permission -> authorityList.add(new SimpleGrantedAuthority(permission.getPermissionName())));


        //tomamos roles y los convertimos en SimpleGrantedAuthority para poder agregarlos a la authorityList
        //obtengo la lista de roles
        userSec.getRolesList()
                //obtengo roles los convierto a SimpleGrantedAuthirity, debo agregar la palabra ROLE_ antes sino spring pensara que es un permisoo
                .forEach(role -> authorityList.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRole()))));




        //retornamos el usuario en formato Spring Security con los datos de nuestro userSec por eso utilizo la clase User de spring y agrego campos obligatorios que pide spring security
        return new User(userSec.getUsername(),
                userSec.getPassword(),
                userSec.isEnabled(),
                userSec.isAccountNotExpired(),
                userSec.isCredentialNotExpired(),
                userSec.isAccountNotLocked(),
                //lista contiene roles y permisos
                authorityList);
    }

    public AuthResponseDTO loginUser(@Valid AuthLoginRequestDTO authLoginRequest) {

        //recuperamos nombre de usuario y contraseña
        String username = authLoginRequest.username();
        String password = authLoginRequest.password();

        Authentication authentication = this.authenticate (username, password);
        //si todo sale bien
        SecurityContextHolder.getContext().setAuthentication(authentication);
        //injecto depndencia de la clase JwtUtils
        String accessToken = jwtUtils.createToken(authentication);
        //envio datos al dto
        AuthResponseDTO authResponseDTO = new AuthResponseDTO(username, "login ok", accessToken, true);
        return authResponseDTO;
    }

    public Authentication authenticate (String username, String password) {
        //con esto debo buscar el usuario
        UserDetails userDetails = this.loadUserByUsername(username);

        //arrojo exception si no encuentra nombre de contraseña
        if (userDetails == null) {
            throw new BadCredentialsException("Invalid username or password");
        }
        // si no es igual hao autowired de pssword encoder, clase es de java
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }
        return new UsernamePasswordAuthenticationToken(username, userDetails.getPassword(), userDetails.getAuthorities());
    }



}
