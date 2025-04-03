package com.revature.controller;


import com.revature.dto.request.HotelFilterDTO;
import com.revature.exceptions.ForbiddenActionException;
import com.revature.exceptions.ResourceNotFoundException;
import com.revature.exceptions.UnauthenticatedException;
import com.revature.models.Hotel;
import com.revature.models.Role;
import com.revature.services.HotelService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("hotels")
public class HotelController {

    private final HotelService hotelService;

    @Autowired
    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Hotel createHotelHandler(@RequestBody Hotel hotel, HttpSession session){
        if (session.getAttribute("userId") == null){
            throw new UnauthenticatedException("User is not authenticated");
        }

        // I need to make sure the role of the user is a teacher
        if (session.getAttribute("role") != Role.OWNER){
            throw new ForbiddenActionException("You must be a teacher to access this");
        }

        return hotelService.createHotel((int) session.getAttribute("userId"), hotel);
    }

    @GetMapping
    public List<Hotel> getAllHotelsHandler(){
        return hotelService.getAllHotels();
    }

    @GetMapping("{hotelId}")
    public Hotel getHotelHandler(@PathVariable int hotelId){
        return hotelService.getHotelById(hotelId).orElseThrow(
        ()-> new ResourceNotFoundException("No hotel with id: " + hotelId));
    }

    @PostMapping("/filter")
    public List<Hotel> filterHotels(@RequestBody HotelFilterDTO filter) {
        return hotelService.filterHotels(filter);
    }

    @PutMapping("{hotelId}")
    public Optional<Hotel> updateHotelHandler(@PathVariable int hotelId, @RequestBody Hotel updatedHotel, HttpSession session) {
        if (session.getAttribute("userId") == null) {
            throw new UnauthenticatedException("User is not authenticated");
        }

        if (session.getAttribute("role") != Role.OWNER) {
            throw new ForbiddenActionException("You must be an owner to modify this hotel");
        }

        if(hotelService.checkHotelExisting(hotelId)){
            throw new ResourceNotFoundException("No hotel with id: " + hotelId);
        }

        if (hotelService.isUserOwnerOfHotel((int) session.getAttribute("userId"),hotelId)){
            throw new ForbiddenActionException("You must be the owner of this hotel to make updates");
        }

        return hotelService.updateHotel(hotelId, updatedHotel);
    }

    @DeleteMapping("{hotelId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteHotelHandler(@PathVariable int hotelId, HttpSession session) {
        if (session.getAttribute("userId") == null) {
            throw new UnauthenticatedException("User is not authenticated");
        }

        if (session.getAttribute("role") != Role.OWNER) {
            throw new ForbiddenActionException("You must be an owner to modify this hotel");
        }

        if(hotelService.checkHotelExisting(hotelId)){
            throw new ResourceNotFoundException("No hotel with id: " + hotelId);
        }

        if (hotelService.isUserOwnerOfHotel((int) session.getAttribute("userId"),hotelId)){
            throw new ForbiddenActionException("You must be the owner of this hotel to make updates");
        }

        hotelService.deleteHotel(hotelId);
    }


    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> resourceNotFoundHandler(ResourceNotFoundException e){
        return Map.of(
                "error", e.getMessage()
        );
    }
}
