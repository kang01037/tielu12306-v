package com.tielu.order.dto.response;

public class SeatAvailabilityResponse {

    private Long trainId;

    private String travelDate;

    private String seatType;

    private Integer totalSeats;

    private Integer availableSeats;

    private Integer soldSeats;

    public Long getTrainId() { return trainId; }
    public void setTrainId(Long trainId) { this.trainId = trainId; }
    public String getTravelDate() { return travelDate; }
    public void setTravelDate(String travelDate) { this.travelDate = travelDate; }
    public String getSeatType() { return seatType; }
    public void setSeatType(String seatType) { this.seatType = seatType; }
    public Integer getTotalSeats() { return totalSeats; }
    public void setTotalSeats(Integer totalSeats) { this.totalSeats = totalSeats; }
    public Integer getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(Integer availableSeats) { this.availableSeats = availableSeats; }
    public Integer getSoldSeats() { return soldSeats; }
    public void setSoldSeats(Integer soldSeats) { this.soldSeats = soldSeats; }
}
