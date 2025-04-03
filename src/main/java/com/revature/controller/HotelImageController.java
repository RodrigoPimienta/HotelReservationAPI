package com.revature.controller;

import com.revature.exceptions.ForbiddenActionException;
import com.revature.exceptions.ResourceNotFoundException;
import com.revature.exceptions.UnauthenticatedException;
import com.revature.models.HotelImage;
import com.revature.models.Role;
import com.revature.services.HotelImageService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("hotels/{hotelId}/images")
public class HotelImageController {

    private final HotelImageService hotelImageService;

    @Autowired
    public HotelImageController(HotelImageService hotelImageService) {
        this.hotelImageService = hotelImageService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public List<HotelImage> createImageHandler(@PathVariable int hotelId, @RequestBody List<HotelImage> images, HttpSession session) {

        if (session.getAttribute("userId") == null){
            throw new UnauthenticatedException("User is not authenticated");
        }

        // I need to make sure the role of the user is a teacher
        if (session.getAttribute("role") != Role.OWNER){
            throw new ForbiddenActionException("You must be a teacher to access this");
        }

        if (hotelImageService.isUserOwnerOfHotel((int) session.getAttribute("userId"), hotelId)){
            throw new ForbiddenActionException("You must be the owner of this hotel to add images");
        }

        return hotelImageService.createHotelImages(hotelId, images);
    }


    @GetMapping
    public List<HotelImage> getImagesHandler(@PathVariable int hotelId) {

        if(hotelImageService.checkHotelExisting(hotelId)){
            throw new ResourceNotFoundException("No hotel with id: " + hotelId);
        }

        return hotelImageService.getHotelImagesByHotel(hotelId);
    }

    @GetMapping("{hotelImageId}")
    public HotelImage getHotelHandler(@PathVariable int hotelId, @PathVariable int hotelImageId){
        if(hotelImageService.checkHotelExisting(hotelId)){
            throw new ResourceNotFoundException("No hotel with id: " + hotelId);
        }

        return hotelImageService.getHotelImageById(hotelId, hotelImageId).orElseThrow(
                ()-> new ResourceNotFoundException("No hotelImage with id: " + hotelImageId));
    }

    @PutMapping("{hotelImageId}")
    public Optional<HotelImage> updateHotelHandler(@PathVariable int hotelId,@PathVariable int hotelImageId, @RequestBody HotelImage updatedHotel, HttpSession session) {
        if (session.getAttribute("userId") == null) {
            throw new UnauthenticatedException("User is not authenticated");
        }

        if (session.getAttribute("role") != Role.OWNER) {
            throw new ForbiddenActionException("You must be an owner to modify this hotel");
        }

        if(hotelImageService.checkHotelExisting(hotelId)){
            throw new ResourceNotFoundException("No hotel with id: " + hotelId);
        }

        if (hotelImageService.isUserOwnerOfHotel((int) session.getAttribute("userId"), hotelId)){
            throw new ForbiddenActionException("You must be the owner of this hotel to make updates");
        }

        if(hotelImageService.checkHotelImageExisting(hotelImageId)){
            throw new ResourceNotFoundException("No hotel image with id: " + hotelImageId);
        }

        // there are related?
        if(hotelImageService.checkHotelRelationWithImage(hotelId,hotelImageId)){
            throw new ResourceNotFoundException("No hotel image with id: " + hotelImageId + " is related to hotel with id: "+hotelId);
        }

        return hotelImageService.updateHotelImage(hotelId,hotelImageId, updatedHotel);
    }

    @DeleteMapping("{hotelImageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteHotelImageHandler(@PathVariable int hotelId, @PathVariable int hotelImageId, HttpSession session) {
        if (session.getAttribute("userId") == null) {
            throw new UnauthenticatedException("User is not authenticated");
        }

        if (session.getAttribute("role") != Role.OWNER) {
            throw new ForbiddenActionException("You must be an owner to delete this hotel");
        }

        if(hotelImageService.checkHotelExisting(hotelId)){
            throw new ResourceNotFoundException("No hotel with id: " + hotelId);
        }

        if (hotelImageService.isUserOwnerOfHotel((int) session.getAttribute("userId"), hotelId)){
            throw new ForbiddenActionException("You must be the owner of the hotel to delete images");
        }

        if(hotelImageService.checkHotelImageExisting(hotelImageId)){
            throw new ResourceNotFoundException("No hotel image with id: " + hotelImageId);
        }

        // there are related?
        if(hotelImageService.checkHotelRelationWithImage(hotelId,hotelImageId)){
            throw new ResourceNotFoundException("No hotel image with id: " + hotelImageId + " is related to hotel with id: "+hotelId);
        }

        hotelImageService.deleteHotelImage(hotelImageId);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> resourceNotFoundHandler(ResourceNotFoundException e){
        return Map.of(
                "error", e.getMessage()
        );
    }

}