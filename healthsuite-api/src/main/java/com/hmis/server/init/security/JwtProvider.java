package com.hmis.server.init.security;

import com.hmis.server.hmis.common.constant.HmisConfigConstants;
import com.hmis.server.hmis.common.exception.HmisApplicationException;
import io.jsonwebtoken.*;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Date;

@Service
public class JwtProvider {
    private KeyStore keyStore;

    private Key key;

    @Value("${jwt.expiresIn}")
    private Integer jwtExpiresIn;

    @PostConstruct
    public void init() {
        try {
            keyStore = KeyStore.getInstance("JKS");
            InputStream resourceAsStream = getClass().getResourceAsStream("/hmis.jks");
            keyStore.load(resourceAsStream, "hmiskey".toCharArray());
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            throw new HmisApplicationException("Exception occurred while loading keystore");
        }
    }

    public String generateToken(Authentication authentication) {
        int expireTime = jwtExpiresIn == null ? HmisConfigConstants.JWT_DEFAULT_EXPIRE_HOURS : jwtExpiresIn;
        Date expireDate = DateUtils.addHours(new Date(), expireTime);
        User principal = (User) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject(principal.getUsername())
                .signWith(getPrivateKey())
                .setExpiration(expireDate)
                .compact();
    }

    public boolean validateToken(String jwt, HttpServletRequest httpServletRequest) {

        try {
            Jwts.parser().setSigningKey(getPublicKey()).parseClaimsJws(jwt);
            return true;
        } catch (MalformedJwtException ex) {
            // System.out.println("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            // System.out.println("Expired JWT token");
            httpServletRequest.setAttribute("expired", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            // System.out.println("Unsupported JWT exception");
        } catch (IllegalArgumentException ex) {
            // System.out.println("Jwt claims string is empty");
        }
        return false;
    }

    private PublicKey getPublicKey() {
        try {
            return keyStore.getCertificate("hmis").getPublicKey();
        } catch (KeyStoreException e) {
            throw new HmisApplicationException("Exception occurred while retrieving public key from keystore");
        }
    }

    private PrivateKey getPrivateKey() {
        try {
            return (PrivateKey) keyStore.getKey("hmis", "hmiskey".toCharArray());
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            throw new HmisApplicationException("Exception occurred while retrieving public key from keystore");
        }
    }

    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(getPublicKey())
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
}
