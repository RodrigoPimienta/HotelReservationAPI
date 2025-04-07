package com.revature.services;

import com.revature.dto.request.ReservationCreateDTO;
import com.revature.dto.request.ReservationFilterDTO;
import com.revature.dto.request.ReservationUpdateStatusDTO;
import com.revature.dto.request.RoomFilterDTO;

import com.revature.dto.response.RoomWithDetailsDTO;
import com.revature.exceptions.ForbiddenActionException;
import com.revature.exceptions.InvalidRequestBodyException;
import com.revature.exceptions.ResourceNotFoundException;
import com.revature.models.*;
import com.revature.repos.HotelRoomDAO;
import com.revature.repos.ReservationDAO;
import com.revature.repos.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class ReservationService {

    private final ReservationDAO reservationDAO;
    private final UserDAO userDAO;
    private final HotelRoomDAO hotelRoomDAO;

    private static final ZoneId MEXICO_CITY_ZONE = ZoneId.of("America/Mexico_City");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    @Autowired
    public ReservationService(ReservationDAO reservationDAO, UserDAO userDAO, HotelRoomDAO hotelRoomDAO){
        this.reservationDAO=reservationDAO;
        this.userDAO=userDAO;
        this.hotelRoomDAO=hotelRoomDAO;
    }

    public boolean isUserNotOwnerOfHotelReservation(int userId, int reservationId) {
        List<Reservation> reservations = reservationDAO.findReservationsWithFilters(
                reservationId, userId, null, null, null, null, true);
        System.out.println(reservations);
        return reservations.isEmpty();
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

    public Reservation createReservation(int userId, ReservationCreateDTO reservationToBeCreated) {
        try {
            // Parse check-in date and set time to 9:00 AM
            LocalDate checkInDate = LocalDate.parse(reservationToBeCreated.getCheckIn(), DATE_FORMATTER);
            ZonedDateTime checkInDateTime = checkInDate.atTime(9, 0).atZone(MEXICO_CITY_ZONE);

            // Parse check-out date and set time to 3:00 PM
            LocalDate checkOutDate = LocalDate.parse(reservationToBeCreated.getCheckOut(), DATE_FORMATTER);
            ZonedDateTime checkOutDateTime = checkOutDate.atTime(15, 0).atZone(MEXICO_CITY_ZONE);

            // Validate dates are not the same day
            if (checkInDate.isEqual(checkOutDate)) {
                throw new InvalidRequestBodyException("Check-in and check-out dates cannot be the same day.");
            }

            // Convert to java.util.Date for compatibility with existing code
            Date checkIn = Date.from(checkInDateTime.toInstant());
            Date checkOut = Date.from(checkOutDateTime.toInstant());

            // Find available rooms
            List<HotelRoom> availableRooms = hotelRoomDAO.findAvailableRooms(
                    reservationToBeCreated.getHotelId(),
                    null, null, null, null,
                    reservationToBeCreated.getRoomTypeId(),
                    checkIn, checkOut,
                    reservationToBeCreated.getGuestNumber(),
                    null
            );

            if (availableRooms.isEmpty()) {
                throw new ResourceNotFoundException("There is no room available to match your specifications");
            }

            // Find user
            Optional<User> existingUserOptional = userDAO.findById(userId);
            if (existingUserOptional.isEmpty()) {
                throw new ResourceNotFoundException("User not found with id: " + userId);
            }
            User user = existingUserOptional.get();

            // Create and save reservation
            HotelRoom roomSelected = availableRooms.get(0);
            Reservation reservation = new Reservation();
            reservation.setTotalGuest(reservationToBeCreated.getGuestNumber());
            reservation.setCheckIn(checkIn);
            reservation.setCheckOut(checkOut);
            reservation.setTotal(roomSelected.getRoomType().getPrice());
            reservation.setStatus(ReservationStatus.PENDING);
            reservation.setRoom(roomSelected);
            reservation.setUser(user);

            return reservationDAO.save(reservation);
        } catch (DateTimeParseException e) {
            throw new InvalidRequestBodyException("Invalid date format. Please use yyyy-MM-dd");
        }
    }

    public Reservation getReservation(int userId, Role role, int reservationId){
        Reservation reservation = reservationDAO.findById(reservationId).orElseThrow(
                () -> new ResourceNotFoundException("No reservation found with id: " + reservationId));

        if (role == Role.OWNER && isUserNotOwnerOfHotelReservation(userId, reservationId)) {
            throw  new ForbiddenActionException("You must be the owner of the reservation hotel to retrieve this reservation");
        }

        if(role == Role.USER && reservation.getUser().getUserId() != userId) {
            throw new ForbiddenActionException("You are not allow to retrieve this reservation");
        }

        return reservation;
    }

    public List<Reservation> getUserReservations(int userId){
        return reservationDAO.findByUser_UserId(userId);
    }

    public List<Reservation> getReservationsWithFilter(int userId, ReservationFilterDTO reservationFilterDTO) throws ParseException {
        return reservationDAO.findReservationsWithFilters(
                reservationFilterDTO.getReservationId(),
                userId,
                reservationFilterDTO.getHotelId(),
                reservationFilterDTO.getRoomTypeId(),
                reservationFilterDTO.getRoomId(),
                reservationFilterDTO.getStatus(),
                reservationFilterDTO.isOwner()
        );
    }

    public Reservation updateReservationStatus(int reservationId, ReservationUpdateStatusDTO reservationUpdateStatusDTO){
        Reservation reservation = reservationDAO.findById(reservationId).orElseThrow(
                () -> new ResourceNotFoundException("No reservation found with id: " + reservationId));

        if(reservation.getStatus() != ReservationStatus.PENDING){
            throw  new ForbiddenActionException("You can not change the status of a reservation which has been accepted or rejected");
        }

        if(reservationUpdateStatusDTO.getStatus() != ReservationStatus.ACCEPT && reservationUpdateStatusDTO.getStatus() != ReservationStatus.REJECT ){
            throw  new InvalidRequestBodyException("Invalid status value. Status must be part of the catalog");
        }

        reservation.setStatus(reservationUpdateStatusDTO.getStatus()); // accepted or rejected
        return reservationDAO.save(reservation);

    }

}
