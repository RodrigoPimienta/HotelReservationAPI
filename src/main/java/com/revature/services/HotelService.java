package com.revature.services;


import com.revature.dto.request.HotelFilterDTO;
import com.revature.exceptions.ResourceNotFoundException;
import com.revature.models.Hotel;
import com.revature.models.User;
import com.revature.repos.HotelDAO;
import com.revature.repos.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class HotelService {

    private final HotelDAO hotelDAO;
    private final UserDAO userDAO;

    @Autowired
    public HotelService(HotelDAO hotelDAO, UserDAO userDAO) {
        this.hotelDAO = hotelDAO;
        this.userDAO =userDAO;
    }

    // Availability
    public boolean checkHotelExisting(int hotelId){
        return hotelDAO.findById(hotelId).isEmpty();
    }

    public boolean isUserOwnerOfHotel(int userId, int hotelId) {
        Optional<User> userOptional = userDAO.findById(userId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Set<Hotel> hotels = user.getHotels();

            return hotels.stream().anyMatch(owner -> owner.getHotelId() == hotelId);
        }

        return false;
    }

    // CREATE
    public Hotel createHotel(int userId, Hotel hotelTobeCreated){
        // We need to make sure the user exists
        User returnedUser = userDAO.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("No User found with id: " + userId));

        Hotel hotelCreated = hotelDAO.save(hotelTobeCreated);

        Set<Hotel> userHotels = new HashSet<>();
        userHotels.add(hotelCreated);
        returnedUser.setHotels(userHotels);
        userDAO.save(returnedUser);

        return hotelCreated;
    }

    // RETRIEVE ALL
    public List<Hotel> getAllHotels(){
        return hotelDAO.findAll();
    }

    // RETRIEVE BY ID

    public Optional<Hotel> getHotelById(int hotelId){
        return hotelDAO.findById(hotelId);
    }

    // filters
    public List<Hotel> filterHotels(HotelFilterDTO filter) {
        if (filter.getName() == null && filter.getCountry() == null && filter.getState() == null && filter.getCity() == null) {
            return hotelDAO.findAll();
        } else {
            return hotelDAO.findHotelsByFilter(filter.getName(), filter.getCountry(), filter.getState(), filter.getCity());
        }
    }

    // PUT
    public Optional<Hotel> updateHotel(int hotelId, Hotel updatedHotel) {
        return hotelDAO.findById(hotelId).map(hotel -> {
            hotel.setName(updatedHotel.getName());
            hotel.setCountry(updatedHotel.getCountry());
            hotel.setState(updatedHotel.getState());
            hotel.setCity(updatedHotel.getCity());
            hotel.setStreet(updatedHotel.getStreet());
            hotel.setHouseNumber(updatedHotel.getHouseNumber());
            hotel.setPostalCode(updatedHotel.getPostalCode());
            return hotelDAO.save(hotel);
        });
    }

    // DELETE
    public void deleteHotel(int hotelId) {
         hotelDAO.deleteById(hotelId);
    }
}
