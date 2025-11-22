package br.com.marconefreitas.controllers.docs;

import br.com.marconefreitas.data.dto.security.AccountCredentialsDTO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public interface AuthControllerDocs {

    @Operation(summary = "Authenticates an user and returns a token")
    ResponseEntity<?> signIn(AccountCredentialsDTO credentials);

    @Operation(summary = "Refresh token for an authenticated user")
    ResponseEntity<?> refresh(String username,
                              String refreshToken);

    public AccountCredentialsDTO create(AccountCredentialsDTO person);
}
