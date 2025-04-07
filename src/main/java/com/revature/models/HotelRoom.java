package com.revature.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "hotel_rooms")
public class HotelRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int hotelRoomId;

    @Column(nullable = false)
    private int num;

    @JsonBackReference("room-type-rooms")
    @ManyToOne
    @JoinColumn(name = "hotel_room_type_id")
    private HotelRoomType roomType;

    @JsonBackReference("hotel-rooms")
    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

    @OneToMany(mappedBy = "room")
    @JsonManagedReference("room-reservations")
    private List<Reservation> reservations;

    public HotelRoom(){}

    public HotelRoom(int hotelRoomId, int num, HotelRoomType roomType) {
        this.hotelRoomId = hotelRoomId;
        this.num = num;
        this.roomType = roomType;
    }

    public int getHotelRoomId() {
        return hotelRoomId;
    }

    public void setHotelRoomId(int hotelRoomId) {
        this.hotelRoomId = hotelRoomId;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public HotelRoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(HotelRoomType roomType) {
        this.roomType = roomType;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

}
