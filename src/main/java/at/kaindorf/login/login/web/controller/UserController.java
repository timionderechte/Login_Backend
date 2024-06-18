package at.kaindorf.login.login.web.controller;

import at.kaindorf.login.login.config.JwtAuthenticationFilter;
import at.kaindorf.login.login.pojos.User;
import at.kaindorf.login.login.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<String>returnUser(){

        String email = jwtAuthenticationFilter.getUserEmail();
        System.out.println(email);
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent()){
            User user = userOpt.get();
            System.out.println(user);
            return ResponseEntity.ok(user.getRealUsername());
        }

        return ResponseEntity.notFound().build();
    }
}
