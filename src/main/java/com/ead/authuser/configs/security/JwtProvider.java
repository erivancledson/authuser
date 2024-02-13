package com.ead.authuser.configs.security;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.*;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

@Log4j2
@Component
public class JwtProvider {

    @Value("${ead.auth.jwtSecret}")
    private String jwtSecret;

    @Value("${ead.auth.jwtExpirationMs}")
    private int jwtExpirationMs; //tempo de espiracao

    //metodo que vai gerar o token
    public String generateJwt(Authentication authentication) {
        UserDetails userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        return Jwts.builder()
                .setSubject((userPrincipal.getUsername())) //nome do usuario
                .setIssuedAt(new Date()) //data
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs)) //data de expiracao do token
                .signWith(SignatureAlgorithm.HS512, jwtSecret) //hash utilizado e a chave secreta
                .compact(); //criacao do jwt
    }

    public String getUsernameJwt(String token) { //recebe o token gerado quando o usuario faz o login
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject(); //extrai o username do usuario
    }
    //valida se o token é valido
    public boolean validateJwt(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken); //verifica se a chave secreta está sendo enviada
            return true;
        } catch (SignatureException e) { //erro na assinatura
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) { //mal formatado
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) { //se já expirou
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) { //não suporta o token
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }


}
