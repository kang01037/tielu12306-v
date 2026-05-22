package com.tielu.ticket.dto.response;

import lombok.Data;

import java.util.Map;

@Data
public class SeatAvailabilityResponse {

    private Long trainId;

    private String travelDate;

    private Map<String, Integer> seatAvailability;
}
