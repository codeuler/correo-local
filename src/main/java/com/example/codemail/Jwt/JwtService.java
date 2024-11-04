package com.example.codemail.Jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    private final String SECRET_KEY;

    public JwtService(@Value("${semilla-encriptar.semilla}") String semilla) {
        this.SECRET_KEY = Base64.getEncoder().encodeToString(semilla.getBytes());
    }

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
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getKey() {
        //Decodificar la llave secreta
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        //Crear una instancia de Key con la llave secreta decodificada
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String getUsernameFromToken(String token) {
        /*
         * Claims::getSubject, Hace referencia de la Clase Claims con su metodo getSubject()
         * Es una manera resumida de la lambda (Claims c) -> c.getSubject();
         */
        return getClaimsFromToken(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return !isTokenExpired(token) &&
                // Evalua sin el username del token es el mismo
                username.equals(userDetails.getUsername());
    }

    /*
     * un Claims es una colección de declaraciones (claims) sobre una entidad (normalmente, el usuario) y metadatos
     * adicionales. Estas declaraciones están contenidas en el payload (cuerpo) del token JWT y se utilizan para
     * transmitir información entre dos partes de manera segura.
     * Internamente se ve algo así
     * "payload": {
     * "sub": "1234567890",
     * "name": "John Doe",
     * "admin": true,
     * "iat": 1516239022
     * }
     */
    private Claims getClaimsFromToken(String token) {
        return Jwts
                /*
                 * Llama a un método estático de la clase Jwts (de la biblioteca jjwt), que devuelve un
                 * JwtParserBuilder, un objeto utilizado para configurar y construir un parser de JWT.
                 */
                .parserBuilder()
                /*
                 * Configura la clave de firma que se utilizará para verificar la autenticidad del token. getKey() es
                 * un método que devuelve la clave de firma. Esta clave debe ser la misma que se utilizó para firmar el
                 * token inicialmente.
                 */
                .setSigningKey(getKey())
                //Construye y devuelve un JwtParser configurado con la clave de firma proporcionada.
                .build()
                /*
                 *  Utiliza el JwtParser para analizar el JWT proporcionado como argumento. Este método devuelve un
                 * objeto Jwt<Header, Claims>, que contiene tanto las reclamaciones del token como la información sobre
                 * la firma.
                 */
                .parseClaimsJws(token)
                /*
                 * Extrae y devuelve las reclamaciones del JWT, que están contenidas en el cuerpo del token.
                 */
                .getBody();
    }

    /*
     * Retorna un Generico, es decir cualquier objeto (<T> T)
     * Recibe el token y una función con una parametro de entrada que es un Claims y retorna un objeto generico
     * (Function<Claims,T> claimsResolver)
     */
    public <T> T getClaimsFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getClaimsFromToken(token);
        //Hace que se ejecute la función que se pasó por parametro, pasadole la misma claims
        return claimsResolver.apply(claims);
    }

    private Date getIssuedAtFromToken(String token) {
        //getIssudedAt retorna la fecha de emisión
        return getClaimsFromToken(token, Claims::getIssuedAt);
    }

    private boolean isTokenExpired(String token) {
        //Compara si la fecha de expiración del token está antes de la fecha actual (new Date())
        return getExpiration(token).before(new Date());
    }

    private Date getExpiration(String token) {
        return getClaimsFromToken(token, Claims::getExpiration);
    }
}
