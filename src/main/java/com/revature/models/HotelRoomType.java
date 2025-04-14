package com.revature.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "hotel_room_types")
public class HotelRoomType {
//    hotel_room_type_id serial primary key,
//    hotel_id int references hotels,
//    name varchar(50) not null,
//    max_guest int not null check (max_guest > 0,
//    price numeric (10,2) not null check (price > 0)

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int hotelRoomTypeId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, columnDefinition = "INT CHECK (max_guest > 0)")
    private int maxGuest;

    @Column(nullable = false, columnDefinition = "NUMERIC(10, 2) CHECK (price > 0)")
    private double price; // PER NIGHT

    @JsonBackReference("hotel-room-types")
    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

    @JsonManagedReference("room-type-rooms")
    @OneToMany(mappedBy = "roomType", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HotelRoom> rooms;

    public HotelRoomType(){}

    public HotelRoomType(int hotelRoomTypeId, String name, int maxGuest, Hotel hotel, double price) {
        this.hotelRoomTypeId = hotelRoomTypeId;
        this.name = name;
        this.maxGuest = maxGuest;
        this.hotel = hotel;
        this.price = price;
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

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }
}
