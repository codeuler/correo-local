package com.example.codemail.Jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    private static final String SECRET_KEY = Base64.getEncoder().encodeToString("llavesecretaqueseusaenjwtafasfadfadfafadfdd".getBytes());

    public String getToken(UserDetails usuario) {
        return getToken(new HashMap<>(), usuario);
    }
    //Genera un JSON web token para un usuario autenticado
    private String getToken(Map<String, Object> claims, UserDetails usuario) {
        //Utiliza el builder de Jwts para construir un JWT (json web token)
        return Jwts
                .builder()
                .setClaims(claims)
                .setSubject(usuario.getUsername())
                //Fecha de emision
                .setIssuedAt(new Date(System.currentTimeMillis()))
                //Fecha de expiración
                .setExpiration(new Date(System.currentTimeMillis()+1000*60*60*24))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getKey() {
        //Decodificar la llave secreta
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        //Crear una instancia de Key con la llave secreta decodificada
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
