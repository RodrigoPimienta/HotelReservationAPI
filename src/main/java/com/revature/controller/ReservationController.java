package com.revature.controller;

import com.revature.dto.request.ReservationCreateDTO;
import com.revature.dto.request.ReservationFilterDTO;
import com.revature.dto.request.ReservationUpdateStatusDTO;
import com.revature.dto.response.ReservationWithDetailsDTO;
import com.revature.exceptions.*;
import com.revature.models.Reservation;
import com.revature.models.Role;
import com.revature.security.CustomUserDetails;
import com.revature.services.ReservationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @Autowired
    public ReservationController(ReservationService reservationService){
        this.reservationService=reservationService;
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationWithDetailsDTO createReservationByRoomTypeHandler(@RequestBody ReservationCreateDTO reservationCreateDTO, HttpSession session){
        return reservationService.createReservation((int) session.getAttribute("userId"),reservationCreateDTO);
    }
    @PreAuthorize("hasRole('USER')")
    @GetMapping("user")
    public List<ReservationWithDetailsDTO> getUserReservationHandler( HttpSession session){
        return reservationService.getUserReservations((int) session.getAttribute("userId"));
    }

    @GetMapping("{reservationId}")
    public ReservationWithDetailsDTO getReservationHandler(@AuthenticationPrincipal CustomUserDetails userDetails,@PathVariable int reservationId, HttpSession session){
        if (session.getAttribute("userId") == null) {
            throw new UnauthenticatedException("User is not authenticated");
        }

        Role role = Role.valueOf(userDetails.getAuthorities().toString());

        return reservationService.getReservation(userDetails.getUserId(),role ,reservationId);
    }

    @PostMapping("/filter")
    public List<ReservationWithDetailsDTO> filterReservationsHandler(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody ReservationFilterDTO filter, HttpSession session) throws ParseException {


        if (Objects.equals(userDetails.getAuthorities().toString(), Role.USER.toString())) {
            filter.setOwner(false);
        }else{
            filter.setOwner(true);
        }

        return reservationService.getReservationsWithFilter(userDetails.getUserId(),filter);
    }

    @PreAuthorize("hasRole('OWNER')")
    @PutMapping("{reservationId}")
    public Reservation updateReservationStatusHandler(@AuthenticationPrincipal CustomUserDetails userDetails,@PathVariable int reservationId, @RequestBody ReservationUpdateStatusDTO reservationUpdateStatusDTO, HttpSession session){

        if(reservationService.isUserNotOwnerOfHotelReservation(userDetails.getUserId(), reservationId)){
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
