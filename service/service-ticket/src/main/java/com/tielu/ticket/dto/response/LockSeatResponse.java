package com.tielu.ticket.dto.response;

import lombok.Data;

@Data
public class LockSeatResponse {

    private Long orderId;

    private Boolean success;

    private String message;
}
