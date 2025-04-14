package com.revature.services;


import com.revature.dto.request.HotelFilterDTO;
import com.revature.dto.request.RoomFilterDTO;
import com.revature.dto.response.RoomTypeWithDetailsDTO;
import com.revature.dto.response.RoomWithDetailsDTO;
import com.revature.exceptions.ResourceNotFoundException;
import com.revature.models.Hotel;
import com.revature.models.HotelRoom;
import com.revature.models.User;
import com.revature.repos.HotelDAO;
import com.revature.repos.HotelRoomDAO;
import com.revature.repos.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class HotelService {

    private final HotelDAO hotelDAO;
    private final UserDAO userDAO;
    private final HotelRoomDAO hotelRoomDAO;

    private static final ZoneId MEXICO_CITY_ZONE = ZoneId.of("America/Mexico_City");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    public HotelService(HotelDAO hotelDAO, UserDAO userDAO, HotelRoomDAO hotelRoomDAO) {
        this.hotelDAO = hotelDAO;
        this.userDAO =userDAO;
        this.hotelRoomDAO=hotelRoomDAO;
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

        Set<Hotel> userHotels = returnedUser.getHotels();
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

            return hotelDAO.findHotelsByFilter(filter.getName(), filter.getCountry(), filter.getState(), filter.getCity(), filter.getOwner());
    }

    public List<RoomWithDetailsDTO> getRoomsByFilters(RoomFilterDTO roomFilterDTO) {
        Integer hotelId = roomFilterDTO.getHotelId() == 0 ? null : roomFilterDTO.getHotelId();
        String country = roomFilterDTO.getCountry() == null || roomFilterDTO.getCountry().isEmpty() ? null : roomFilterDTO.getCountry();
        String state = roomFilterDTO.getState() == null || roomFilterDTO.getState().isEmpty() ? null : roomFilterDTO.getState();
        String city = roomFilterDTO.getCity() == null || roomFilterDTO.getCity().isEmpty() ? null : roomFilterDTO.getCity();
        Integer roomId = roomFilterDTO.getRoomId() == 0 ? null : roomFilterDTO.getRoomId();
        Integer roomTypeId = roomFilterDTO.getRoomTypeId() == 0 ? null : roomFilterDTO.getRoomTypeId();
        Integer guestNumber = roomFilterDTO.getGuestNumber() == 0 ? null : roomFilterDTO.getGuestNumber();
        Double price = roomFilterDTO.getPrice() == 0.0 ? null : roomFilterDTO.getPrice();

        // Initialize with current date and default times
        LocalDate today = LocalDate.now();

        // Parse check-in date or use today with 9:00 AM
        LocalDate checkInDate;
        if (roomFilterDTO.getCheckIn() == null || roomFilterDTO.getCheckIn().isEmpty()) {
            checkInDate = today;
        } else {
            checkInDate = LocalDate.parse(roomFilterDTO.getCheckIn(), DATE_FORMATTER);
        }
        ZonedDateTime checkInDateTime = checkInDate.atTime(9, 0).atZone(MEXICO_CITY_ZONE);

        // Parse check-out date or use tomorrow with 3:00 PM
        LocalDate checkOutDate;
        if (roomFilterDTO.getCheckOut() == null || roomFilterDTO.getCheckOut().isEmpty()) {
            checkOutDate = today.plusDays(1);
        } else {
            checkOutDate = LocalDate.parse(roomFilterDTO.getCheckOut(), DATE_FORMATTER);
        }
        ZonedDateTime checkOutDateTime = checkOutDate.atTime(15, 0).atZone(MEXICO_CITY_ZONE);

        // Convert to java.util.Date for the DAO method
        Date checkIn = Date.from(checkInDateTime.toInstant());
        Date checkOut = Date.from(checkOutDateTime.toInstant());

        List<HotelRoom> rooms = hotelRoomDAO.findAvailableRooms(
                hotelId, country, state, city, roomId, roomTypeId,
                checkIn, checkOut, guestNumber, price);

        return rooms.stream()
                .map(RoomWithDetailsDTO::new)
                .collect(Collectors.toList());
    }

    public List<RoomTypeWithDetailsDTO> getRoomsTypeByFilters(RoomFilterDTO roomFilterDTO) {
        Integer hotelId = roomFilterDTO.getHotelId() == 0 ? null : roomFilterDTO.getHotelId();
        String country = roomFilterDTO.getCountry() == null || roomFilterDTO.getCountry().isEmpty() ? null : roomFilterDTO.getCountry();
        String state = roomFilterDTO.getState() == null || roomFilterDTO.getState().isEmpty() ? null : roomFilterDTO.getState();
        String city = roomFilterDTO.getCity() == null || roomFilterDTO.getCity().isEmpty() ? null : roomFilterDTO.getCity();
        Integer roomId = roomFilterDTO.getRoomId() == 0 ? null : roomFilterDTO.getRoomId();
        Integer roomTypeId = roomFilterDTO.getRoomTypeId() == 0 ? null : roomFilterDTO.getRoomTypeId();
        Integer guestNumber = roomFilterDTO.getGuestNumber() == 0 ? null : roomFilterDTO.getGuestNumber();
        Double price = roomFilterDTO.getPrice() == 0.0 ? null : roomFilterDTO.getPrice();

        // Initialize with current date and default times
        LocalDate today = LocalDate.now();

        // Parse check-in date or use today with 9:00 AM
        System.out.println(roomFilterDTO);
        LocalDate checkInDate;
        if (roomFilterDTO.getCheckIn() == null || roomFilterDTO.getCheckIn().isEmpty()) {
            System.out.println("today");
            checkInDate = today;
        } else {
            System.out.println(roomFilterDTO.getCheckIn());
            checkInDate = LocalDate.parse(roomFilterDTO.getCheckIn(), DATE_FORMATTER);
        }
        ZonedDateTime checkInDateTime = checkInDate.atTime(9, 0).atZone(MEXICO_CITY_ZONE);

        // Parse check-out date or use tomorrow with 3:00 PM
        LocalDate checkOutDate;
        if (roomFilterDTO.getCheckOut() == null || roomFilterDTO.getCheckOut().isEmpty()) {
            System.out.println("tmoroow");

            checkOutDate = today.plusDays(1);
        } else {
            System.out.println(roomFilterDTO.getCheckOut());
            checkOutDate = LocalDate.parse(roomFilterDTO.getCheckOut(), DATE_FORMATTER);
        }
        ZonedDateTime checkOutDateTime = checkOutDate.atTime(15, 0).atZone(MEXICO_CITY_ZONE);

        // Convert to java.util.Date for the DAO method
        Date checkIn = Date.from(checkInDateTime.toInstant());
        Date checkOut = Date.from(checkOutDateTime.toInstant());

        List<HotelRoom> rooms = hotelRoomDAO.findAvailableRooms(
                hotelId, country, state, city, roomId, roomTypeId,
                checkIn, checkOut, guestNumber, price);

        List<RoomTypeWithDetailsDTO> roomTypes = new ArrayList<>();

        rooms.stream().forEach(room -> {
            Optional<RoomTypeWithDetailsDTO> existingRoomType = roomTypes.stream()
                    .filter(rt -> rt.getHotelRoomTypeId() ==room.getRoomType().getHotelRoomTypeId())
                    .findFirst();

            if (existingRoomType.isPresent()) {
                RoomTypeWithDetailsDTO roomType = existingRoomType.get();
                roomType.setNumberRooms(roomType.getNumberRooms() + 1);
            } else {
                RoomTypeWithDetailsDTO newRoomType = new RoomTypeWithDetailsDTO(room.getRoomType());
                newRoomType.setNumberRooms(1);
                roomTypes.add(newRoomType);
            }
        });

        return roomTypes;
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
