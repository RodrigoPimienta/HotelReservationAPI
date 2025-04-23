package com.revature;

import com.revature.exceptions.EmailAlreadyTakenException;
import com.revature.models.User;
import com.revature.repos.UserDAO;
import com.revature.services.UserService;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserDAO mockDAO;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail("test@gmail.com");
    }

    @Test
    void invalidEmail() {
        String email = "test_gmail.com";
        boolean result = userService.validateEmail(email);
        Assert.assertFalse(result);
    }

    @Test
    void validEmail() {
        String email = "test@gmail.com";
        boolean result = userService.validateEmail(email);
        Assert.assertTrue(result);
    }

    @Test
    void invalidLengthPassword() {
        String password = "ABCDEFG";
        boolean result = userService.validatePassword(password);
        Assert.assertFalse(result);
    }

    @Test
    void invalidLowerCasePassword() {
        String password = "ABCDEF1*";
        boolean result = userService.validatePassword(password);
        Assert.assertFalse(result);
    }

    @Test
    void invalidUpperCasePassword() {
        String password = "abcdef1*";
        boolean result = userService.validatePassword(password);
        Assert.assertFalse(result);
    }

    @Test
    void invalidDigitPassword() {
        String password = "abcdefG*";
        boolean result = userService.validatePassword(password);
        Assert.assertFalse(result);
    }

    @Test
    void invalidSpecialCharPassword() {
        String password = "abcdefG1";
        boolean result = userService.validatePassword(password);
        Assert.assertFalse(result);
    }

    @Test
    void validPassword() {
        String password = "Abcdef1*";
        boolean result = userService.validatePassword(password);
        Assert.assertTrue(result);
    }

    @Test
    void takenEmail() {
        when(mockDAO.findUserByEmail("test@gmail.com")).thenReturn(Optional.of(testUser));

        boolean isEmailAvailable = userService.isEmailAvailable("test@gmail.com");

        Assert.assertFalse(isEmailAvailable);
    }

    @Test
    void availableEmail() {
        when(mockDAO.findUserByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        boolean isEmailAvailable = userService.isEmailAvailable("nonexistent@example.com");

        Assert.assertTrue(isEmailAvailable);
    }
}
