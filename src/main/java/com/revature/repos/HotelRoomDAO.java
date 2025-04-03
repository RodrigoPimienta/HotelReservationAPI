package com.revature.repos;

import com.revature.models.HotelRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HotelRoomDAO extends JpaRepository<HotelRoom, Integer> {
    @Query("SELECT hr FROM HotelRoom hr WHERE hr.hotel.hotelId = :hotelId")
    List<HotelRoom> findByHotelId(@Param("hotelId") int hotelId);

    Optional<HotelRoom> findByHotelRoomIdAndHotel_HotelId(int hotelRoomId, int hotelId); // Updated method sign
}
