package com.revature.controller;

import com.revature.dto.response.RoomTypeDTO;
import com.revature.exceptions.ForbiddenActionException;
import com.revature.exceptions.ResourceNotFoundException;
import com.revature.exceptions.UnauthenticatedException;

import com.revature.models.HotelRoomType;
import com.revature.models.Role;

import com.revature.services.HotelRoomTypeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("hotels/{hotelId}/roomTypes")
public class HotelRoomTypesController {

    private final HotelRoomTypeService hotelRoomTypeService;

    @Autowired
    public HotelRoomTypesController(HotelRoomTypeService hotelRoomTypeService) {
        this.hotelRoomTypeService = hotelRoomTypeService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public List<HotelRoomType> createRoomTypeHandler(@PathVariable int hotelId, @RequestBody List<HotelRoomType> RoomTypes, HttpSession session) {

        if (session.getAttribute("userId") == null){
            throw new UnauthenticatedException("User is not authenticated");
        }

        // I need to make sure the role of the user is a teacher
        if (session.getAttribute("role") != Role.OWNER){
            throw new ForbiddenActionException("You must be a teacher to access this");
        }

        if (hotelRoomTypeService.isUserOwnerOfHotel((int) session.getAttribute("userId"), hotelId)){
            throw new ForbiddenActionException("You must be the owner of this hotel to add room types");
        }

        return hotelRoomTypeService.createHotelRoomType(hotelId, RoomTypes);
    }


    @GetMapping
    public List<RoomTypeDTO> getRoomTypesHandler(@PathVariable int hotelId) {

        if(hotelRoomTypeService.checkHotelExisting(hotelId)){
            throw new ResourceNotFoundException("No hotel with id: " + hotelId);
        }

        return hotelRoomTypeService.getHotelRoomTypesByHotel(hotelId);
    }

    @GetMapping("{hotelRoomTypeId}")
    public HotelRoomType getHotelRoomTypeHandler(@PathVariable int hotelId, @PathVariable int hotelRoomTypeId){
        if(hotelRoomTypeService.checkHotelExisting(hotelId)){
            throw new ResourceNotFoundException("No hotel with id: " + hotelId);
        }

        return hotelRoomTypeService.getHotelRoomTypeById(hotelId, hotelRoomTypeId).orElseThrow(
                ()-> new ResourceNotFoundException("No hotelRoomType with id: " + hotelRoomTypeId));
    }

    @PutMapping("{hotelRoomTypeId}")
    public Optional<HotelRoomType> updateHotelRoomTypeHandler(@PathVariable int hotelId, @PathVariable int hotelRoomTypeId, @RequestBody HotelRoomType updatedHotel, HttpSession session) {
        if (session.getAttribute("userId") == null) {
            throw new UnauthenticatedException("User is not authenticated");
        }

        if (session.getAttribute("role") != Role.OWNER) {
            throw new ForbiddenActionException("You must be an owner to modify this hotel");
        }

        if(hotelRoomTypeService.checkHotelExisting(hotelId)){
            throw new ResourceNotFoundException("No hotel with id: " + hotelId);
        }

        if (hotelRoomTypeService.isUserOwnerOfHotel((int) session.getAttribute("userId"), hotelId)){
            throw new ForbiddenActionException("You must be the owner of this hotel to make updates");
        }

        if(hotelRoomTypeService.checkHotelRoomTypeExisting(hotelRoomTypeId)){
            throw new ResourceNotFoundException("No hotel RoomType with id: " + hotelRoomTypeId);
        }

        // there are related?
        if(hotelRoomTypeService.checkHotelRelationWithRoomType(hotelId,hotelRoomTypeId)){
            throw new ResourceNotFoundException("No hotel RoomType with id: " + hotelRoomTypeId + " is related to hotel with id: "+hotelId);
        }

        return hotelRoomTypeService.updateHotelRoomType(hotelId,hotelRoomTypeId, updatedHotel);
    }

    @DeleteMapping("{hotelRoomTypeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteHotelRoomTypeHandler(@PathVariable int hotelId, @PathVariable int hotelRoomTypeId, HttpSession session) {
        if (session.getAttribute("userId") == null) {
            throw new UnauthenticatedException("User is not authenticated");
        }

        if (session.getAttribute("role") != Role.OWNER) {
            throw new ForbiddenActionException("You must be an owner to delete this hotel");
        }

        if(hotelRoomTypeService.checkHotelExisting(hotelId)){
            throw new ResourceNotFoundException("No hotel with id: " + hotelId);
        }

        if (hotelRoomTypeService.isUserOwnerOfHotel((int) session.getAttribute("userId"), hotelId)){
            throw new ForbiddenActionException("You must be the owner of this hotel to delete room types");
        }

        if(hotelRoomTypeService.checkHotelRoomTypeExisting(hotelRoomTypeId)){
            throw new ResourceNotFoundException("No hotel RoomType with id: " + hotelRoomTypeId);
        }

        // there are related?
        if(hotelRoomTypeService.checkHotelRelationWithRoomType(hotelId,hotelRoomTypeId)){
            throw new ResourceNotFoundException("No hotel RoomType with id: " + hotelRoomTypeId + " is related to hotel with id: "+hotelId);
        }

        hotelRoomTypeService.deleteHotelRoomType(hotelRoomTypeId);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> resourceNotFoundHandler(ResourceNotFoundException e){
        return Map.of(
                "error", e.getMessage()
        );
    }

}
