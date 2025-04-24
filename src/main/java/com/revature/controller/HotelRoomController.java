package com.revature.controller;

import com.revature.dto.request.HotelRoomDTO;
import com.revature.dto.response.HotelRoomWithRoomTypeDTO;
import com.revature.exceptions.ForbiddenActionException;
import com.revature.exceptions.ResourceNotFoundException;

import com.revature.models.HotelRoom;

import com.revature.security.CustomUserDetails;
import com.revature.services.HotelRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @PreAuthorize("hasRole('OWNER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public List<HotelRoom> createRoomHandler(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable int hotelId, @RequestBody List<HotelRoomDTO> Rooms) {

        if (hotelRoomService.isUserOwnerOfHotel(userDetails.getUserId(), hotelId)){
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

    @PreAuthorize("hasRole('OWNER')")
    @PutMapping("{hotelRoomId}")
    public Optional<HotelRoom> updateHotelRoomHandler(@AuthenticationPrincipal CustomUserDetails userDetails,@PathVariable int hotelId, @PathVariable int hotelRoomId, @RequestBody HotelRoomDTO updatedHotel) {
        if(hotelRoomService.checkHotelExisting(hotelId)){
            throw new ResourceNotFoundException("No hotel with id: " + hotelId);
        }

        if (hotelRoomService.isUserOwnerOfHotel(userDetails.getUserId(), hotelId)){
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

    @PreAuthorize("hasRole('OWNER')")
    @DeleteMapping("{hotelRoomId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteHotelRoomHandler(@AuthenticationPrincipal CustomUserDetails userDetails,@PathVariable int hotelId, @PathVariable int hotelRoomId) {

        if(hotelRoomService.checkHotelExisting(hotelId)){
            throw new ResourceNotFoundException("No hotel with id: " + hotelId);
        }

        if (hotelRoomService.isUserOwnerOfHotel(userDetails.getUserId(), hotelId)){
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
