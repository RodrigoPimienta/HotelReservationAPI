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

}
