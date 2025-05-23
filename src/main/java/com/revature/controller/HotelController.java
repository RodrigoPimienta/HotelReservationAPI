package com.revature.controller;


import com.revature.dto.request.HotelFilterDTO;
import com.revature.dto.request.RoomFilterDTO;
import com.revature.dto.response.RoomTypeWithDetailsDTO;
import com.revature.dto.response.RoomWithDetailsDTO;
import com.revature.exceptions.ForbiddenActionException;
import com.revature.exceptions.ResourceNotFoundException;
import com.revature.exceptions.UnauthenticatedException;
import com.revature.models.Hotel;
import com.revature.models.Role;
import com.revature.services.HotelService;
import com.revature.util.SendGridUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
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
    public List<Hotel> filterHotels(@RequestBody HotelFilterDTO filter, HttpSession session) {
        if (session.getAttribute("userId") == null){
            throw new UnauthenticatedException("User is not authenticated");
        }

        // I need to make sure the role of the user is a teacher
        if (session.getAttribute("role") == Role.OWNER){
            filter.setOwner((Integer) session.getAttribute("userId"));
        }else{
            filter.setOwner(null);
        }

        return hotelService.filterHotels(filter);
    }

    @PostMapping("/rooms/filter")
    public List<RoomWithDetailsDTO> filterRoomsHandler(@RequestBody RoomFilterDTO roomFilterDTO, HttpSession session) throws ParseException {

        if (session.getAttribute("userId") == null) {
            throw new UnauthenticatedException("User is not authenticated");
        }

        if (session.getAttribute("role") != Role.USER) {
            roomFilterDTO.setOwner(true);
        }

        return hotelService.getRoomsByFilters(roomFilterDTO);
    }

    @PostMapping("/roomTypes/filter")
    public List<RoomTypeWithDetailsDTO> filterRoomTypesHandler(@RequestBody RoomFilterDTO roomFilterDTO, HttpSession session) throws ParseException {

        if (session.getAttribute("userId") == null) {
            throw new UnauthenticatedException("User is not authenticated");
        }

        if (session.getAttribute("role") != Role.USER) {
            roomFilterDTO.setOwner(true);
        }

        return hotelService.getRoomsTypeByFilters(roomFilterDTO);
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

        if (!hotelService.isUserOwnerOfHotel((int) session.getAttribute("userId"),hotelId)){
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

        if (!hotelService.isUserOwnerOfHotel((int) session.getAttribute("userId"),hotelId)){
            throw new ForbiddenActionException("You must be the owner to delete this hotel");
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
