package com.revature.services;

import com.revature.exceptions.InvalidRequestBodyException;
import com.revature.exceptions.ResourceNotFoundException;
import com.revature.models.Hotel;
import com.revature.models.HotelAmenity;
import com.revature.models.User;
import com.revature.repos.HotelAmenityDAO;
import com.revature.repos.HotelDAO;
import com.revature.repos.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class HotelAmenityService {


    private final HotelAmenityDAO hotelAmenityDAO;
    private final HotelDAO hotelDAO;
    private final UserDAO userDAO;

    @Autowired
    public HotelAmenityService(HotelAmenityDAO hotelImageDAO,HotelDAO hotelDAO, UserDAO userDAO){
        this.hotelAmenityDAO=hotelImageDAO;
        this.hotelDAO=hotelDAO;
        this.userDAO=userDAO;
    }

    public boolean checkHotelExisting(int hotelId){
        return hotelDAO.findById(hotelId).isEmpty();
    }


    public boolean checkHotelAmenityExisting(int hotelAmenityId){
        return hotelAmenityDAO.findById(hotelAmenityId).isEmpty();
    }


    public boolean checkHotelRelationWithAmenity(int hotelId, int hotelAmenityId){
        return hotelAmenityDAO.findByHotelAmenityIdAndHotel_HotelId(hotelAmenityId, hotelId).isEmpty();
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
    public List<HotelAmenity> createHotelAmenity(int hotelId, List<HotelAmenity> hotelAmenitiesToBeCreated) {
        if(hotelAmenitiesToBeCreated.isEmpty()){
            throw new InvalidRequestBodyException("Must be at least one amenity to save");
        }

        Optional<Hotel> hotelOptional = hotelDAO.findById(hotelId);

        if (hotelOptional.isEmpty()) {
            throw new ResourceNotFoundException("Not hotel with id:"+hotelId);
        }

        Hotel hotel = hotelOptional.get();

        for (HotelAmenity amenity : hotelAmenitiesToBeCreated) {
            amenity.setHotel(hotel);
            hotelAmenityDAO.save(amenity);
        }

        return hotelAmenitiesToBeCreated;
    }

    // RETRIEVE BY ID
    public Optional<HotelAmenity> getHotelAmenityById(int hotelId, int hotelAmenityId){
        return hotelAmenityDAO.findByHotelAmenityIdAndHotel_HotelId(hotelAmenityId, hotelId);
    }

    // RETRIEVE BY HOTEL
    public List<HotelAmenity> getHotelAmenitiesByHotel(int hotelId){
        return hotelAmenityDAO.findByHotelId(hotelId);
    }

    // PUT
    public Optional<HotelAmenity> updateHotelAmenity(int hotelId,int hotelAmenityId, HotelAmenity updateHotelAmenity) {
        return hotelAmenityDAO.findByHotelAmenityIdAndHotel_HotelId(hotelAmenityId,hotelId).map(hotelAmenity -> {
            hotelAmenity.setName(updateHotelAmenity.getName());
            hotelAmenity.setDescription(updateHotelAmenity.getDescription());
            hotelAmenity.setUrl(updateHotelAmenity.getUrl());
            return hotelAmenityDAO.save(hotelAmenity);
        });
    }

    // DELETE
    public void deleteHotelAmenity(int hotelAmenityId) {
        hotelAmenityDAO.deleteById(hotelAmenityId);
    }
}
