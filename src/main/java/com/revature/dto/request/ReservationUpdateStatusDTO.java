package com.revature.dto.request;

import com.revature.models.ReservationStatus;

public class ReservationUpdateStatusDTO {
    private ReservationStatus status;

    public ReservationUpdateStatusDTO(){}

    public ReservationUpdateStatusDTO(ReservationStatus status) {
        this.status = status;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }
}
