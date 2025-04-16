package com.revature.services;


import com.revature.dto.request.LoginRequestDTO;
import com.revature.dto.response.LoginDTO;
import com.revature.models.User;

public interface AuthService {
    LoginDTO login(LoginRequestDTO loginDto);
    User registerUser(User user);
}