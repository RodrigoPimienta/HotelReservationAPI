package com.revature.services;

import com.revature.exceptions.InvalidRequestBodyException;
import com.revature.exceptions.ResourceNotFoundException;
import com.revature.models.Hotel;
import com.revature.models.HotelImage;
import com.revature.models.User;
import com.revature.repos.HotelDAO;
import com.revature.repos.HotelImageDAO;
import com.revature.repos.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class HotelImageService {

    private final HotelImageDAO hotelImageDAO;
    private final HotelDAO hotelDAO;
    private final UserDAO userDAO;


    @Autowired
    public HotelImageService(HotelImageDAO hotelImageDAO, HotelDAO hotelDAO, UserDAO userDAO){
        this.hotelImageDAO=hotelImageDAO;
        this.hotelDAO=hotelDAO;
        this.userDAO=userDAO;
    }

    public boolean checkHotelExisting(int hotelId){
        return hotelDAO.findById(hotelId).isEmpty();
    }


    public boolean checkHotelImageExisting(int hotelImageId){
        return hotelImageDAO.findById(hotelImageId).isEmpty();
    }


    public boolean checkHotelRelationWithImage(int hotelId, int hotelImageId){
        return hotelImageDAO.findByHotelImageIdAndHotel_HotelId(hotelImageId, hotelId).isEmpty();
    }

    public boolean isUserOwnerOfHotel(int userId, int hotelId) {
        Optional<User> userOptional = userDAO.findById(userId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Set<Hotel> hotels = user.getHotels();

            return hotels.stream().noneMatch(owner -> owner.getHotelId() == hotelId);
        }

        return true;
    }

    // CREATE
    public List<HotelImage> createHotelImages(int hotelId, List<HotelImage> hotelImagesToBeCreated) {

        if(hotelImagesToBeCreated.isEmpty()){
            throw new InvalidRequestBodyException("Must be at least one image to save");
        }

        Optional<Hotel> hotelOptional = hotelDAO.findById(hotelId);

        if (hotelOptional.isEmpty()) {
            throw new ResourceNotFoundException("Not hotel with id:"+hotelId);
        }

        Hotel hotel = hotelOptional.get();

        for (HotelImage image : hotelImagesToBeCreated) {
            image.setHotel(hotel);
            hotelImageDAO.save(image);
        }

        return hotelImagesToBeCreated;
    }

    // RETRIEVE BY ID
    public Optional<HotelImage> getHotelImageById(int hotelId, int hotelImageId){
        return hotelImageDAO.findByHotelImageIdAndHotel_HotelId(hotelImageId, hotelId);
    }

    // RETRIEVE BY HOTEL
    public List<HotelImage> getHotelImagesByHotel(int hotelId){
      return hotelImageDAO.findByHotelId(hotelId);
    }

    // PUT
    public Optional<HotelImage> updateHotelImage(int hotelId,int hotelImageId, HotelImage updateHotelImage) {
        return hotelImageDAO.findByHotelImageIdAndHotel_HotelId(hotelImageId,hotelId).map(hotelImage -> {
           hotelImage.setName(updateHotelImage.getName());
           hotelImage.setDescription(updateHotelImage.getDescription());
           hotelImage.setUrl(updateHotelImage.getUrl());
           return hotelImageDAO.save(hotelImage);
        });
    }

    // DELETE
    public void deleteHotelImage(int hotelImageId) {
        hotelImageDAO.deleteById(hotelImageId);
    }

}
