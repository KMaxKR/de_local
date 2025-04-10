package ks.mx.de_local.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JWTService {

    @Value("${general.token.key}")
    private String key;
    @Value("${general.token.lifetime}")
    private Long lifetime;

    public String generateToken(String username){
        String token = "";
        //generate token
        token = Jwts.builder()
            .setSubject(username)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + lifetime))
            .signWith(getKey())
            .compact();
        //return token
        return "Bearer " + token;
    }

    public Claims getClaim(String token){
        return Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token).getBody();
    }

    public String getUsernameFromToken(String token){
        return  getClaim(token).getSubject();
    }

    public boolean isValidToken(String token){
        return getClaim(token).getExpiration().after(new Date());
    }

    private Key getKey(){
        byte[] byte_key = Base64.getDecoder().decode(key);
        return new SecretKeySpec(byte_key, SignatureAlgorithm.HS256.getJcaName());
    }
}
