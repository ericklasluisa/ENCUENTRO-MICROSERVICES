package ec.edu.espe.ms_authentication.service;

import ec.edu.espe.ms_authentication.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    public String generarToken(Usuario user) {
        return buildToken(user, jwtExpiration);
    }

    public String generarRefreshToken(Usuario user) {
        return buildToken(user, refreshExpiration);
    }

    public String buildToken(Usuario user, long expiration) {
        return Jwts.builder()
                .id(user.getIdUsuario().toString())
                .claims(Map.of("nombre", user.getNombre()))
                .subject(user.getCorreo())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSecretKey())
                .compact();
    }

    public String getToken(String username) {
        // Aquí se implementaría la lógica para generar un token JWT
        // Por simplicidad, retornamos un token ficticio
        return "token-" + username;
    }

    private SecretKey getSecretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extraerCorreo(String token) {
        Claims jwtToken = Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return jwtToken.getSubject();
    }

    public boolean isTokenValid(String token, Usuario usuario) {
        String correo = extraerCorreo(token);
        return (correo.equals(usuario.getCorreo()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        Date expiration = claims.getExpiration();
        return expiration.before(new Date());
    }
}
