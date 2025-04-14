package com.revature.dto.response;

import com.revature.models.HotelRoomType;

public class RoomTypeWithDetailsDTO {
    private int hotelRoomTypeId;
    private String name;
    private int maxGuest;
    private double price;
    private HotelWithDetailsDTO hotel;
    private int numberRooms;

    public RoomTypeWithDetailsDTO(HotelRoomType roomType) {
        this.hotelRoomTypeId=roomType.getHotelRoomTypeId();
        this.name=roomType.getName();
        this.maxGuest=roomType.getMaxGuest();
        this.price=roomType.getPrice();
        this.hotel= new HotelWithDetailsDTO(roomType.getHotel());
        this.numberRooms=1;
    }

    public int getHotelRoomTypeId() {
        return hotelRoomTypeId;
    }

    public void setHotelRoomTypeId(int hotelRoomTypeId) {
        this.hotelRoomTypeId = hotelRoomTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getMaxGuest() {
        return maxGuest;
    }

    public void setMaxGuest(int maxGuest) {
        this.maxGuest = maxGuest;
    }

    public HotelWithDetailsDTO getHotel() {
        return hotel;
    }

    public void setHotel(HotelWithDetailsDTO hotel) {
        this.hotel = hotel;
    }

    public int getNumberRooms() {
        return numberRooms;
    }

    public void setNumberRooms(int numberRooms) {
        this.numberRooms = numberRooms;
    }
}
