package com.revature.dto.response;

import com.revature.models.HotelRoomType;

public class RoomTypeDTO {
    private int hotelRoomTypeId;
    private String name;
    private int maxGuest;
    private double price;

    public RoomTypeDTO() {}

    public RoomTypeDTO(HotelRoomType roomType) {
        this.hotelRoomTypeId = roomType.getHotelRoomTypeId();
        this.name = roomType.getName();
        this.maxGuest = roomType.getMaxGuest();
        this.price = roomType.getPrice();
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

    public int getMaxGuest() {
        return maxGuest;
    }

    public void setMaxGuest(int maxGuest) {
        this.maxGuest = maxGuest;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
