
package at.kaindorf.login.login.web.controller;


import at.kaindorf.login.login.pojos.AuthRequest;
import at.kaindorf.login.login.pojos.RegisterRequest;
import at.kaindorf.login.login.web.service.AuthenticationService;
import at.kaindorf.login.login.pojos.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Optional;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity register(
            @RequestBody RegisterRequest request
    ){
        Optional<AuthResponse> tokenOptional = service.register(request);
        if(tokenOptional.isPresent()){
            return ResponseEntity.ok(tokenOptional.get());
        }
        return ResponseEntity.status(409).body(Collections.singletonMap("msg", "user already exists"));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponse> authenticate(
            @RequestBody AuthRequest request
    ){
        return ResponseEntity.ok(service.authenticate(request));
    }
}
