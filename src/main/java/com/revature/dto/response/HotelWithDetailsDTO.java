package com.revature.dto.response;

import com.revature.models.Hotel;
import com.revature.models.HotelAmenity;
import com.revature.models.HotelImage;

import java.util.List;

public class HotelWithDetailsDTO {

    private int hotelId;

    private String name;

    private String country;

    private String state;

    private String city;

    private String street;

    private String houseNumber;

    private String postalCode;

    private List<HotelImage> images;

    private List<HotelAmenity> amenities;
    public HotelWithDetailsDTO(Hotel hotel){
        this.hotelId=hotel.getHotelId();
        this.name=hotel.getName();
        this.country=hotel.getCountry();
        this.state=hotel.getState();
        this.city=hotel.getCity();
        this.street=hotel.getStreet();
        this.houseNumber=hotel.getHouseNumber();
        this.postalCode=hotel.getPostalCode();
        this.images=hotel.getImages();
        this.amenities=hotel.getAmenities();
    }

    public int getHotelId() {
        return hotelId;
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public List<HotelImage> getImages() {
        return images;
    }

    public void setImages(List<HotelImage> images) {
        this.images = images;
    }

    public List<HotelAmenity> getAmenities() {
        return amenities;
    }

    public void setAmenities(List<HotelAmenity> amenities) {
        this.amenities = amenities;
    }
}
