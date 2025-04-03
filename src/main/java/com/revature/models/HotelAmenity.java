package com.revature.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "hotel_amenities")
public class HotelAmenity {

//    amenity_id serial primary key,
//    hotel_id int references hotels,
//    name varchar(40) not null,
//    description varchar(255) not null

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int hotelAmenityId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String url;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

    public HotelAmenity(){}

    public HotelAmenity(String name, String description, String url, Hotel hotel) {
        this.name = name;
        this.description = description;
        this.url=url;
        this.hotel = hotel;
    }

    public int getHotelAmenityId() {
        return hotelAmenityId;
    }

    public void setHotelAmenityId(int hotelAmenityId) {
        this.hotelAmenityId = hotelAmenityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }
}
