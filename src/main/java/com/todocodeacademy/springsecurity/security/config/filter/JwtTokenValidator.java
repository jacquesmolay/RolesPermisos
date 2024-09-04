package com.todocodeacademy.springsecurity.security.config.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.todocodeacademy.springsecurity.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.lang.NonNull;

import java.awt.*;
import java.io.IOException;
import java.util.Collection;

//clases OncePerRequestFilter se ejecuta cada vez que se realice una request
public class JwtTokenValidator extends OncePerRequestFilter {


    private JwtUtils jwtUtils;


    //costructor para injectar sin autowired

    public JwtTokenValidator(JwtUtils jwtUtils){

        this.jwtUtils=jwtUtils;
    }

        //parametros de filtro interno, recivo la request l response y el filtro
    protected void doFilterInternal (@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {

        ///dela cabecera traer los datos de autorizacion
        String jwtToken=request.getHeader(HttpHeaders.AUTHORIZATION);

        if(jwtToken!=null){

            //autentificacion tipo bearer, para elminar la palabrar bearer de la respuesta salto al indice 7
            jwtToken=jwtToken.substring(7);

            //si no es nul decodifico el token
            DecodedJWT decodedJWT=jwtUtils.validateToken(jwtToken);

            //obtego nombre de usuario
            String username=jwtUtils.extractUsername(decodedJWT);

            //obtengo los permisos
            String authorities=jwtUtils.getSpecificClaim(decodedJWT,"authorities").asString();

            //transformar datos a grandauthorities para ponerlo en el context holder y asi el usuario no tenga que logearse una y otra vez

            //los permisos authorities los paso de un texto separado por comas a una collection
            Collection<? extends GrantedAuthority> authoritiesList= AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);

            //traigo el context holder
            SecurityContext context= SecurityContextHolder.getContext();
            //creo nueva instancia del autehtication entrego username y permisos
            Authentication authentication=new UsernamePasswordAuthenticationToken(username,null,authoritiesList);
            //entrego esa informacion al context
            context.setAuthentication(authentication);
            //entrego informacion al security conetxt holder
            SecurityContextHolder.setContext(context);

        }


        filterChain.doFilter(request,response);


    }

}
