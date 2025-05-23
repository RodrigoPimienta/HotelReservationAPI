package com.revature.controller;


import com.revature.dto.response.UserDTO;
import com.revature.dto.response.UserWithDetailsDTO;
import com.revature.exceptions.*;
import com.revature.models.User;
import com.revature.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService=userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createHotelHandler(@RequestBody User user, HttpSession session){
        return userService.register(user);
    }

    @PostMapping("login")
    public UserWithDetailsDTO loginHandler(@RequestBody User user, HttpSession session){

        Optional<User> potentialUser = userService.login(user);

        if (potentialUser.isPresent()){

            session.setAttribute("userId", potentialUser.get().getUserId());
            session.setAttribute("role", potentialUser.get().getRole());

            return new UserWithDetailsDTO(potentialUser.get());
        }

        throw new InvalidCredentialsException("Username or Password is incorrect");
    }

    @PutMapping()
    public User updateHotelHandler(@RequestBody User user, HttpSession session){

        if (session.getAttribute("userId") == null) {
            throw new UnauthenticatedException("User is not authenticated");
        }

        return userService.updateUser( (int) session.getAttribute("userId"),user);
    }

    // TODO PATCH UPDATE

    @DeleteMapping()
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteHotelHandler(HttpSession session) {
        if (session.getAttribute("userId") == null) {
            throw new UnauthenticatedException("User is not authenticated");
        }

//        if(hotelService.checkHotelExisting(hotelId)){
//            throw new ResourceNotFoundException("No hotel with id: " + hotelId);
//        }


        userService.deleteUser( (int) session.getAttribute("userId"));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> resourceNotFoundHandler(ResourceNotFoundException e){
        return Map.of(
                "error", e.getMessage()
        );
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> invalidCredentialsException (InvalidCredentialsException e){
        return Map.of(
                "error", e.getMessage()
        );
    }

    @ExceptionHandler(InvalidRequestBodyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> invalidRequestBodyException (InvalidRequestBodyException e){
        return Map.of(
                "error", e.getMessage()
        );
    }


}
