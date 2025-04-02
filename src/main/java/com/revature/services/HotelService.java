package com.revature.services;


import com.revature.dto.request.HotelFilterDTO;
import com.revature.models.Hotel;
import com.revature.repos.HotelDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HotelService {

    private final HotelDAO hotelDAO;

    @Autowired
    public HotelService(HotelDAO hotelDAO) {
        this.hotelDAO = hotelDAO;
    }

    // Availability
    public boolean checkHotelExisting(int hotelId){
        return hotelDAO.findById(hotelId).isEmpty();
    }

    // CREATE
    public Hotel createHotel(Hotel hotelTobeCreated){
        return hotelDAO.save(hotelTobeCreated);
    }

    // RETRIEVE ALL
    public List<Hotel> getAllHotels(){
        return hotelDAO.findAll();
    }

    // RETRIEVE BY ID

    public Optional<Hotel> getHotelById(int hotelId){
        return hotelDAO.findById(hotelId);
    }

    // filters
    public List<Hotel> filterHotels(HotelFilterDTO filter) {
        if (filter.getName() == null && filter.getCountry() == null && filter.getState() == null && filter.getCity() == null) {
            return hotelDAO.findAll();
        } else {
            return hotelDAO.findHotelsByFilter(filter.getName(), filter.getCountry(), filter.getState(), filter.getCity());
        }
    }

    // PUT
    public Optional<Hotel> updateHotel(int hotelId, Hotel updatedHotel) {
        return hotelDAO.findById(hotelId).map(hotel -> {
            hotel.setName(updatedHotel.getName());
            hotel.setCountry(updatedHotel.getCountry());
            hotel.setState(updatedHotel.getState());
            hotel.setCity(updatedHotel.getCity());
            hotel.setStreet(updatedHotel.getStreet());
            hotel.setHouseNumber(updatedHotel.getHouseNumber());
            hotel.setPostalCode(updatedHotel.getPostalCode());
            return hotelDAO.save(hotel);
        });
    }

    // DELETE
    public void deleteHotel(int hotelId) {
         hotelDAO.deleteById(hotelId);
    }
}
