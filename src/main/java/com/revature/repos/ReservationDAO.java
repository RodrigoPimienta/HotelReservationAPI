package com.revature.repos;

import com.revature.models.Reservation;
import com.revature.models.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationDAO extends JpaRepository<Reservation, Integer> {

    List<Reservation> findByUser_UserId(int userId);

    @Query("SELECT r FROM Reservation r WHERE " +
            "(:reservationId IS NULL OR r.reservationId = :reservationId) AND " +
            "(:userId IS NULL  OR r.user.userId = :userId OR :owner = true) AND " +
            "(:hotelId IS NULL OR r.room.hotel.hotelId = :hotelId) AND " +
            "(:roomTypeId IS NULL OR r.room.roomType.hotelRoomTypeId = :roomTypeId) AND " +
            "(:roomId IS NULL OR r.room.hotelRoomId = :roomId) AND " +
            "(:status IS NULL OR r.status = :status) AND " +
            "(:owner = false OR EXISTS (" +
            "   SELECT u FROM User u JOIN u.hotels h WHERE u.userId = :userId AND h.hotelId = r.room.hotel.hotelId" +
            "))")
    List<Reservation> findReservationsWithFilters(
            @Param("reservationId") Integer reservationId,
            @Param("userId") Integer userId,
            @Param("hotelId") Integer hotelId,
            @Param("roomTypeId") Integer roomTypeId,
            @Param("roomId") Integer roomId,
            @Param("status") ReservationStatus status,
            @Param("owner") boolean owner
    );
}
