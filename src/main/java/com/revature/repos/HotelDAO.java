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
            "(:country is null or h.Country ILIKE :country) AND " +
            "(:state is null or h.State ILIKE :state) AND " +
            "(:city is null or h.City ILIKE :city)")
    List<Hotel> findHotelsByFilter(
            @Param("name") String name,
            @Param("country") String country,
            @Param("state") String state,
            @Param("city") String city
    );

}
