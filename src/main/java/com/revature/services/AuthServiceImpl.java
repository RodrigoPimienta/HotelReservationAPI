package com.revature.services;


import com.revature.dto.request.LoginRequestDTO;
import com.revature.dto.response.LoginDTO;
import com.revature.repos.UserDAO;
import com.revature.models.User;
import com.revature.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDAO userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           UserDAO userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public LoginDTO login(LoginRequestDTO loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getEmail(),
                        loginDto.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtTokenProvider.generateToken(authentication);

        User user = userRepository.findUserByEmail(loginDto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new LoginDTO(user, token);
    }

    @Override
    public User registerUser(User registrationDto) {
        if (userRepository.findUserByEmail(registrationDto.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists!");
        }

        registrationDto.setPassword(passwordEncoder.encode(registrationDto.getPassword()));


        return userRepository.save(registrationDto);

    }

}