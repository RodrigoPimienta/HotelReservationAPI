package com.revature.dto.response;

import com.revature.models.Reservation;
import com.revature.models.ReservationStatus;

import java.util.Date;

public class ReservationWithDetailsDTO {

    private int reservationId;

    private int totalGuest;

    private Date checkIn;

    private Date checkOut;

    private Double total;

    private String comment;

    private ReservationStatus status;

    private UserDTO user;

    private RoomWithDetailsDTO room;

    public ReservationWithDetailsDTO(){}

    public ReservationWithDetailsDTO(Reservation reservation){
        this.reservationId= reservation.getReservationId();
        this.totalGuest=reservation.getTotalGuest();
        this.checkIn=reservation.getCheckIn();
        this.checkOut=reservation.getCheckOut();
        this.total= reservation.getTotal();
        this.comment=reservation.getComment();
        this.status=reservation.getStatus();
        this.user=new UserDTO(reservation.getUser());
        this.room= new RoomWithDetailsDTO(reservation.getRoom());
    }

    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public int getTotalGuest() {
        return totalGuest;
    }

    public void setTotalGuest(int totalGuest) {
        this.totalGuest = totalGuest;
    }

    public Date getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(Date checkIn) {
        this.checkIn = checkIn;
    }

    public Date getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(Date checkOut) {
        this.checkOut = checkOut;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public RoomWithDetailsDTO getRoom() {
        return room;
    }

    public void setRoom(RoomWithDetailsDTO room) {
        this.room = room;
    }
}
