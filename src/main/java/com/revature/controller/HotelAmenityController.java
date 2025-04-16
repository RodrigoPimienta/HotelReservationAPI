package com.revature.controller;

import com.revature.exceptions.ForbiddenActionException;
import com.revature.exceptions.ResourceNotFoundException;
import com.revature.exceptions.UnauthenticatedException;
import com.revature.models.HotelAmenity;
import com.revature.models.Role;
import com.revature.security.CustomUserDetails;
import com.revature.services.HotelAmenityService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("hotels/{hotelId}/amenities")
public class HotelAmenityController {

    private final HotelAmenityService hotelAmenityService;

    @Autowired
    public HotelAmenityController(HotelAmenityService hotelAmenityService) {
        this.hotelAmenityService = hotelAmenityService;
    }

    @PreAuthorize("hasRole('OWNER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public List<HotelAmenity> createAmenityHandler(@AuthenticationPrincipal CustomUserDetails userDetails,@PathVariable int hotelId, @RequestBody List<HotelAmenity> amenities, HttpSession session) {
        if (hotelAmenityService.isUserOwnerOfHotel(userDetails.getUserId(), hotelId)){
            throw new ForbiddenActionException("You must be the owner of this hotel to add amenities");
        }

        return hotelAmenityService.createHotelAmenity(hotelId, amenities);
    }


    @GetMapping
    public List<HotelAmenity> getAmenityHandler(@PathVariable int hotelId) {

        if(hotelAmenityService.checkHotelExisting(hotelId)){
            throw new ResourceNotFoundException("No hotel with id: " + hotelId);
        }

        return hotelAmenityService.getHotelAmenitiesByHotel(hotelId);
    }

    @GetMapping("{hotelAmenityId}")
    public HotelAmenity getHotelHandler(@PathVariable int hotelId, @PathVariable int hotelAmenityId){
        if(hotelAmenityService.checkHotelExisting(hotelId)){
            throw new ResourceNotFoundException("No hotel with id: " + hotelId);
        }

        return hotelAmenityService.getHotelAmenityById(hotelId, hotelAmenityId).orElseThrow(
                ()-> new ResourceNotFoundException("No hotel amenity with id: " + hotelAmenityId));
    }

    @PreAuthorize("hasRole('OWNER')")
    @PutMapping("{hotelAmenityId}")
    public Optional<HotelAmenity> updateHotelHandler(@AuthenticationPrincipal CustomUserDetails userDetails,@PathVariable int hotelId, @PathVariable int hotelAmenityId, @RequestBody HotelAmenity updatedHotel, HttpSession session) {
        if(hotelAmenityService.checkHotelExisting(hotelId)){
            throw new ResourceNotFoundException("No hotel with id: " + hotelId);
        }

        if (hotelAmenityService.isUserOwnerOfHotel(userDetails.getUserId(), hotelId)){
            throw new ForbiddenActionException("You must be the owner of this hotel to make updates");
        }

        if(hotelAmenityService.checkHotelAmenityExisting(hotelAmenityId)){
            throw new ResourceNotFoundException("No hotel amenity with id: " + hotelAmenityId);
        }

        // there are related?
        if(hotelAmenityService.checkHotelRelationWithAmenity(hotelId,hotelAmenityId)){
            throw new ResourceNotFoundException("No hotel amenity with id: " + hotelAmenityId + " is related to hotel with id: "+hotelId);
        }

        return hotelAmenityService.updateHotelAmenity(hotelId,hotelAmenityId, updatedHotel);
    }

    @PreAuthorize("hasRole('OWNER')")
    @DeleteMapping("{hotelAmenityId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteHotelHandler(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable int hotelId, @PathVariable int hotelAmenityId, HttpSession session) {
        if(hotelAmenityService.checkHotelExisting(hotelId)){
            throw new ResourceNotFoundException("No hotel with id: " + hotelId);
        }

        if (hotelAmenityService.isUserOwnerOfHotel(userDetails.getUserId(), hotelId)){
            throw new ForbiddenActionException("You must be the owner fo this hotel delete amenities");
        }

        if(hotelAmenityService.checkHotelAmenityExisting(hotelAmenityId)){
            throw new ResourceNotFoundException("No hotel amenity with id: " + hotelAmenityId);
        }

        // there are related?
        if(hotelAmenityService.checkHotelRelationWithAmenity(hotelId,hotelAmenityId)){
            throw new ResourceNotFoundException("No hotel amenity with id: " + hotelAmenityId + " is related to hotel with id: "+hotelId);
        }

        hotelAmenityService.deleteHotelAmenity(hotelAmenityId);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> resourceNotFoundHandler(ResourceNotFoundException e){
        return Map.of(
                "error", e.getMessage()
        );
    }
}