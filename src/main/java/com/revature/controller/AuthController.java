package com.revature.controller;

import com.revature.dto.request.LoginRequestDTO;
import com.revature.dto.response.LoginDTO;
import com.revature.models.User;
import com.revature.services.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginDTO> authenticateUser(@RequestBody LoginRequestDTO loginDto) {
        System.out.println(loginDto);
        return ResponseEntity.ok(authService.login(loginDto));
    }


    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User registrationDto) {
        System.out.println(registrationDto);
        return new ResponseEntity<>(authService.registerUser(registrationDto), HttpStatus.CREATED);
    }

}
