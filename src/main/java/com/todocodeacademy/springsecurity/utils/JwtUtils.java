package com.todocodeacademy.springsecurity.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.security.core.GrantedAuthority;
import java.util.Date;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JwtUtils {

    //clave utilizada en el rchivo properties
    @Value("${security.jwt.private.key}")
    private String privateKey;

    //usuario utilizado en el archivo propertis
    @Value("${security.jwt.user.generator}")
    private String userGeneratos;


    //creacion de tokens
    public String createToken(Authentication authentication){

        //paso calve privada a HMA256
        Algorithm algorithm=Algorithm.HMAC256(privateKey);

        //esta dentro del security context holder
        //representa al usuario autenticado, nombre del usuario principal dentro de la autentificacion
        String username=authentication.getPrincipal().toString();

        //necesito los permisos en formato string, por lo que creo una variable y traigo los permisos con getAuthorities
        String authorities =authentication.getAuthorities()
                    //como devuelve una colecction lo paso a esrteam
                .stream()
                //lo mapeo para traer a cada uno de los authorities
                .map(GrantedAuthority::getAuthority)
                //y lo ordeno por comas
                .collect(Collectors.joining(","));

        //a partir de esto generamos el token
        String jwtToken = JWT.create()
                .withIssuer(this.userGeneratos) //acá va el usuario que genera el token
                .withSubject(username) // a quien se le genera el token usuario, no el del archivo properties
                .withClaim("authorities", authorities) //claims son los datos contraidos en el JWT
                .withIssuedAt(new Date()) //fecha de generación del token
                .withExpiresAt(new Date(System.currentTimeMillis() + 1800000)) //fecha de expiración, tiempo en milisegundos en este caso son 30 minutos
                .withJWTId(UUID.randomUUID().toString()) //id al token - que genere una random
                .withNotBefore(new Date (System.currentTimeMillis())) //desde cuando es válido (desde ahora en este caso pero se puede modificar)
                .sign(algorithm); //nuestra firma es la que creamos con la clave secreta

        return jwtToken;


    }


    //decodificar y validar nuesros tokens

    public DecodedJWT validateToken(String token) {

        try {
            Algorithm algorithm = Algorithm.HMAC256(this.privateKey);//algortimo pra desencriptar token, para lo cual utilizamos el mismo algoritmo del metodo anterior
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(this.userGeneratos) //acá va el usuario que genera el token
                    .build(); //usa patrón builder para construir

            //si está todo ok, no genera excepción y hace el return
            DecodedJWT decodedJWT = verifier.verify(token);
            return decodedJWT;
        }
        catch (JWTVerificationException exception) {
            throw new JWTVerificationException("Invalid token. Not authorized");
        }
    }


    //metodo para obtener el usuario/username
    public String extractUsername (DecodedJWT decodedJWT) {
        //el subject es el usuario según establecimos al crear el token
        return decodedJWT.getSubject().toString();
    }

    //obtener el claim en particular
    public Claim getSpecificClaim (DecodedJWT decodedJWT, String claimName) {
        return decodedJWT.getClaim(claimName);
    }

    //devuelvo todos los claims
    public Map<String, Claim> returnAllClaims (DecodedJWT decodedJWT){
        return decodedJWT.getClaims();
    }




}
