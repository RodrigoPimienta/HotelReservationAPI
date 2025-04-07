package com.revature.repos;

import com.revature.models.HotelRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface HotelRoomDAO extends JpaRepository<HotelRoom, Integer> {
    @Query("SELECT hr FROM HotelRoom hr WHERE hr.hotel.hotelId = :hotelId")
    List<HotelRoom> findByHotelId(@Param("hotelId") int hotelId);

    Optional<HotelRoom> findByHotelRoomIdAndHotel_HotelId(int hotelRoomId, int hotelId); // Updated method sign

    @Query("SELECT hr FROM HotelRoom hr " +
            "JOIN FETCH hr.roomType rt " +
            "JOIN FETCH hr.hotel h " +
            "WHERE (:hotelId IS NULL OR h.hotelId = :hotelId) " +
            "AND (:country IS NULL OR h.Country = :country) " +
            "AND (:state IS NULL OR h.State = :state) " +
            "AND (:city IS NULL OR h.City = :city) " +
            "AND (:roomId IS NULL OR hr.hotelRoomId = :roomId) " +
            "AND (:roomTypeId IS NULL OR rt.hotelRoomTypeId = :roomTypeId) " +
            "AND (:guests IS NULL OR rt.maxGuest >= :guests) " +
            "AND (:price IS NULL OR rt.price <= :price) " +
            "AND NOT EXISTS (" +
            "   SELECT 1 FROM Reservation r " +
            "   WHERE r.room.hotelRoomId = hr.hotelRoomId " +
            "   AND r.status IN ('ACCEPT', 'PENDING') " +
            "   AND NOT (r.checkOut <= :checkIn OR r.checkIn >= :checkOut)" +
            ")")
    List<HotelRoom> findAvailableRooms(
            @Param("hotelId") Integer hotelId,
            @Param("country") String country,
            @Param("state") String state,
            @Param("city") String city,
            @Param("roomId") Integer roomId,
            @Param("roomTypeId") Integer roomTypeId,
            @Param("checkIn") Date checkIn,
            @Param("checkOut") Date checkOut,
            @Param("guests") Integer guests,
            @Param("price") Double price
    );

}

