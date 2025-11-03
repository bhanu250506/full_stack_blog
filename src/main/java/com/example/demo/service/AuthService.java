package com.example.demo.service;


import com.example.demo.model.User;
import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequestDto;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    public AuthResponse register(RegisterRequestDto registerRequestDto){
        var user = User.builder()
                .name(registerRequestDto.getName())
                .email(registerRequestDto.getEmail())
                .password(passwordEncoder.encode(registerRequestDto.getPassword()))
                .build();

        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(jwtToken)
                .name(user.getName())
                .email(user.getEmail())
                .password(user.getPassword())
                .build();

    }


    public AuthResponse login(LoginRequest loginRequest) {
        try {
            // Attempt authentication
            var authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            // Retrieve the authenticated user
            var user = (User) authentication.getPrincipal();  // Assuming User is your custom user class
            var jwtToken = jwtService.generateToken(user);

            return AuthResponse.builder().token(jwtToken).build();
        } catch (BadCredentialsException ex) {
            // Handle bad credentials (invalid username or password)
            throw new RuntimeException("Invalid email or password", ex);
        }
    }

}
