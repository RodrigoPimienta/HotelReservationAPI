package com.revature.dto.request;

public class ReservationCreateDTO {

//    Make hotel reservations by selecting check-in and check-out dates, room type,
//    and the number of guests. * (Aaron)

    private int hotelId;


    private String checkIn;

    private String checkOut;

    private int roomTypeId;

    private int guestNumber;

    public ReservationCreateDTO(){}

    public ReservationCreateDTO(int hotelId, String checkIn, String checkOut, int roomTypeId, int guestNumber) {
        this.hotelId = hotelId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.roomTypeId = roomTypeId;
        this.guestNumber = guestNumber;
    }

    public int getHotelId() {
        return hotelId;
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }

    public String getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(String checkIn) {
        this.checkIn = checkIn;
    }

    public String getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(String checkOut) {
        this.checkOut = checkOut;
    }

    public int getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(int roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    public int getGuestNumber() {
        return guestNumber;
    }

    public void setGuestNumber(int guestNumber) {
        this.guestNumber = guestNumber;
    }
}
