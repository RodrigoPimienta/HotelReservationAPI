package com.revature.dto.request;

import com.revature.models.ReservationStatus;

public class ReservationUpdateStatusDTO {
    private ReservationStatus status;

    private String comment;

    public ReservationUpdateStatusDTO(){}

    public ReservationUpdateStatusDTO(ReservationStatus status, String comment) {
        this.status = status;
        this.comment =comment;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
