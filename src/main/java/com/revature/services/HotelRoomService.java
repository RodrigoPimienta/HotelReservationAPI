package com.revature.services;

import com.revature.dto.request.HotelRoomDTO;
import com.revature.dto.response.HotelRoomWithRoomTypeDTO;
import com.revature.exceptions.InvalidRequestBodyException;
import com.revature.exceptions.ResourceNotFoundException;
import com.revature.models.Hotel;
import com.revature.models.HotelRoom;
import com.revature.models.HotelRoomType;
import com.revature.models.User;
import com.revature.repos.HotelDAO;
import com.revature.repos.HotelRoomDAO;
import com.revature.repos.HotelRoomTypeDAO;
import com.revature.repos.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class HotelRoomService {

    private final HotelRoomDAO hotelRoomDAO;
    private final HotelRoomTypeDAO hotelRoomTypeDAO;
    private final HotelDAO hotelDAO;
    private final UserDAO userDAO;


    @Autowired
    public HotelRoomService(HotelRoomDAO HotelRoomDAO, HotelRoomTypeDAO hotelRoomTypeDAO, HotelDAO hotelDAO, UserDAO userDAO){
        this.hotelRoomDAO=HotelRoomDAO;
        this.hotelRoomTypeDAO=hotelRoomTypeDAO;
        this.hotelDAO=hotelDAO;
        this.userDAO=userDAO;
    }


    public boolean checkHotelExisting(int hotelId){
        return hotelDAO.findById(hotelId).isEmpty();
    }


    public boolean checkHotelRoomExisting(int HotelRoomId){
        return hotelRoomDAO.findById(HotelRoomId).isEmpty();
    }


    public boolean checkHotelRelationWithRoomType(int hotelId, int hotelRoomTypeId){
        return hotelRoomTypeDAO.findByHotelRoomTypeIdAndHotel_HotelId(hotelRoomTypeId, hotelId).isEmpty();
    }

    public boolean checkHotelRelationWithRoom(int hotelId, int hotelRoomId){
        return hotelRoomDAO.findByHotelRoomIdAndHotel_HotelId(hotelRoomId, hotelId).isEmpty();
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
    public List<HotelRoom> createHotelRoomFromDTO(int hotelId, List<HotelRoomDTO> HotelRoomsToBeCreated) {
        if (HotelRoomsToBeCreated.isEmpty()) {
            throw new InvalidRequestBodyException("Must be at least one room type to save");
        }

        Optional<Hotel> hotelOptional = hotelDAO.findById(hotelId);

        if (hotelOptional.isEmpty()) {
            throw new ResourceNotFoundException("Not hotel with id:" + hotelId);
        }

        Hotel hotel = hotelOptional.get();

        List<HotelRoom> hotelRooms = new ArrayList<>();
        for (HotelRoomDTO roomDTO : HotelRoomsToBeCreated) {
            Optional<HotelRoomType> roomTypeOptional = hotelRoomTypeDAO.findById(roomDTO.getHotelRoomTypeId());

            if (roomTypeOptional.isEmpty()) {
                throw new ResourceNotFoundException("No HotelRoomType with id: " + roomDTO.getHotelRoomTypeId());
            }

            HotelRoomType roomType = roomTypeOptional.get();

            HotelRoom room = new HotelRoom();
            room.setNum(roomDTO.getNum());
            room.setRoomType(roomType);
            room.setHotel(hotel);

            if (checkHotelRelationWithRoomType(hotelId, roomDTO.getHotelRoomTypeId())) {
                throw new ResourceNotFoundException("No hotel RoomType with id: " + roomDTO.getHotelRoomTypeId() + " is related to hotel with id: " + hotelId);
            }

            hotelRoomDAO.save(room);
            hotelRooms.add(room);
        }

        return hotelRooms;
    }
    // RETRIEVE BY ID
    public Optional<HotelRoomWithRoomTypeDTO> getHotelRoomById(int hotelId, int hotelRoomId) {
        Optional<HotelRoom> optionalHotelRoom = hotelRoomDAO.findByHotelRoomIdAndHotel_HotelId(hotelRoomId, hotelId);

        if (optionalHotelRoom.isPresent()) {
            HotelRoom hotelRoom = optionalHotelRoom.get();
            HotelRoomWithRoomTypeDTO dto = new HotelRoomWithRoomTypeDTO(hotelRoom);
            return Optional.of(dto);
        } else {
            return Optional.empty();
        }
    }

    // RETRIEVE BY HOTEL
    public List<HotelRoomWithRoomTypeDTO> getHotelRoomsByHotel(int hotelId) {
        List<HotelRoom> hotelRooms = hotelRoomDAO.findByHotelId(hotelId);

        return hotelRooms.stream()
                .map(HotelRoomWithRoomTypeDTO::new)
                .collect(Collectors.toList());
    }
    // PUT
    public Optional<HotelRoom> updateHotelRoom(int hotelId,int HotelRoomId, HotelRoom updateHotelRoom) {
        return hotelRoomDAO.findByHotelRoomIdAndHotel_HotelId(HotelRoomId,hotelId).map(HotelRoom -> {
            HotelRoom.setNum(updateHotelRoom.getNum());
            HotelRoom.setRoomType(updateHotelRoom.getRoomType());
            return hotelRoomDAO.save(HotelRoom);
        });
    }

    // DELETE
    public void deleteHotelRoom(int HotelRoomId) {
        hotelRoomDAO.deleteById(HotelRoomId);
    }

}
