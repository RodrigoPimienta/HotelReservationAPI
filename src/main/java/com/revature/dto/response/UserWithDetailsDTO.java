package com.revature.dto.response;

import com.revature.models.Role;
import com.revature.models.User;
import com.revature.models.UserBusiness;

public class UserWithDetailsDTO {
    private int userId;

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private Role role;

    private UserBusiness business;

    public UserWithDetailsDTO(){}


    public UserWithDetailsDTO(User user){
        this.userId=user.getUserId();
        this.firstName= user.getFirstName();
        this.lastName= user.getLastName();
        this.email= user.getEmail();
        this.role=user.getRole();
        this.business=user.getBusiness();
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public UserBusiness getUserBusiness() {
        return business;
    }

    public void setUserBusiness(UserBusiness userBusiness) {
        this.business = userBusiness;
    }
}
