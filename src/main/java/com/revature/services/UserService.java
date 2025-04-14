package com.revature.services;

import com.revature.exceptions.EmailAlreadyTakenException;
import com.revature.exceptions.InvalidRequestBodyException;
import com.revature.exceptions.ResourceNotFoundException;
import com.revature.models.Role;
import com.revature.models.User;
import com.revature.models.UserBusiness;
import com.revature.repos.UserBusinessDAO;
import com.revature.repos.UserDAO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserDAO userDAO;
    private final UserBusinessDAO userBusinessDAO;

    public UserService(UserDAO userDAO, UserBusinessDAO userBusinessDAO){
        this.userDAO=userDAO;
        this.userBusinessDAO=userBusinessDAO;
    }

    public User register(User userToBeRegistered){
        Optional<User> potentialUser = userDAO.findUserByEmail(userToBeRegistered.getEmail());

        if (potentialUser.isPresent()){
            throw new EmailAlreadyTakenException("Email: " + userToBeRegistered.getEmail() + " is already taken!");
        }

        // TODO validation for email and password
        // TODO ENCRYPT PASSWORD

        if(userToBeRegistered.getBusiness() == null && userToBeRegistered.getRole() == Role.OWNER){
            throw new InvalidRequestBodyException("Business information is required for OWNER role.");
        }

        return userDAO.save(userToBeRegistered);
    }

    // TODO Login
    public Optional<User> login(User userCredentials){

        // Look up the user by their username;
        Optional<User> potentialUser = userDAO.findUserByEmail(userCredentials.getEmail());

        // Validate the password match if the user is present
        if (potentialUser.isPresent()){
            User returnedUser = potentialUser.get();

            // Verify the user password matches
            if (returnedUser.getPassword().equals(userCredentials.getPassword())){
                return potentialUser;
            }
        }

        return Optional.empty();

    }


    public User updateUser(int userId, User updatedUser) {
        Optional<User> existingUserOptional = userDAO.findById(userId);

        if (existingUserOptional.isEmpty()) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        // TODO validation for email and password
        // TODO ENCRYPT PASSWORD

        User existingUser = existingUserOptional.get();
        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPassword(updatedUser.getPassword());

        if (existingUser.getRole() == Role.OWNER) {
            if (updatedUser.getBusiness() == null) {
                throw new InvalidRequestBodyException("Business information is required for OWNER role.");
            }

            UserBusiness existingBusiness = existingUser.getBusiness();

            if (existingBusiness != null) {
                existingBusiness.setLegalName(updatedUser.getBusiness().getLegalName());
                existingBusiness.setPhoneNumber(updatedUser.getBusiness().getPhoneNumber());
                existingBusiness.setTaxCode(updatedUser.getBusiness().getTaxCode());
            } else {
                UserBusiness newBusiness = updatedUser.getBusiness();
                newBusiness.setUser(existingUser);
                userBusinessDAO.save(newBusiness);
                existingUser.setBusiness(newBusiness);
            }
        }
        return userDAO.save(existingUser);
    }

    public void deleteUser(int userId) {
        Optional<User> existingUserOptional = userDAO.findById(userId);

        if (existingUserOptional.isEmpty()) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        userDAO.deleteById(userId);
    }

    public void deleteHotel(int userId) {
        userDAO.deleteById(userId);
    }

}
