package com.revature.dto.request;

import com.revature.models.ReservationStatus;

import java.time.OffsetDateTime;
import java.util.Optional;

public class ReservationFilterDTO {
    private int reservationId;
    private int hotelId;
    private String country;
    private String state;
    private String city;
    private int roomTypeId;
    private int roomId;
    private String checkIn;
    private String checkOut;
    private ReservationStatus status;
    private boolean owner;

    public ReservationFilterDTO(){}

    public ReservationFilterDTO(int reservationId, int hotelId, String country, String state, String city, int roomTypeId, int roomId, String checkIn, String checkOut, ReservationStatus status, boolean owner) {
        this.reservationId = reservationId;
        this.hotelId = hotelId;
        this.country = country;
        this.state = state;
        this.city = city;
        this.roomTypeId = roomTypeId;
        this.roomId = roomId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.status = status;
        this.owner = owner;
    }

    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public int getHotelId() {
        return hotelId;
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
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

    public int getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(int roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
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


    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public boolean isOwner() {
        return owner;
    }

    public void setOwner(boolean owner) {
        this.owner = owner;
    }
}
