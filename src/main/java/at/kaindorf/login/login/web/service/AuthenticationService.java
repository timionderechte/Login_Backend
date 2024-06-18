package at.kaindorf.login.login.web.service;

import at.kaindorf.login.login.config.JwtService;
import at.kaindorf.login.login.pojos.*;
import at.kaindorf.login.login.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public Optional<AuthResponse> register(RegisterRequest request){
        User user = User
                .builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .user_name(request.getUser_name())
                .build();

        if(userRepo.findByEmail(request.getEmail()).isPresent()){
            return Optional.empty();
        }

        userRepo.save(user);
        User userFromDB = userRepo.findByEmail(request.getEmail()).orElseThrow();


        var jwtToken = jwtService.generateToken(user);
        return Optional.of(AuthResponse.builder()
                .token(jwtToken)
                .build());
    }


    public AuthResponse authenticate(AuthRequest request){
        User user = userRepo.findByEmail(request.getEmail()).orElseThrow();

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        request.getPassword()
                )
        );

        var jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }

}