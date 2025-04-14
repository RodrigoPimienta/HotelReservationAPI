package com.revature.repos;

import com.revature.models.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelDAO extends JpaRepository<Hotel, Integer> {

    @Query("SELECT h FROM Hotel h WHERE " +
            "(:name is null or h.name ILIKE %:name%) AND " +
            "(:country is null or h.country ILIKE %:country%) AND " +
            "(:state is null or h.state ILIKE %:state%) AND " +
            "(:city is null or h.city ILIKE %:city%) AND"+
            "(:userId is null OR EXISTS (" +
            "   SELECT u FROM User u JOIN u.hotels h2 WHERE u.userId = :userId AND h2.hotelId = h.hotelId" +
            "))")
    List<Hotel> findHotelsByFilter(
            @Param("name") String name,
            @Param("country") String country,
            @Param("state") String state,
            @Param("city") String city,
            @Param("userId") Integer owner
    );

}
