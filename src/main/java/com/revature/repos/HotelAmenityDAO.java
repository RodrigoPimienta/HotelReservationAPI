package com.revature.repos;

import com.revature.models.HotelAmenity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HotelAmenityDAO extends JpaRepository<HotelAmenity, Integer> {
    @Query("SELECT ha FROM HotelAmenity ha WHERE ha.hotel.hotelId = :hotelId")
    List<HotelAmenity> findByHotelId(@Param("hotelId") int hotelId);

    Optional<HotelAmenity> findByHotelAmenityIdAndHotel_HotelId(int hotelAmenityId, int hotelId);
}
