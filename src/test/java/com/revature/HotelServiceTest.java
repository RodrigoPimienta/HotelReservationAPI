package com.revature;

import com.revature.exceptions.ResourceNotFoundException;
import com.revature.models.Hotel;
import com.revature.models.User;
import com.revature.repos.HotelDAO;
import com.revature.repos.UserDAO;
import com.revature.services.HotelService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HotelServiceTest {

    @Mock
    private HotelDAO mockedHotelDAO;

    @Mock
    private UserDAO mockedUserDAO;

    @InjectMocks
    private HotelService hotelService;

    @Test
    public void CreateHotelShouldThrowNotFoundErrorIfUserDoesNotExist() {
        // Arrange
        int userId = 1;
        Hotel hotelToBeRegistered = new Hotel(1, "Main hotel", "Mexico", "Colima", "Colima", "Main street", "209", "28900");

        when(mockedUserDAO.findById(userId)).thenReturn(Optional.empty());

        // Act y Assert
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            hotelService.createHotel(userId, hotelToBeRegistered);
        });
    }

    @Test
    public void CreateHotelShouldBeSuccessIfUserExists() {
        // Arrange
        int userId = 1;
        Hotel hotelToBeRegistered = new Hotel(1, "Main hotel", "Mexico", "Colima", "Colima", "Main street", "209", "28900");
        Hotel sampleHotel = new Hotel(1, "Main hotel", "Mexico", "Colima", "Colima", "Main street", "209", "28900");
        User sampleUser = new User();
        sampleUser.setUserId(userId);
        Set<Hotel> mockedUserHotels = new HashSet<>(); // Usa un HashSet para el mock
        sampleUser.setHotels(mockedUserHotels);

        when(mockedUserDAO.findById(userId)).thenReturn(Optional.of(sampleUser));
        when(mockedHotelDAO.save(any(Hotel.class))).thenReturn(sampleHotel);
        when(mockedUserDAO.save(any(User.class))).thenReturn(sampleUser);

        // Act
        Hotel hotelSave = hotelService.createHotel(userId, hotelToBeRegistered);

        // Assert
        Assertions.assertEquals(sampleHotel, hotelSave);
        Assertions.assertTrue(mockedUserHotels.contains(sampleHotel));
    }
}
