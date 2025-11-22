package br.com.marconefreitas.services;

import br.com.marconefreitas.data.dto.security.AccountCredentialsDTO;
import br.com.marconefreitas.data.dto.security.TokenDTO;
import br.com.marconefreitas.exception.RequiredObjectNullException;
import br.com.marconefreitas.mapper.ObjectMapper;
import br.com.marconefreitas.model.User;
import br.com.marconefreitas.repository.UserRepository;
import br.com.marconefreitas.security.JWTTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthServices {

    Logger logger = LoggerFactory.getLogger(AuthServices.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTTokenProvider provider ;

    @Autowired
    private UserRepository repository;

    public ResponseEntity<TokenDTO> signIn(AccountCredentialsDTO credentials){
        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        credentials.getUsername(),
                        credentials.getPassword()));

        var user = repository.findByUsername(credentials.getUsername());
        if (user == null){
            throw new UsernameNotFoundException("User not found: " + credentials.getUsername());
        }

        var tokenResponse = provider.createAccessToken(credentials.getUsername(), user.getRoles());

        return ResponseEntity.ok(tokenResponse);
    }

    public ResponseEntity<TokenDTO> refresh(String username, String refreshToken){

        var user = repository.findByUsername(username);
        TokenDTO tokenResponse ;
        if (user != null) {
            tokenResponse = provider.refreshToken(refreshToken);
        } else{
            throw new UsernameNotFoundException("User not found: " + username);
        }
        return ResponseEntity.ok(tokenResponse);
    }

    private String gemeratedHashedPassword(String senha) {

        Map<String, PasswordEncoder> encoders = new HashMap<>();
        PasswordEncoder pbkdf2Encoder = new Pbkdf2PasswordEncoder("", 8,
                185000,
                Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256);

        encoders.put("pbkdf2", pbkdf2Encoder);
        DelegatingPasswordEncoder passwordEncoder = new DelegatingPasswordEncoder("pbkdf2", encoders);
        passwordEncoder.setDefaultPasswordEncoderForMatches(pbkdf2Encoder);

         return passwordEncoder.encode(senha);
    }

    public AccountCredentialsDTO create(AccountCredentialsDTO user){
        logger.info("Creating one user)");

        if(user == null){
            throw new RequiredObjectNullException();
        }
        User entity = new User();
        entity.setFullName(user.getFullname());
        entity.setUserName(user.getUsername());
        entity.setPassword(gemeratedHashedPassword(user.getPassword()));
        entity.setAccountNonExpired(true);
        entity.setAccountNonLocked(true);
        entity.setCredentialsNonExpired(true);
        entity.setEnabled(true);

        return  ObjectMapper.parseObject(repository.save(entity), AccountCredentialsDTO.class);

    }
}
