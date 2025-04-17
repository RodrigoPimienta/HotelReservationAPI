package com.revature.controller;

import com.revature.dto.request.HotelRoomDTO;
import com.revature.dto.response.HotelRoomWithRoomTypeDTO;
import com.revature.exceptions.ForbiddenActionException;
import com.revature.exceptions.ResourceNotFoundException;
import com.revature.exceptions.UnauthenticatedException;

import com.revature.models.HotelRoom;
import com.revature.models.Role;

import com.revature.services.HotelRoomService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("hotels/{hotelId}/rooms")
public class HotelRoomController {

    private final HotelRoomService hotelRoomService;

    @Autowired
    public HotelRoomController(HotelRoomService hotelRoomService) {
        this.hotelRoomService = hotelRoomService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public List<HotelRoom> createRoomHandler(@PathVariable int hotelId, @RequestBody List<HotelRoomDTO> Rooms, HttpSession session) {

        if (session.getAttribute("userId") == null){
            throw new UnauthenticatedException("User is not authenticated");
        }

        // I need to make sure the role of the user is a teacher
        if (session.getAttribute("role") != Role.OWNER){
            throw new ForbiddenActionException("You must be a teacher to access this");
        }

        if (hotelRoomService.isUserOwnerOfHotel((int) session.getAttribute("userId"), hotelId)){
            throw new ForbiddenActionException("You must be the owner of this hotel to add room s");
        }

        return hotelRoomService.createHotelRoomFromDTO(hotelId, Rooms);
    }


    @GetMapping
    public List<HotelRoomWithRoomTypeDTO> getRoomsHandler(@PathVariable int hotelId) {

        if(hotelRoomService.checkHotelExisting(hotelId)){
            throw new ResourceNotFoundException("No hotel with id: " + hotelId);
        }

        return hotelRoomService.getHotelRoomsByHotel(hotelId);
    }

    @GetMapping("{hotelRoomId}")
    public HotelRoomWithRoomTypeDTO getHotelRoomHandler(@PathVariable int hotelId, @PathVariable int hotelRoomId){
        if(hotelRoomService.checkHotelExisting(hotelId)){
            throw new ResourceNotFoundException("No hotel with id: " + hotelId);
        }

        return hotelRoomService.getHotelRoomById(hotelId, hotelRoomId).orElseThrow(
                ()-> new ResourceNotFoundException("No hotelRoom with id: " + hotelRoomId));
    }

    @PutMapping("{hotelRoomId}")
    public Optional<HotelRoom> updateHotelRoomHandler(@PathVariable int hotelId, @PathVariable int hotelRoomId, @RequestBody HotelRoomDTO updatedHotel, HttpSession session) {
        if (session.getAttribute("userId") == null) {
            throw new UnauthenticatedException("User is not authenticated");
        }

        if (session.getAttribute("role") != Role.OWNER) {
            throw new ForbiddenActionException("You must be an owner to modify this hotel");
        }

        if(hotelRoomService.checkHotelExisting(hotelId)){
            throw new ResourceNotFoundException("No hotel with id: " + hotelId);
        }

        if (hotelRoomService.isUserOwnerOfHotel((int) session.getAttribute("userId"), hotelId)){
            throw new ForbiddenActionException("You must be the owner of this hotel to make updates");
        }

        if(hotelRoomService.checkHotelRoomExisting(hotelRoomId)){
            throw new ResourceNotFoundException("No hotel Room with id: " + hotelRoomId);
        }

        // there are related?
        if(hotelRoomService.checkHotelRelationWithRoom(hotelId,hotelRoomId)){
            throw new ResourceNotFoundException("No hotel Room with id: " + hotelRoomId + " is related to hotel with id: "+hotelId);
        }

        return hotelRoomService.updateHotelRoom(hotelId,hotelRoomId, updatedHotel);
    }

    @DeleteMapping("{hotelRoomId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteHotelRoomHandler(@PathVariable int hotelId, @PathVariable int hotelRoomId, HttpSession session) {
        if (session.getAttribute("userId") == null) {
            throw new UnauthenticatedException("User is not authenticated");
        }

        if (session.getAttribute("role") != Role.OWNER) {
            throw new ForbiddenActionException("You must be an owner to delete this hotel");
        }

        if(hotelRoomService.checkHotelExisting(hotelId)){
            throw new ResourceNotFoundException("No hotel with id: " + hotelId);
        }

        if (hotelRoomService.isUserOwnerOfHotel((int) session.getAttribute("userId"), hotelId)){
            throw new ForbiddenActionException("You must be the owner of this hotel to delete room s");
        }

        if(hotelRoomService.checkHotelRoomExisting(hotelRoomId)){
            throw new ResourceNotFoundException("No hotel Room with id: " + hotelRoomId);
        }

        // there are related?
        if(hotelRoomService.checkHotelRelationWithRoom(hotelId,hotelRoomId)){
            throw new ResourceNotFoundException("No hotel Room with id: " + hotelRoomId + " is related to hotel with id: "+hotelId);
        }

        hotelRoomService.deleteHotelRoom(hotelRoomId);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> resourceNotFoundHandler(ResourceNotFoundException e){
        return Map.of(
                "error", e.getMessage()
        );
    }

}
