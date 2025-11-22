package br.com.marconefreitas.security;


import br.com.marconefreitas.data.dto.security.TokenDTO;
import br.com.marconefreitas.exception.InvalidJWTAuthenticationException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Base64;
import java.util.Date;
import java.util.List;

@Service
public class JWTTokenProvider {

    @Value("${security.jwt.token.secret-key:secret}")
    private String secretKey;

    @Value("${security.jwt.token.expire-length:3600000}")
    private long validityInMiliseconds;  // 1hr

    @Autowired
    private UserDetailsService userDetailsService;

    Algorithm algorithm = null;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        algorithm = Algorithm.HMAC256(secretKey.getBytes());

    }
    public TokenDTO createAccessToken(String userName, List<String> roles){

        Date now = new Date();
        Date validityDate = new Date(now.getTime() + validityInMiliseconds);

        String accessToken = getAccessToken(userName, roles, now, validityDate);
        String refreshToken = getRefreshToken(userName, roles, now);
        return new TokenDTO(userName, true, now, validityDate, accessToken, refreshToken);

    }

    public TokenDTO refreshToken(String refreshToken){

        if (StringUtils.isNotBlank(refreshToken) && refreshToken.startsWith("Bearer ")) {
            refreshToken = refreshToken.substring(7);
        }
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT =  verifier.verify(refreshToken);

        String userName = decodedJWT.getSubject();

        List<String> roles = decodedJWT.getClaim("roles").asList(String.class);
        return createAccessToken(userName, roles);

    }

    private String getRefreshToken(String userName, List<String> roles, Date now) {

        Date refreshTokenValidity = new Date(now.getTime() + validityInMiliseconds * 3);
        return JWT.create()
                .withClaim("roles", roles)
                .withIssuedAt(now)
                .withExpiresAt(refreshTokenValidity)
                .withSubject(userName)
                .sign(algorithm)
                ;
    }

    private String getAccessToken(String userName, List<String> roles, Date now, Date validityDate) {
        String issueUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .build().toUriString();
        return JWT.create()
                .withClaim("roles", roles)
                .withIssuedAt(now)
                .withExpiresAt(validityDate)
                .withSubject(userName)
                .withIssuer(issueUrl)
                .sign(algorithm)
                ;

    }

    public Authentication getAuthentication(String token){
        DecodedJWT decodedJWT = decodedToken(token);
        UserDetails user = this.userDetailsService.loadUserByUsername(decodedJWT.getSubject());
        return new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());
    }

    private DecodedJWT decodedToken(String token) {
        Algorithm alg = Algorithm.HMAC256(secretKey.getBytes());
        JWTVerifier verifier = JWT.require(alg).build();
     //   DecodedJWT decodedJWT = verifier.verify(token);
        return verifier.verify(token);
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if(StringUtils.isNotBlank(bearerToken) && bearerToken.startsWith("Bearer ")){

            return bearerToken.substring("Bearer ".length());
        } return null;
    }

    public boolean validateToken(String token){
        DecodedJWT decodedJWT = decodedToken(token);
        try {
            if (decodedJWT.getExpiresAt().before(new Date())) {
                return false;
            }
            return true;
        } catch(Exception e){
            throw new InvalidJWTAuthenticationException("Expired or invalid JWT Token");
        }
    }

}
