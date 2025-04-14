package com.revature.services;


import com.revature.dto.response.RoomTypeDTO;
import com.revature.exceptions.InvalidRequestBodyException;
import com.revature.exceptions.ResourceNotFoundException;
import com.revature.models.Hotel;
import com.revature.models.HotelRoomType;
import com.revature.models.User;
import com.revature.repos.HotelDAO;
import com.revature.repos.HotelRoomTypeDAO;
import com.revature.repos.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class HotelRoomTypeService {

    private final HotelRoomTypeDAO hotelRoomTypeDAO;
    private final HotelDAO hotelDAO;
    private final UserDAO userDAO;


    @Autowired
    public HotelRoomTypeService(HotelRoomTypeDAO hotelRoomTypeDAO, HotelDAO hotelDAO, UserDAO userDAO){
        this.hotelRoomTypeDAO=hotelRoomTypeDAO;
        this.hotelDAO=hotelDAO;
        this.userDAO=userDAO;
    }


    public boolean checkHotelExisting(int hotelId){
        return hotelDAO.findById(hotelId).isEmpty();
    }


    public boolean checkHotelRoomTypeExisting(int hotelRoomTypeId){
        return hotelRoomTypeDAO.findById(hotelRoomTypeId).isEmpty();
    }


    public boolean checkHotelRelationWithRoomType(int hotelId, int hotelRoomTypeId){
        return hotelRoomTypeDAO.findByHotelRoomTypeIdAndHotel_HotelId(hotelRoomTypeId, hotelId).isEmpty();
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
    public List<HotelRoomType> createHotelRoomType(int hotelId, List<HotelRoomType> hotelRoomTypesToBeCreated) {

        if(hotelRoomTypesToBeCreated.isEmpty()){
            throw new InvalidRequestBodyException("Must be at least one room type to save");
        }

        Optional<Hotel> hotelOptional = hotelDAO.findById(hotelId);

        if (hotelOptional.isEmpty()) {
            throw new ResourceNotFoundException("Not hotel with id:"+hotelId);
        }

        Hotel hotel = hotelOptional.get();

        for (HotelRoomType room : hotelRoomTypesToBeCreated) {
            room.setHotel(hotel);
            hotelRoomTypeDAO.save(room);
        }

        return hotelRoomTypesToBeCreated;
    }

    // RETRIEVE BY ID
    public Optional<HotelRoomType> getHotelRoomTypeById(int hotelId, int hotelRoomTypeId){
        return hotelRoomTypeDAO.findByHotelRoomTypeIdAndHotel_HotelId(hotelRoomTypeId, hotelId);
    }


    // RETRIEVE BY HOTEL
    public List<RoomTypeDTO> getHotelRoomTypesByHotel(int hotelId) {
        List<HotelRoomType> hotelRoomsTypes = hotelRoomTypeDAO.findByHotelId(hotelId);

        return hotelRoomsTypes.stream()
                .map(RoomTypeDTO::new)
                .collect(Collectors.toList());
    }

    // PUT
    public Optional<HotelRoomType> updateHotelRoomType(int hotelId,int hotelRoomTypeId, HotelRoomType updateHotelRoomType) {
        return hotelRoomTypeDAO.findByHotelRoomTypeIdAndHotel_HotelId(hotelRoomTypeId,hotelId).map(hotelRoomType -> {
            hotelRoomType.setName(updateHotelRoomType.getName());
            hotelRoomType.setMaxGuest(updateHotelRoomType.getMaxGuest());
            hotelRoomType.setPrice(updateHotelRoomType.getPrice());
            return hotelRoomTypeDAO.save(hotelRoomType);
        });
    }

    // DELETE
    public void deleteHotelRoomType(int hotelRoomTypeId) {
        hotelRoomTypeDAO.deleteById(hotelRoomTypeId);
    }

}
