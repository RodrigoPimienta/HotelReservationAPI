package com.revature.repos;

import com.revature.models.HotelRoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HotelRoomTypeDAO extends JpaRepository<HotelRoomType, Integer> {
    @Query("SELECT hrt FROM HotelRoomType hrt WHERE hrt.hotel.hotelId = :hotelId")
    List<HotelRoomType> findByHotelId(@Param("hotelId") int hotelId);

    Optional<HotelRoomType> findByHotelRoomTypeIdAndHotel_HotelId(int hotelRoomTypeId, int hotelId); // Updated method signature

}
