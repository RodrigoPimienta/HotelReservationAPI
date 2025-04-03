package com.revature.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

@Entity
@Table(name = "hotel_rooms")
public class HotelRoom {
//    hotel_room_id serial primary key,
//    hotel_id int references hotels,
//    hotel_room_type_id references hotel_room_types,
//    num int unique not null,
//    status varchar(50) check ( status in ('AVAILABLE', 'UNAVAILABLE','MAINTEINES'))

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
