package br.com.marconefreitas.controllers;


import br.com.marconefreitas.controllers.docs.AuthControllerDocs;
import br.com.marconefreitas.data.dto.security.AccountCredentialsDTO;
import br.com.marconefreitas.services.AuthServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication endpoints")
@RestController
@RequestMapping("/auth")
public class AuthController implements AuthControllerDocs {

    @Autowired
    AuthServices services;



    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody AccountCredentialsDTO credentials) {
       if(credentials == null || StringUtils.isBlank(credentials.getPassword())
               || StringUtils.isBlank(credentials.getUsername())){
           return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid credentials");
       }
       var token = services.signIn(credentials);
       if(token == null){
           return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid credentials");
       }

       return token;
    }

    @PutMapping("/refresh/{username}")
    public ResponseEntity<?> refresh(@PathVariable("username") String username,
                                     @RequestHeader("Authorization") String refreshToken) {
        if(StringUtils.isBlank(username)
                || StringUtils.isBlank(refreshToken)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid credentials");
        }
        var token = services.refresh(username, refreshToken);
        if(token == null){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid credentials");
        }

        return token;
    }


    @PostMapping(value = "/createUser",
            consumes = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            },
            produces = {    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    public AccountCredentialsDTO create(@RequestBody AccountCredentialsDTO person){
        return services.create(person);
    }
}
