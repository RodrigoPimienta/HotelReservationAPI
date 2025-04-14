package com.revature.controller;

import com.revature.dto.request.ReservationCreateDTO;
import com.revature.dto.request.ReservationFilterDTO;
import com.revature.dto.request.ReservationUpdateStatusDTO;
import com.revature.dto.response.ReservationWithDetailsDTO;
import com.revature.exceptions.*;
import com.revature.models.Reservation;
import com.revature.models.Role;
import com.revature.services.ReservationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @Autowired
    public ReservationController(ReservationService reservationService){
        this.reservationService=reservationService;
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationWithDetailsDTO createReservationByRoomTypeHandler(@RequestBody ReservationCreateDTO reservationCreateDTO, HttpSession session){
        if (session.getAttribute("userId") == null) {
            throw new UnauthenticatedException("User is not authenticated");
        }

        if (session.getAttribute("role") != Role.USER) {
            throw new ForbiddenActionException("You must be an owner to modify this hotel");
        }


        return reservationService.createReservation((int) session.getAttribute("userId"),reservationCreateDTO);
    }

    @GetMapping("user")
    public List<ReservationWithDetailsDTO> getUserReservationHandler( HttpSession session){

        if (session.getAttribute("userId") == null) {
            throw new UnauthenticatedException("User is not authenticated");
        }

        if (session.getAttribute("role") != Role.USER) {
            throw new ForbiddenActionException("You must be an owner to modify this hotel");
        }

        return reservationService.getUserReservations((int) session.getAttribute("userId"));
    }

    @GetMapping("{reservationId}")
    public ReservationWithDetailsDTO getReservationHandler(@PathVariable int reservationId,  HttpSession session){
        if (session.getAttribute("userId") == null) {
            throw new UnauthenticatedException("User is not authenticated");
        }

        int userId = (int) session.getAttribute("userId");
        Role role = Role.valueOf(session.getAttribute("role").toString());

        return reservationService.getReservation(userId, role,reservationId);
    }

    @PostMapping("/filter")
    public List<ReservationWithDetailsDTO> filterReservationsHandler(@RequestBody ReservationFilterDTO filter, HttpSession session) throws ParseException {

        if (session.getAttribute("userId") == null) {
            throw new UnauthenticatedException("User is not authenticated");
        }

        if (session.getAttribute("role") == Role.USER) {
            filter.setOwner(false);
        }else{
            filter.setOwner(true);
        }

        return reservationService.getReservationsWithFilter((int) session.getAttribute("userId"),filter);
    }

    @PutMapping("{reservationId}")
    public Reservation updateReservationStatusHandler(@PathVariable int reservationId, @RequestBody ReservationUpdateStatusDTO reservationUpdateStatusDTO, HttpSession session){

        if (session.getAttribute("userId") == null) {
            throw new UnauthenticatedException("User is not authenticated");
        }

        if (session.getAttribute("role") != Role.OWNER) {
            throw new ForbiddenActionException("You must be an owner to modify this hotel");
        }
        if(reservationService.isUserNotOwnerOfHotelReservation((int) session.getAttribute("userId"), reservationId)){
            throw new ForbiddenActionException("You must be the owner of the reservation hotel to make updates in the status");
        }

        return reservationService.updateReservationStatus( reservationId,reservationUpdateStatusDTO);
    }


    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> resourceNotFoundHandler(ResourceNotFoundException e){
        return Map.of(
                "error", e.getMessage()
        );
    }

    @ExceptionHandler(InvalidRequestBodyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> invalidRequestBodyException (InvalidRequestBodyException e){
        return Map.of(
                "error", e.getMessage()
        );
    }
}
