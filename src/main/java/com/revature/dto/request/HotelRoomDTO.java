package com.revature.dto.request;

public class HotelRoomDTO {
    private int num;
    private int hotelRoomTypeId;

    public HotelRoomDTO(){}

    public HotelRoomDTO(int num, int hotelRoomTypeId){
        this.num = num;
        this.hotelRoomTypeId = hotelRoomTypeId;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getHotelRoomTypeId() {
        return hotelRoomTypeId;
    }

    public void setHotelRoomTypeId(int hotelRoomTypeId) {
        this.hotelRoomTypeId = hotelRoomTypeId;
    }
}