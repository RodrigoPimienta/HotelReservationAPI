package com.revature.controller;


import com.revature.dto.request.HotelFilterDTO;
import com.revature.dto.request.RoomFilterDTO;
import com.revature.dto.response.RoomTypeWithDetailsDTO;
import com.revature.dto.response.RoomWithDetailsDTO;
import com.revature.exceptions.ForbiddenActionException;
import com.revature.exceptions.ResourceNotFoundException;
import com.revature.models.Hotel;
import com.revature.models.Role;
import com.revature.security.CustomUserDetails;
import com.revature.services.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("hotels")
public class HotelController {

    private final HotelService hotelService;

    @Autowired
    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @PreAuthorize("hasRole('OWNER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Hotel createHotelHandler(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody Hotel hotel){
        return hotelService.createHotel(userDetails.getUserId(), hotel);
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
    public List<Hotel> filterHotels(@AuthenticationPrincipal CustomUserDetails userDetails,  @RequestBody HotelFilterDTO filter) {
        if (Objects.equals(userDetails.getAuthorities().toString(), Role.OWNER.toString())) {
            filter.setOwner(userDetails.getUserId());
        }else{
            filter.setOwner(null);
        }

        return hotelService.filterHotels(filter);
    }

    @PostMapping("/rooms/filter")
    public List<RoomWithDetailsDTO> filterRoomsHandler(@AuthenticationPrincipal CustomUserDetails userDetails ,@RequestBody RoomFilterDTO roomFilterDTO) throws ParseException {
        if (Objects.equals(userDetails.getAuthorities().toString(), Role.OWNER.toString())) {
            roomFilterDTO.setOwner(true);
        }

        return hotelService.getRoomsByFilters(roomFilterDTO);
    }

    @PostMapping("/roomTypes/filter")
    public List<RoomTypeWithDetailsDTO> filterRoomTypesHandler(@AuthenticationPrincipal CustomUserDetails userDetails,@RequestBody RoomFilterDTO roomFilterDTO) throws ParseException {
        if (Objects.equals(userDetails.getAuthorities().toString(), Role.OWNER.toString())) {
            roomFilterDTO.setOwner(true);
        }

        return hotelService.getRoomsTypeByFilters(roomFilterDTO);
    }

    @PutMapping("{hotelId}")
    public Optional<Hotel> updateHotelHandler(@AuthenticationPrincipal CustomUserDetails userDetails,@PathVariable int hotelId, @RequestBody Hotel updatedHotel) {
        if (Objects.equals(userDetails.getAuthorities().toString(), Role.USER.toString())) {
            throw new ForbiddenActionException("You must be an owner to modify this hotel");
        }

        if(hotelService.checkHotelExisting(hotelId)){
            throw new ResourceNotFoundException("No hotel with id: " + hotelId);
        }

        if (!hotelService.isUserOwnerOfHotel(userDetails.getUserId(),hotelId)){
            throw new ForbiddenActionException("You must be the owner of this hotel to make updates");
        }

        return hotelService.updateHotel(hotelId, updatedHotel);
    }

    @PreAuthorize("hasRole('OWNER')")
    @DeleteMapping("{hotelId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteHotelHandler(@AuthenticationPrincipal CustomUserDetails userDetails,@PathVariable int hotelId) {

        if(hotelService.checkHotelExisting(hotelId)){
            throw new ResourceNotFoundException("No hotel with id: " + hotelId);
        }

        if (!hotelService.isUserOwnerOfHotel(userDetails.getUserId()  ,hotelId)){
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
